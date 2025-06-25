package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.article.ArticlesResponseDto;
import org.ssafy.datacontest.dto.premium.PremiumResponse;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.entity.Premium;
import org.ssafy.datacontest.service.PremiumService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name="Premium", description ="프리미엄 관련 API")
@RequestMapping("/premium")
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping("/{articleId}")
    @Operation(
            summary ="프리미엄 등록",
            description = ""
    )
    public ResponseEntity<PremiumResponse> registerPremium(@PathVariable("articleId") Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(premiumService.registerPremium(articleId, userDetails.getUsername()));
    }

    @GetMapping("")
    @Operation(
            summary = "프리미엄 작품 조회",
            description = "프리미엄 작품 랜덤 4개 반환합니다."
    )
    public ResponseEntity<List<ArticlesResponseDto>> getPremiumArticles() {
        return ResponseEntity.ok(premiumService.getPremiumArticles());
    }

}
