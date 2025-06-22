package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.SliceResponseDto;
import org.ssafy.datacontest.dto.article.ArticleRequestDto;
import org.ssafy.datacontest.dto.article.ArticleScrollRequestDto;
import org.ssafy.datacontest.dto.article.ArticlesResponseDto;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.enums.Category;
import org.ssafy.datacontest.enums.SortType;
import org.ssafy.datacontest.service.ArticleService;
import org.ssafy.datacontest.service.impl.CustomUserDetailsService;

import java.util.List;

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
    public ResponseEntity<?> registerArticle(@ModelAttribute @Valid ArticleRequestDto request, @AuthenticationPrincipal CustomUserDetails userDetails){
        String userName = userDetails.getUsername();
        Long articleId = articleService.createArticle(request, userName);

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

    @GetMapping("")
    @Operation(
            summary = "작품 조회",
            description = "검색어, 카테고리, 정렬 방식, 페이지 번호 등의 파라미터를 통해 작품 목록을 조회합니다. \n" +
                    "파라미터 없이 요청 시 전체 작품을 반환합니다."
    )
    // category 하나만 들어왔을 때 인식이 안 되는 문제 -> RequestParam 으로 받아서 직접 넣어주기
    public ResponseEntity<SliceResponseDto<ArticlesResponseDto>> getArticles(
            @RequestParam(required = false) List<Category> category,
            @ModelAttribute ArticleScrollRequestDto request
    ) {
        request.setCategory(category);  // 수동으로 세팅

        // 검색 시, 강제 최신순 정렬
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            request.setSortType(SortType.LATEST);
        }

        return ResponseEntity.ok(articleService.getArticlesByCursor(request));
    }

}