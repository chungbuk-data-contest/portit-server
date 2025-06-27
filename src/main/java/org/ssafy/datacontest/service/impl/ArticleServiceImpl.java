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
import org.ssafy.datacontest.dto.company.CompanyRecommendDto;
import org.ssafy.datacontest.dto.gpt.GptRequest;
import org.ssafy.datacontest.dto.image.ImageDto;
import org.ssafy.datacontest.dto.image.ImageUpdateDto;
import org.ssafy.datacontest.dto.tag.TagDto;
import org.ssafy.datacontest.entity.*;
import org.ssafy.datacontest.enums.Category;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.enums.IndustryType;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ArticleMapper;
import org.ssafy.datacontest.mapper.ImageMapper;
import org.ssafy.datacontest.repository.*;
import org.ssafy.datacontest.service.ArticleService;
import org.ssafy.datacontest.service.helper.*;
import org.ssafy.datacontest.util.GptUtil;
import org.ssafy.datacontest.validation.ArticleValidation;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ArticleValidation articleValidation;
    private final CompanyRepository companyRepository;
    private final GptUtil gptUtil;
    private final ArticleLikeRepository articleLikeRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ImageHelper imageHelper;
    private final GptHelper gptHelper;
    private final ThumbnailHelper thumbnailHelper;
    private final ArticleAssembler articleAssembler;
    private final TagHelper tagHelper;

    // ====== Create ======

    @Transactional
    @Override
    public Long createArticle(ArticleRequest articleRequest, String userName) throws Exception {
        User user = findUser(userName);
        articleValidation.validRequest(articleRequest);

        List<MultipartFile> files = articleRequest.getFiles();
        MultipartFile thumbnail = articleRequest.getThumbnail();

        List<String> fileUrls = imageHelper.uploadFiles(files);

        // 썸네일 파일과 파일 리스트 - 해시값 비교 => 다를 경우에만 S3 저장 필요
        int index = thumbnailHelper.isDuplicateThumbnail(thumbnail, files);
        String thumbnailUrl = (index == -1) ? imageHelper.uploadFile(thumbnail) : fileUrls.get(index);

        // DB 등록
        Article article = createAndSaveArticle(articleRequest, user, thumbnailUrl);
        saveAdditionalArticleData(articleRequest, article, fileUrls);

        return article.getArtId();
    }

    // ====== Update ======

    @Override
    @Transactional
    public Long updateArticle(ArticleUpdateRequest articleRequestDto, String userName, Long articleId, List<ImageUpdateDto> imageIdList) throws Exception {
        User user = findUser(userName);
        Article article = getArticleOrThrow(articleId);
        articleRequestDto.setImageIdList(imageIdList);

        articleValidation.validateUpdateRequest(articleRequestDto, user, article);

        updateArticleImages(articleRequestDto, article);
        tagHelper.updateArticleTags(articleRequestDto, article);
        updateArticleInfo(articleRequestDto, article);

        return articleId;
    }

    // ====== Delete ======

    @Override
    @Transactional
    public void deleteArticle(Long articleId, String userName) {
        User user = findUser(userName);
        Article article = getArticleOrThrow(articleId);

        // 권한 확인 ( ==, != 는 객체 주소(참조)로 비교하기에 다를 수 있음 )
        articleValidation.checkUserAuthorizationForArticle(user, article);

        article.setDeleted(true);
    }

    // ====== Get ======

    @Override
    public ArticleDetailResponse getArticle(Long articleId, String userName) {
        Article article = getArticleOrThrow(articleId);
        User user = article.getUser();

        articleValidation.validateNotDeleted(article);

        boolean liked = isArticleLikedByUser(articleId, userName);

        List<TagDto> tagDtos = articleAssembler.getTagDto(article);
        List<ImageDto> fileDtos = articleAssembler.getImageDto(article);

        ArticleResponse articleDto = ArticleMapper.toArticleResponseDto(article, fileDtos, tagDtos, user);
        List<CompanyRecommendDto> companyDtoList = articleAssembler.getRecommendedCompanies(article);

        return ArticleDetailResponse.builder().article(articleDto).companies(companyDtoList).liked(liked).build();
    }

    @Override
    public SliceResponseDto<ArticleListResponse> getArticlesByCursor(ArticleScrollRequest articleScrollRequest) {
        Slice<Article> articles = articleRepository.findNextPageByCursor(articleScrollRequest);
        List<Article> articleList = articles.getContent();

        List<Long> articleIds = articleList.stream()
                .map(Article::getArtId)
                .toList();

        Map<Long, List<String>> tagMap = articleAssembler.getTagMapByArticleIds(articleIds);
        List<ArticleListResponse> dtoList = articleAssembler.mapArticlesToDtoList(articleList, tagMap);

        return new SliceResponseDto<>(dtoList, articles.hasNext());
    }

    @Override
    public List<MyArticlesResponse> getMyArticles(String userName, Long companyId) {
        User user = userRepository.findByLoginId(userName);
        if(user == null) throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND);

        Company company = companyRepository.findByCompanyId(companyId);
        if(company == null) throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.COMPANY_NOT_FOUND);

        List<Article> articles = articleRepository.findByUser_Id(user.getId());
        if (articles == null || articles.isEmpty()) {
            return Collections.emptyList();
        }

        return articles.stream()
                .map(article -> {
                    boolean isChatting = hasChatRoom(article.getArtId(), companyId);
                    return ArticleMapper.toMyArticleResponse(article, isChatting);
                })
                .toList();
    }

    @Override
    public List<String> generateTags(GptRequest gptRequest) {
        String prompt = String.format("""
            다음 작품 설명을 기반으로 4글자의 태그 2개를 생성해줘. 해시태그 기호 없이, 콤마로 구분해서 응답해줘.
            작품 설명: %s
            """, gptRequest.getDescription());

        String response = gptUtil.callGpt(prompt);
        return Arrays.stream(response.split(","))
                .map(String::trim)
                .toList();
    }

    private boolean hasChatRoom(Long articleId, Long companyId){
        return chatRoomRepository.findByArticle_ArtIdAndCompany_CompanyId(articleId, companyId) != null;
    }

    private User findUser(String userName) {
        return userRepository.findByLoginId(userName);
    }

    private Article createAndSaveArticle(ArticleRequest dto, User user, String thumbnailUrl) {
        String industry = gptHelper.generateIndustry(new GptRequest(dto.getDescription()));
        Article article = ArticleMapper.toEntity(dto, user, thumbnailUrl, IndustryType.valueOf(industry));
        articleRepository.save(article);
        return article;
    }

    private void saveAdditionalArticleData(ArticleRequest dto, Article article, List<String> fileUrls) {
        tagHelper.saveTag(dto.getTag(), article);
        saveFile(fileUrls, article);
    }

    private void saveFile(List<String> fileUrls, Article article) {
        int index = 0;
        for(String fileUrl : fileUrls){
            Image file = ImageMapper.toEntity(fileUrl, article, index);
            imageRepository.save(file);
            index++;
        }
    }

    private Article getArticleOrThrow(Long articleId) {
        Article article = articleRepository.findByArtId(articleId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.ARTICLE_NOT_FOUND));

        return article;
    }

    private void updateArticleImages(ArticleUpdateRequest articleRequestDto, Article article) throws Exception {
        // 이미지 관련 처리
        List<Image> existingFile = imageRepository.findByArticle(article);
        imageHelper.updateImages(articleRequestDto, existingFile);

        // 새로 추가한 이미지 db & s3 저장
        List<String> newFiles = imageHelper.saveNewFile(articleRequestDto, article);
        MultipartFile thumbnailFile = articleRequestDto.getNewThumbnailImage();

        String thumbnailUrl = "";

        if (thumbnailFile != null) { // 썸네일사진 - 파일들 중복 확인
            int index = thumbnailHelper.isDuplicateThumbnail(thumbnailFile, articleRequestDto.getFiles());
            thumbnailUrl = (index == -1) ? imageHelper.uploadFile(thumbnailFile) : newFiles.get(index);
        } else if (articleRequestDto.getThumbnailUrl() != null) { // 기존 사진일 경우
            thumbnailUrl = articleRequestDto.getThumbnailUrl();
        }

        article.updateThumbnailUrl(thumbnailUrl);
    }

    private void updateArticleInfo(ArticleUpdateRequest dto, Article article) {
        article.updateArticle(
                dto.getTitle(),
                dto.getDescription(),
                dto.getExternalLink(),
                Category.valueOf(dto.getCategory()),
                article.getThumbnailUrl()
        );
    }

    private boolean isArticleLikedByUser(Long articleId, String userName){
        if(userName == null && userName.isBlank()) return false;
        Company company = companyRepository.findByLoginId(userName);
        if(company == null) return false;

        return articleLikeRepository
                .findByCompany_CompanyIdAndArticle_ArtId(company.getCompanyId(), articleId)
                .isPresent();
    }
}
