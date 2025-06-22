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
    public Long createArticle(ArticleRequestDto articleRequestDto, String userName) {
        User user = userRepository.findByLoginId(userName);

        articleValidation.isValidRequest(articleRequestDto); // null 여부 처리

        // 이미지, 영상 업로드
        List<String> fileUrls = new ArrayList<>();
        for(MultipartFile file : articleRequestDto.getFiles()){
            String fileUrl = uploadFile(file);
            fileUrls.add(fileUrl);
        }

        // 글 DB 등록
        Article article = ArticleMapper.toEntity(articleRequestDto, user);
        articleRepository.save(article);

        // List들 DB 등록
        saveTag(articleRequestDto.getTag(), article);
        saveFile(fileUrls, article);

        return article.getArtId();
    }

    @Override
    @Transactional
    public Long updateArticle(ArticleUpdateRequestDto articleRequestDto, String userName, Long articleId, List<ImageUpdateDto> imageIdList) {
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
        saveNewFile(articleRequestDto, article);

        // 태그 삭제
        tagRepository.deleteByArticle(article);
        saveTag(articleRequestDto.getTag(), article);

        // 나머지 업데이트
        article.updateArticle(articleRequestDto.getTitle(), articleRequestDto.getDescription(), articleRequestDto.getExternalLink(), Category.valueOf(articleRequestDto.getCategory()));

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
    public SliceResponseDto<ArticlesResponseDto> getArticlesByCursor(ArticleScrollRequestDto articleScrollRequestDto) {
        Slice<Article> articles = articleRepository.findNextPageByCursor(articleScrollRequestDto);
        List<Article> articleList = articles.getContent();

        // articleId 리스트 추출
        List<Long> articleIds = articleList.stream()
                .map(Article::getArtId)
                .toList();

        // 썸네일 이미지 조회: articleId별로 첫 번째 이미지
        List<Object[]> rawList = imageRepository.findFirstImageUrlsByArticleIds(articleIds);

        Map<Long, String> thumbnailMap = rawList.stream()
                .filter(row -> row[0] != null && row[1] != null)
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> (String) row[1]
                ));

        // 태그 조회: articleId별로 List<Tag>
        List<Object[]> rawTagData = tagRepository.findTagsByArticleIds(articleIds);

        Map<Long, List<String>> tagMap = rawTagData.stream()
                .collect(Collectors.groupingBy(
                        row -> ((Number) row[0]).longValue(),
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));

        // DTO 변환
        List<ArticlesResponseDto> dtoList = articleList.stream()
                .map(article -> ArticleMapper.toArticlesResponseDto(
                        article,
                        thumbnailMap.get(article.getArtId()),
                        tagMap.getOrDefault(article.getArtId(), List.of())
                ))
                .toList();

        return new SliceResponseDto<>(dtoList, articles.hasNext());
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

    private void saveNewFile(ArticleUpdateRequestDto articleRequestDto, Article article) {
        List<MultipartFile> files = articleRequestDto.getFiles();

        int fileIndex = 0; // 새 이미지만큼만 files에서 꺼내기

        for (int i = 0; i < articleRequestDto.getImageIdList().size(); i++) {
            ImageUpdateDto dto = articleRequestDto.getImageIdList().get(i);

            // 새 이미지인 경우 (imageId == null)
            if (dto.getImageId() == null) {
                String url = uploadFile(files.get(fileIndex++)); // 순서 주의
                Image image = ImageMapper.toEntity(url, article, i); // 현재 위치 i가 index
                imageRepository.save(image);
            }
        }
    }
}
