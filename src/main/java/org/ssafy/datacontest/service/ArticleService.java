package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.article.*;

import java.util.List;

public interface ArticleService {
    Long createArticle(ArticleRequestDto articleRequestDto);
    Long updateArticle(ArticleRequestDto articleRequestDto);
    Long deleteArticle(ArticleRequestDto articleRequestDto);
    ArticleResponseDto getArticle(Long articleId);
    List<ArticleResponseDto> getArticles();
}
