package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.dto.article.ArticleRequestDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Image;
import org.ssafy.datacontest.entity.Tag;
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
}
