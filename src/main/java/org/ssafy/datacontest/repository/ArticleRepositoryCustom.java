package org.ssafy.datacontest.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.ssafy.datacontest.dto.article.ArticleScrollRequestDto;
import org.ssafy.datacontest.entity.Article;

public interface ArticleRepositoryCustom {
    Slice<Article> findNextPageByCursor(ArticleScrollRequestDto req);
}
