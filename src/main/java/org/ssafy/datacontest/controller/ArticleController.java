package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.article.ArticleRequestDto;
import org.ssafy.datacontest.service.ArticleService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name="Article", description = "작품 관련 API")
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("") // 객체 보내주기
    @Operation(
            summary = "작품 등록",
            description = "작품을 등록하고 작품 번호를 리턴합니다."
    )
    public ResponseEntity<?> registerArticle(@ModelAttribute @Valid ArticleRequestDto request){
        Long articleId = articleService.createArticle(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(articleId);
    }

    @DeleteMapping("/{articleId}")
    @Operation(
            summary = "작품 삭제",
            description = "작품 ID를 통해 해당 작품을 삭제합니다."
    )
    public ResponseEntity<String> deleteArticle(@PathVariable("articleId") Long articleId) {
        articleService.deleteArticle(articleId);
        return ResponseEntity.status(HttpStatus.OK).body("작품이 정상적으로 삭제되었습니다.");
    }

}