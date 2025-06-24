package org.ssafy.datacontest.controller;

import com.amazonaws.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.ssafy.datacontest.dto.article.*;
import org.ssafy.datacontest.dto.gpt.GptRequest;
import org.ssafy.datacontest.dto.image.ImageUpdateDto;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.enums.Category;
import org.ssafy.datacontest.enums.SortType;
import org.ssafy.datacontest.service.ArticleService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name="Article", description = "작품 관련 API")
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;
    private final ObjectMapper objectMapper;

    @PostMapping("") // 객체 보내주기
    @Operation(
            summary = "작품 등록",
            description = "작품을 등록하고 작품 번호를 리턴합니다."
    )
    public ResponseEntity<?> registerArticle(@ModelAttribute @Valid ArticleRequestDto request, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        String userName = userDetails.getUsername();
        Long articleId = articleService.createArticle(request, userName);

        return ResponseEntity.status(HttpStatus.CREATED).body(articleId);
    }

    @DeleteMapping("/{articleId}")
    @Operation(
            summary = "작품 삭제",
            description = "작품 ID를 통해 해당 작품을 삭제합니다."
    )
    public ResponseEntity<String> deleteArticle(@PathVariable("articleId") Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        articleService.deleteArticle(articleId, userDetails.getUsername());
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

    @GetMapping("/premium")
    @Operation(
            summary = "프리미엄 작품 조회",
            description = "프리미엄 작품 랜덤 4개 반환합니다."
    )
    public ResponseEntity<List<ArticlesResponseDto>> getPremiumArticles() {
        return ResponseEntity.ok(articleService.getPremiumArticles());
    }

    @GetMapping("/{articleId}")
    @Operation(
            summary = "작품 상세 조회",
            description = "작품 ID를 통해 해당 작품의 상세 정보를 조회합니다."
    )
    public ResponseEntity<ArticleDetailResponse> getArticle(@PathVariable("articleId") Long articleId) {
        return ResponseEntity.ok(articleService.getArticle(articleId));
    }

    @PutMapping("/{articleId}")
    @Operation(
            summary = "작품 수정",
            description = "작품 ID를 이용해 해당 작품의 정보를 수정할 수 있습니다.\n\n" +
                    "이미지 수정은 아래와 같은 방식으로 전달해주세요:\n" +
                    "- imageList : 전체 이미지 순서를 담은 JSON 문자열\n\n" +
                    "- 기존 이미지는 imageId 값을 포함해 보내고,\n\n" +
                    "- 새로 추가하는 이미지는 imageId를 `null`로 설정해주세요.\n\n" +
                    "- 새 이미지(imageId가 null인 경우)의 개수와 함께 보내는 이미지 파일(MultipartFile)의 개수는 같아야합니다!\n\n" +
                    "- 이미지 순서를 바꾸고 싶다면, 전체 이미지를 새로운 순서대로 imageList에 담아 보내주시면 됩니다."
    )
    public ResponseEntity<Long> updateArticle(
            @PathVariable("articleId") Long articleId,
            @ModelAttribute ArticleUpdateRequestDto request,
            @RequestPart("imageList") String imageListJson,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {

        // JSON 문자열 → List<ImageUpdateDto>
        List<ImageUpdateDto> imageList = objectMapper.readValue(
                imageListJson,
                new TypeReference<>() {}
        );

        return ResponseEntity.ok(articleService.updateArticle(request, userDetails.getUsername(), articleId, imageList));
    }

    @Operation(summary = "작품 기반 태그 생성", description = "작품 설명을 기반으로 2개의 태그를 생성")
    @PostMapping("/tags")
    public ResponseEntity<List<String>> generateTags(@RequestBody GptRequest gptRequest) {
        List<String> tags = articleService.generateTags(gptRequest);
        return ResponseEntity.ok(tags);
    }
}