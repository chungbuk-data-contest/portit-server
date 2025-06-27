package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.article.ArticleListResponse;
import org.ssafy.datacontest.dto.premium.PremiumResponse;

import java.util.List;

public interface PremiumService {
    PremiumResponse registerPremium(Long articleId, String userName);
    List<ArticleListResponse> getPremiumArticles();

}
