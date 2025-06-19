package org.ssafy.datacontest.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.dto.article.ArticleRequestDto;
import org.ssafy.datacontest.enums.Category;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;

import java.util.List;

@Component
public class ArticleValidation {

    public void isValidRequest(ArticleRequestDto request) {
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
