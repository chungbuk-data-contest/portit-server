package org.ssafy.datacontest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.service.CompanyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/import")
    public ResponseEntity<String> importCompanies() {
        companyService.fetchAndSaveCompanies();

        return ResponseEntity.ok("공공데이터 저장 완료");
    }
}
