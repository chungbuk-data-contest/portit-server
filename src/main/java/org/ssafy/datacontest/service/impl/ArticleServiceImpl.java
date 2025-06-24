package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.dto.SliceResponseDto;
import org.ssafy.datacontest.dto.article.*;
import org.ssafy.datacontest.dto.image.ImageDto;
import org.ssafy.datacontest.dto.image.ImageUpdateDto;
import org.ssafy.datacontest.dto.tag.TagDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Image;
import org.ssafy.datacontest.entity.Tag;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.Category;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ArticleMapper;
import org.ssafy.datacontest.mapper.ImageMapper;
import org.ssafy.datacontest.mapper.TagMapper;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.ImageRepository;
import org.ssafy.datacontest.repository.TagRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.ArticleService;
import org.ssafy.datacontest.service.S3FileService;
import org.ssafy.datacontest.validation.ArticleValidation;

import java.security.MessageDigest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final S3FileService s3FileService;
    private final ArticleValidation articleValidation;

    @Transactional
    @Override
    public Long createArticle(ArticleRequestDto articleRequestDto, String userName) throws Exception {
        User user = userRepository.findByLoginId(userName);

        articleValidation.isValidRequest(articleRequestDto); // null 여부 처리

        List<MultipartFile> files = articleRequestDto.getFiles();
        MultipartFile thumbnail = articleRequestDto.getThumbnail();

        // 썸네일 파일과 파일 리스트 - 해시값 비교 => 다를 경우에만 S3 저장 필요
        int index = isDuplicateThumbnail(thumbnail, files);

        String thumbnailUrl = "";
        if(index == -1) { // 썸네일 따로 저장 필요
            thumbnailUrl = uploadFile(thumbnail);
        }

        // 이미지, 영상 업로드
        List<String> fileUrls = new ArrayList<>();

        int loop = 0;
        for(MultipartFile file : files){
            String fileUrl = uploadFile(file);
            if(index != -1 && loop == index) {
                thumbnailUrl = fileUrl;
            }
            fileUrls.add(fileUrl);
            loop++;
        }

        // 글 DB 등록
        Article article = ArticleMapper.toEntity(articleRequestDto, user, thumbnailUrl);
        articleRepository.save(article);

        // List들 DB 등록
        saveTag(articleRequestDto.getTag(), article);
        saveFile(fileUrls, article);

        return article.getArtId();
    }

    @Override
    @Transactional
    public Long updateArticle(ArticleUpdateRequestDto articleRequestDto, String userName, Long articleId, List<ImageUpdateDto> imageIdList) throws Exception {
        User user = userRepository.findByLoginId(userName);
        Article article = getArticleOrThrow(articleId);
        articleRequestDto.setImageIdList(imageIdList);

        articleValidation.checkUserAuthorizationForArticle(user, article);
        articleValidation.isValidRequest(articleRequestDto);

        // 이미지 수정 혹은 순서 변경 반영
        List<Image> existingFile = imageRepository.findByArticle(article);

        // 삭제된 이미지 찾아서 삭제
        deleteImagesNotInRequest(articleRequestDto, existingFile);

        // 기존에 남아있는 이미지 -> 순서 바뀐 경우 수정
        updateImageOrder(articleRequestDto);


        // 새로 추가한 이미지 db & s3 저장
        List<String> newFiles = saveNewFile(articleRequestDto, article);

        MultipartFile thumbnailFile = articleRequestDto.getNewThumbnailImage();

        String thumbnailUrl = "";
        if (thumbnailFile != null) { // 썸네일사진 - 파일들 중복 확인
            int index = isDuplicateThumbnail(thumbnailFile, articleRequestDto.getFiles());

            if (index == -1) { // 중복 X -> S3 업로드
                thumbnailUrl = uploadFile(thumbnailFile);
            } else { // 파일들에서 가져오기
                thumbnailUrl = newFiles.get(index); // newFiles도 null 체크 되어야 안전
            }
        } else if (articleRequestDto.getThumbnailUrl() != null) { // 기존 사진일 경우
            thumbnailUrl = articleRequestDto.getThumbnailUrl();
        }

        log.info("Thumbnail url is {}", thumbnailUrl);

        // 태그 삭제
        tagRepository.deleteByArticle(article);
        saveTag(articleRequestDto.getTag(), article);

        // 나머지 업데이트
        article.updateArticle(articleRequestDto.getTitle(), articleRequestDto.getDescription(), articleRequestDto.getExternalLink(), Category.valueOf(articleRequestDto.getCategory()), thumbnailUrl);

        return articleId;
    }

    @Override
    @Transactional
    public void deleteArticle(Long articleId, String userName) {
        // 유저 가져오기
        User user = userRepository.findByLoginId(userName);

        // articleId 존재 여부 확인
        Article article = getArticleOrThrow(articleId);

        // 권한 확인 ( ==, != 는 객체 주소(참조)로 비교하기에 다를 수 있음 )
        articleValidation.checkUserAuthorizationForArticle(user, article);

        // 파일 조회 => s3 삭제
        List<Image> images = imageRepository.findByArticle(article);
        deleteFile(images);

        imageRepository.deleteByArticle(article);
        tagRepository.deleteByArticle(article);

        // 썸네일 s3 존재할 경우 삭제
        deleteImageUrl(article.getThumbnailUrl());
        articleRepository.deleteById(articleId); // 글 삭제
    }

    @Override
    public ArticleResponseDto getArticle(Long articleId) {
        Article article = getArticleOrThrow(articleId);
        User user = article.getUser();

        List<TagDto> tagDtos = getTagDto(article);
        List<ImageDto> fileDtos = getImageDto(article);

        return ArticleMapper.toArticleResponseDto(article, fileDtos, tagDtos, user);
    }

    @Override
    public SliceResponseDto<ArticlesScrollResponse> getArticlesByCursor(ArticleScrollRequestDto articleScrollRequestDto) {
        // 일반 게시글 처리
        Slice<Article> articles = articleRepository.findNextPageByCursor(articleScrollRequestDto);
        List<Article> articleList = articles.getContent();

        List<Long> articleIds = articleList.stream()
                .map(Article::getArtId)
                .toList();

        Map<Long, List<String>> tagMap = getTagMapByArticleIds(articleIds);
        List<ArticlesResponseDto> dtoList = mapArticlesToDtoList(articleList, tagMap);

        // 프리미엄 게시글 처리
        List<ArticlesResponseDto> premiumDtoList = List.of();
        if (Boolean.TRUE.equals(articleScrollRequestDto.getIsFirstPage())) {
            List<Article> premiumArticles = articleRepository.findRandomPremiumArticles(4);
            List<Long> premiumIds = premiumArticles.stream()
                    .map(Article::getArtId)
                    .toList();

            Map<Long, List<String>> premiumTagMap = getTagMapByArticleIds(premiumIds);
            premiumDtoList = mapArticlesToDtoList(premiumArticles, premiumTagMap);
        }

        ArticlesScrollResponse articlesScrollResponse = ArticlesScrollResponse.builder()
                .premiumArticles(premiumDtoList)
                .articles(dtoList)
                .build();

        return new SliceResponseDto<>(List.of(articlesScrollResponse), articles.hasNext());
    }

    private List<ArticlesResponseDto> mapArticlesToDtoList(List<Article> articles, Map<Long, List<String>> tagMap) {
        return articles.stream()
                .map(article -> ArticleMapper.toArticlesResponseDto(
                        article,
                        tagMap.getOrDefault(article.getArtId(), List.of())
                ))
                .toList();
    }

    private Map<Long, List<String>> getTagMapByArticleIds(List<Long> articleIds) {
        List<Object[]> rawTagData = tagRepository.findTagsByArticleIds(articleIds);

        return rawTagData.stream()
                .collect(Collectors.groupingBy(
                        row -> ((Number) row[0]).longValue(),
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));
    }

    private String uploadFile(MultipartFile file) {
        return s3FileService.uploadFile(file);
    }

    private void saveTag(List<String> tags, Article article) {
        for(String tag: tags){
            Tag tagg = TagMapper.toEntity(tag, article);
            tagRepository.save(tagg);
        }
    }

    private void saveFile(List<String> fileUrls, Article article) {
        int index = 0;
        for(String fileUrl : fileUrls){
            Image file = ImageMapper.toEntity(fileUrl, article, index);
            imageRepository.save(file);
            index++;
        }
    }

    private void deleteFile(List<Image> fileUrls) {
        for(Image file : fileUrls){
            s3FileService.deleteFile(file.getImageUrl());
        }
    }

    private void deleteImageUrl(String fileUrl) {
        s3FileService.deleteFile(fileUrl);
    }

    private Article getArticleOrThrow(Long articleId) {
        Article article = articleRepository.findByArtId(articleId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.ARTICLE_NOT_FOUND));

        return article;
    }

    private List<TagDto> getTagDto(Article article) {
        List<Tag> tagList = tagRepository.findByArticle(article);
        List<TagDto> tagDtos = new ArrayList<>();
        for(Tag tag : tagList){
            tagDtos.add(TagMapper.toDto(tag));
        }

        return tagDtos;
    }

    private List<ImageDto> getImageDto(Article article) {
        List<Image> fileList = imageRepository.findByArticle(article)
                .stream()
                .sorted(Comparator.comparingInt(Image::getImageIndex))
                .toList();

        List<ImageDto> imageDtos = new ArrayList<>();
        for (Image image : fileList) {
            imageDtos.add(ImageMapper.toDto(image));
        }
        return imageDtos;
    }

    private void deleteImagesNotInRequest(ArticleUpdateRequestDto articleRequestDto, List<Image> existingFile) {
        // imageIdList에서 null이 아닌 id만 추출해서 Set으로 변환
        Set<Long> incomingIds = articleRequestDto.getImageIdList().stream()
                .map(ImageUpdateDto::getImageId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 기존 이미지 중 요청에 포함되지 않은 이미지만 삭제 대상
        List<Image> toDeleteImage = existingFile.stream()
                .filter(img -> !incomingIds.contains(img.getImageId()))
                .toList();

        // 삭제
        for (Image image : toDeleteImage) {
            imageRepository.deleteByImageId(image.getImageId());
        }
        deleteFile(toDeleteImage);
    }

    private void updateImageOrder(ArticleUpdateRequestDto articleRequestDto) {
        // 1. 유효한 imageId만 필터링해서 DB 조회
        List<Image> existingImages = imageRepository.findByImageIdIn(
                articleRequestDto.getImageIdList().stream()
                        .filter(dto -> dto.getImageId() != null)  // imageId가 null이 아닌 경우만
                        .map(ImageUpdateDto::getImageId)
                        .toList()
        );

        // 2. imageId → Image 매핑
        Map<Long, Image> imageMap = existingImages.stream()
                .collect(Collectors.toMap(Image::getImageId, Function.identity()));

        // 3. 순서 업데이트
        for (int i = 0; i < articleRequestDto.getImageIdList().size(); i++) {
            Long imageId = articleRequestDto.getImageIdList().get(i).getImageId();

            if (imageId == null) continue; // 새 이미지인 경우

            Image image = imageMap.get(imageId);
            if (image != null && image.getImageIndex() != i) {
                image.updateImageIndex(i);
            }
        }
    }

    private List<String> saveNewFile(ArticleUpdateRequestDto articleRequestDto, Article article) {
        List<MultipartFile> files = articleRequestDto.getFiles();
        List<String> uploadedUrls = new ArrayList<>();

        int fileIndex = 0;

        for (int i = 0; i < articleRequestDto.getImageIdList().size(); i++) {
            ImageUpdateDto dto = articleRequestDto.getImageIdList().get(i);

            if (dto.getImageId() == null) { // 새 이미지
                String url = uploadFile(files.get(fileIndex++));
                uploadedUrls.add(url);
                Image image = ImageMapper.toEntity(url, article, i);
                imageRepository.save(image);
            }
        }

        return uploadedUrls;
    }

    private String getHash(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = file.getBytes();
        byte[] hash = digest.digest(bytes);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    private int isDuplicateThumbnail(MultipartFile thumbnail, List<MultipartFile> files) throws Exception {
        if (thumbnail == null || files == null || files.isEmpty()) {
            return -1;
        }

        String thumbnailHash = getHash(thumbnail);

        for (int i = 0; i < files.size(); i++) {
            String fileHash = getHash(files.get(i));
            if (fileHash.equals(thumbnailHash)) {
                return i;
            }
        }

        return -1;
    }

}
