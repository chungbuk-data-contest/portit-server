package org.ssafy.datacontest.controller;

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
import org.ssafy.datacontest.service.ArticleService;
import org.ssafy.datacontest.util.SecurityUtil;

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
    public ResponseEntity<?> registerArticle(@ModelAttribute @Valid ArticleRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        String userName = SecurityUtil.extractUsername(userDetails);
        Long articleId = articleService.createArticle(request, userName);

        return ResponseEntity.status(HttpStatus.CREATED).body(articleId);
    }

    @GetMapping("/{articleId}")
    @Operation(
            summary = "작품 상세 조회",
            description = "작품 ID를 통해 해당 작품의 상세 정보를 조회합니다."
    )
    public ResponseEntity<ArticleDetailResponse> getArticle(@PathVariable("articleId") Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userName = SecurityUtil.extractUsername(userDetails);

        return ResponseEntity.ok(articleService.getArticle(articleId, userName));
    }

    @GetMapping("")
    @Operation(
            summary = "작품 전체 조회",
            description = "검색어, 카테고리, 정렬 방식, 페이지 번호 등의 파라미터를 통해 작품 목록을 조회합니다. \n" +
                    "파라미터 없이 요청 시 전체 작품을 반환합니다."
    )
    // category 하나만 들어왔을 때 인식이 안 되는 문제 -> RequestParam 으로 받아서 직접 넣어주기
    public ResponseEntity<SliceResponseDto<ArticleListResponse>> getArticles(
            @RequestParam(required = false) List<Category> category,
            @ModelAttribute ArticleScrollRequest request
    ) {
        request.setCategory(category);  // 수동으로 세팅
        return ResponseEntity.ok(articleService.getArticlesByCursor(request));
    }

    @PutMapping("/{articleId}")
    @Operation(
            summary = "작품 수정",
            description = """
        작품 ID를 이용해 해당 작품의 정보를 수정할 수 있습니다.

        이미지 수정은 아래와 같은 방식으로 전달해주세요:
        - imageList : 전체 이미지 순서를 담은 JSON 문자열
        - 기존 이미지는 imageId 값을 포함해 보내고,
        - 새로 추가하는 이미지는 imageId를 null로 설정해주세요.
        - 새 이미지의 개수와 MultipartFile의 개수는 같아야합니다!
        - 이미지 순서를 바꾸고 싶다면, 전체 이미지를 새로운 순서대로 보내주세요.
        """
    )
    public ResponseEntity<Long> updateArticle(
            @PathVariable("articleId") Long articleId,
            @ModelAttribute ArticleUpdateRequest request,
            @RequestPart("imageList") String imageListJson,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        String userName = SecurityUtil.extractUsername(userDetails);

        // JSON 문자열 → List<ImageUpdateDto>
        List<ImageUpdateDto> imageList = objectMapper.readValue(
                imageListJson,
                new TypeReference<>() {}
        );

        return ResponseEntity.ok(articleService.updateArticle(request, userName, articleId, imageList));
    }

    @DeleteMapping("/{articleId}")
    @Operation(
            summary = "작품 삭제",
            description = "작품 ID를 통해 해당 작품을 삭제합니다."
    )
    public ResponseEntity<String> deleteArticle(@PathVariable("articleId") Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userName = SecurityUtil.extractUsername(userDetails);
        articleService.deleteArticle(articleId, userName);
        return ResponseEntity.status(HttpStatus.OK).body("작품이 정상적으로 삭제되었습니다.");
    }

    @Operation(summary = "작품 기반 태그 생성", description = "작품 설명을 기반으로 2개의 태그를 생성")
    @PostMapping("/tags")
    public ResponseEntity<List<String>> generateTags(@RequestBody GptRequest gptRequest) {
        List<String> tags = articleService.generateTags(gptRequest);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/my/{companyId}")
    @Operation(summary = "내 작품 조회", description = "작품 제안 시 사용")
    public ResponseEntity<List<MyArticlesResponse>> getMyArticles(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("companyId") Long companyId) {
        String userName = SecurityUtil.extractUsername(userDetails);
        return ResponseEntity.ok(articleService.getMyArticles(userName, companyId));
    }
}