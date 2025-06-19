package org.ssafy.datacontest.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.dto.article.*;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Image;
import org.ssafy.datacontest.entity.Tag;
import org.ssafy.datacontest.enums.Category;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ArticleMapper;
import org.ssafy.datacontest.mapper.ImageMapper;
import org.ssafy.datacontest.mapper.TagMapper;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.ImageRepository;
import org.ssafy.datacontest.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;
    private final S3FileService s3FileService;

    @Transactional
    public Long createArticle(ArticleRequestDto articleRequestDto) {
        // TODO: 유저 인증

        isValidRequest(articleRequestDto); // null 여부 처리

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

    private void isValidRequest(ArticleRequestDto request) {
        isValidTitle(request.getTitle());
        isValidCategory(request.getCategory());
        isValidCategoryName(request.getCategory());
        isValidFile(request.getFiles());
        isValidTag(request.getTag());
    }

    private void isValidTitle(String title){
        if(title == null || title.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_TITLE);
        }
    }

    private void isValidCategory(String category){
        if(category == null || category.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_CATEGORY);
        }
    }

    private void isValidCategoryName(String category){
        try{
            Category.valueOf(category);
        } catch (IllegalArgumentException e){
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_CATEGORY);
        }
    }

    private void isValidTag(List<String> tag){
        if(tag == null || tag.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_TAG);
        }
    }

    private void isValidFile(List<MultipartFile> file){
        if(file == null || file.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_FILE);
        }

        // MultipartFile 은 폼 필드만 있을 경우에도 리스트 생성 => files 안의 모든 파일이 isEmpty 인지 확인
        boolean allEmpty = file.stream().allMatch(MultipartFile::isEmpty);
        if (allEmpty) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_FILE);
        }
    }

}
