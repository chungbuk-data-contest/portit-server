package org.ssafy.datacontest.service;


import org.ssafy.datacontest.dto.SliceResponseDto;
import org.ssafy.datacontest.dto.company.ArticleLikeResponse;
import org.ssafy.datacontest.dto.company.CompanyResponse;
import org.ssafy.datacontest.dto.company.CompanyScrollRequest;
import org.ssafy.datacontest.dto.company.CompanyScrollResponse;

public interface CompanyService {
    void fetchAndSaveCompanies();
    ArticleLikeResponse toggleLike(Long articleId, String companyName);
    SliceResponseDto<CompanyScrollResponse> getCompaniesByCursor(CompanyScrollRequest companyScrollRequest);
    CompanyResponse getCompany(String companyName);
}
