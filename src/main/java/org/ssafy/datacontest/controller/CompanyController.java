package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.SliceResponseDto;
import org.ssafy.datacontest.dto.company.ArticleLikeResponse;
import org.ssafy.datacontest.dto.company.CompanyScrollRequest;
import org.ssafy.datacontest.dto.company.CompanyScrollResponse;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.enums.IndustryType;
import org.ssafy.datacontest.enums.RegionType;
import org.ssafy.datacontest.service.CompanyService;

import javax.swing.plaf.synth.Region;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Company", description = "기업 관련 API")
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("")
    @Operation(
            summary = "공공데이터 이용한 기업 등록"
    )
    public ResponseEntity<String> importCompanies() {
        companyService.fetchAndSaveCompanies();

        return ResponseEntity.ok("공공데이터 저장 완료");
    }

    @PostMapping("/articles/{articleId}/likes")
    @Operation(
            summary ="기업이 작품 좋아요 등록/취소 (토글)"
    )
    public ResponseEntity<ArticleLikeResponse> likeCompany(@PathVariable("articleId") Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(companyService.toggleLike(articleId, userDetails.getUsername()));
    }

    @GetMapping("")
    @Operation(
            summary = "기업 목록 무한 스크롤 조회 (필터링 및 커서 기반 페이징)"
    )
    public ResponseEntity<SliceResponseDto<CompanyScrollResponse>> getCompanies(
            @RequestParam(required = false) List<IndustryType> companyField,
            @RequestParam(required = false) List<RegionType> companyLoc,
            @ModelAttribute CompanyScrollRequest companyScrollRequest
            ){
        companyScrollRequest.setCompanyField(companyField);
        companyScrollRequest.setCompanyLoc(companyLoc);

        return ResponseEntity.ok(companyService.getCompaniesByCursor(companyScrollRequest));
    }
}
