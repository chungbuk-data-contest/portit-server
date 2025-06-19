package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.article.*;

public interface ArticleService {
    Long createArticle(ArticleRequestDto articleRequestDto);
}
