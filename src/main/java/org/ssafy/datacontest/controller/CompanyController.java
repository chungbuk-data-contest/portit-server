package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.service.CompanyService;

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
}
