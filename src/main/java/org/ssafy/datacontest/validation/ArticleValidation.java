package org.ssafy.datacontest.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.dto.article.ArticleRequestDto;
import org.ssafy.datacontest.dto.article.ArticleUpdateRequestDto;
import org.ssafy.datacontest.dto.image.ImageUpdateDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.Category;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;

import java.util.List;

@Component
public class ArticleValidation {

    public void checkUserAuthorizationForArticle(User user, Article article){
        if(!article.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_USER);
        }
    }

    public void isValidRequest(Object request) {
        if (request instanceof ArticleRequestDto dto) {
            isValidTitle(dto.getTitle());
            isValidCategory(dto.getCategory());
            isValidCategoryName(dto.getCategory());
            isValidFile(dto.getFiles());
            validateTagCount(dto.getTag());
            isValidTag(dto.getTag());
        } else if (request instanceof ArticleUpdateRequestDto dto) {
            isValidTitle(dto.getTitle());
            isValidCategory(dto.getCategory());
            isValidCategoryName(dto.getCategory());
            isValidFile(dto.getFiles());
            validateTagCount(dto.getTag());
            isValidTag(dto.getTag());
            validateImageListAndFileSize(dto.getImageIdList(), dto.getFiles());
        }
    }

    private void validateTagCount(List<String> tagList) {
        if(tagList.size() > 2) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TAG);
        }
    }

    private void isValidTag(List<String> tagList){
        if (tagList == null || tagList.isEmpty()) return;

        for (String tag : tagList) {
            if (tag.length() != 4) {
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TAG_LENGTH);
            }
        }
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

    private void validateImageListAndFileSize(List<ImageUpdateDto> imageIdList, List<MultipartFile> fileIdList){
        long newImageCount = imageIdList.stream()
                .filter(dto -> dto.getImageId() == null)
                .count();

        if (newImageCount != fileIdList.size()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISMATCH_IMAGE_COUNT);
        }
    }
}
