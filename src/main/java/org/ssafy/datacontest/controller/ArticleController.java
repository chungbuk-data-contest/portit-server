package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.dto.article.ArticleRequestDto;
import org.ssafy.datacontest.dto.article.ArticleResponseDto;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.service.ArticleService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name="Article", description = "작품 관련 API")
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("") // 객체 보내주기
    public ResponseEntity<ArticleResponseDto> registerArticle(@ModelAttribute @Valid ArticleRequestDto request, BindingResult bindingResult) throws CustomException {

        log.info(request.getCategory());
        log.info(request.getTitle());
        log.info(request.getTag() == null ? "true" : "false");
        log.info(request.getImageFiles() == null ? "true" : "false");

//        if (bindingResult.hasErrors()) {
//            StringBuilder messageBuilder = new StringBuilder();
//
//            bindingResult.getFieldErrors().forEach(error ->
//                    messageBuilder.append("[")
//                            .append(error.getField())
//                            .append("] ")
//                            .append(error.getDefaultMessage())
//                            .append("; ")
//            );
//
//            throw new CustomException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", messageBuilder.toString().trim());
//        }

        ArticleResponseDto articleResponseDto = articleService.registerArtwork(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(articleResponseDto);
    }

}