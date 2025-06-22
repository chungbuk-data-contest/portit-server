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
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Image;
import org.ssafy.datacontest.entity.Tag;
import org.ssafy.datacontest.entity.User;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        User user = userRepository.findByEmail(userName);

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
    public Long updateArticle(ArticleRequestDto articleRequestDto) {
        return 0L;
    }

    @Override
    @Transactional
    public void deleteArticle(Long articleId, String userName) {
        // TODO: 유저 확인 / 권한 확인
        User user = userRepository.findByEmail(userName);

        // articleId 존재 여부 확인
        Article article = articleRepository.findByArtId(articleId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.ARTICLE_NOT_FOUND));

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
        return null;
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
}
