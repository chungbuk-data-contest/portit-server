package org.ssafy.datacontest.service;


import org.ssafy.datacontest.dto.company.ArticleLikeResponse;

public interface CompanyService {
    void fetchAndSaveCompanies();
    ArticleLikeResponse toggleLike(Long articleId, String companyName);
}
