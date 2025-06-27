package org.ssafy.datacontest.repository.custom;

import org.springframework.data.domain.Slice;
import org.ssafy.datacontest.dto.article.ArticleScrollRequest;
import org.ssafy.datacontest.entity.Article;

import java.util.List;

public interface ArticleRepositoryCustom {
    Slice<Article> findNextPageByCursor(ArticleScrollRequest req);
    List<Article> findRandomPremiumArticles(int count);
}
