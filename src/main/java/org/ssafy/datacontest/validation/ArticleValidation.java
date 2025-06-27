package org.ssafy.datacontest.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.dto.article.ArticleRequest;
import org.ssafy.datacontest.dto.article.ArticleUpdateRequest;
import org.ssafy.datacontest.dto.image.ImageUpdateDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.Category;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;

import java.util.List;

@Slf4j
@Component
public class ArticleValidation {

    public void validateUpdateRequest(ArticleUpdateRequest articleRequestDto, User user, Article article) {
        checkUserAuthorizationForArticle(user, article);
        validRequest(articleRequestDto);
        validateNotDeleted(article);
    }

    public void checkUserAuthorizationForArticle(User user, Article article){
        if(!article.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_USER);
        }
    }

    public void validRequest(Object request) {
        if (request instanceof ArticleRequest dto) {
            validTitle(dto.getTitle());
            validDescription(dto.getDescription());
            validCategory(dto.getCategory());
            validCategoryName(dto.getCategory());
            validFile(dto.getFiles());
            validateTagCount(dto.getTag());
            validTag(dto.getTag());
            validThumbnail(dto.getThumbnail());
        } else if (request instanceof ArticleUpdateRequest dto) {
            validTitle(dto.getTitle());
            validDescription(dto.getDescription());
            validCategory(dto.getCategory());
            validCategoryName(dto.getCategory());
            validateTagCount(dto.getTag());
            validTag(dto.getTag());
            validateImageListAndFileSize(dto.getImageIdList(), dto.getFiles());
            validNewOrExistingThumbnail(dto.getNewThumbnailImage(), dto.getThumbnailUrl());
        }
    }

    private void validateTagCount(List<String> tagList) {
        if(tagList.size() > 2) {
            log.info("태그 개수 초과: {}", tagList.size());
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TAG_COUNT);
        }
    }

    private void validTag(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) return;

        for (String tag : tagList) {
            if (tag.charAt(0) != '#') throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TAG_TYPE);
            if (tag.length() > 5) {
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TAG_LENGTH);
            }
        }
    }

    private void validTitle(String title){
        if(title == null || title.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_TITLE);
        }
    }

    private void validDescription(String description){
        if(description == null || description.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_DESCRIPTION);
        }
    }

    private void validCategory(String category){
        if(category == null || category.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_CATEGORY);
        }
    }

    private void validThumbnail(MultipartFile multipartFile){
        if(multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_THUMBNAIL);
        }
    }

    private void validCategoryName(String category){
        try{
            Category.valueOf(category);
        } catch (IllegalArgumentException e){
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_CATEGORY);
        }
    }

    private void validFile(List<MultipartFile> file){
        if(file == null || file.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_FILE);
        }

        // MultipartFile 은 폼 필드만 있을 경우에도 리스트 생성 => files 안의 모든 파일이 isEmpty 인지 확인
        boolean allEmpty = file.stream().allMatch(MultipartFile::isEmpty);
        if (allEmpty) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_FILE);
        }
    }

    private void validateImageListAndFileSize(List<ImageUpdateDto> imageIdList, List<MultipartFile> fileIdList){
        long newImageCount = imageIdList.stream()
                .filter(dto -> dto.getImageId() == null)
                .count();

        long fileCount = (fileIdList == null) ? 0 : fileIdList.size();

        if (newImageCount != fileCount) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISMATCH_IMAGE_COUNT);
        }
    }

    private void validNewOrExistingThumbnail(MultipartFile newThumbnailImage, String newThumbnailUrl) {
        if( (newThumbnailImage == null || newThumbnailImage.isEmpty()) && (newThumbnailUrl == null || newThumbnailUrl.isEmpty()) ) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_THUMBNAIL);
        }
    }

    public void validateNotDeleted(Article article) {
        if(article.isDeleted()){
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.DELETED_ARTICLE);
        }
    }
}
