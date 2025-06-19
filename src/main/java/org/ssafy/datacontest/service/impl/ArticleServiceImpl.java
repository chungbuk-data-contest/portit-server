package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.dto.article.ArticleRequestDto;
import org.ssafy.datacontest.dto.article.ArticleResponseDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Image;
import org.ssafy.datacontest.entity.Tag;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ArticleMapper;
import org.ssafy.datacontest.mapper.ImageMapper;
import org.ssafy.datacontest.mapper.TagMapper;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.ImageRepository;
import org.ssafy.datacontest.repository.TagRepository;
import org.ssafy.datacontest.service.ArticleService;
import org.ssafy.datacontest.service.S3FileService;
import org.ssafy.datacontest.validation.ArticleValidation;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;
    private final S3FileService s3FileService;
    private final ArticleValidation articleValidation;

    @Transactional
    @Override
    public Long createArticle(ArticleRequestDto articleRequestDto) {
        // TODO: 유저 인증

        articleValidation.isValidRequest(articleRequestDto); // null 여부 처리

        // 이미지, 영상 업로드
        List<String> fileUrls = new ArrayList<>();
        for(MultipartFile file : articleRequestDto.getFiles()){
            String fileUrl = uploadFile(file);
            fileUrls.add(fileUrl);
        }

        // 글 DB 등록
        Article article = ArticleMapper.toEntity(articleRequestDto);
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
    public void deleteArticle(Long articleId) {
        // TODO: 유저 확인 / 권한 확인

        // articleId 존재 여부 확인
        Article article = articleRepository.findByArtId(articleId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.ARTICLE_NOT_FOUND));

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
    public List<ArticleResponseDto> getArticles() {
        return List.of();
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
        for(String fileUrl : fileUrls){
            Image file = ImageMapper.toEntity(fileUrl, article);
            imageRepository.save(file);
        }
    }

    private void deleteFile(List<Image> fileUrls) {
        for(Image file : fileUrls){
            s3FileService.deleteFile(file.getImageUrl());
        }
    }
}
