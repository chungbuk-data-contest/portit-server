package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.SliceResponseDto;
import org.ssafy.datacontest.dto.article.*;

public interface ArticleService {
    Long createArticle(ArticleRequestDto articleRequestDto, String userName);
    Long updateArticle(ArticleRequestDto articleRequestDto);
    void deleteArticle(Long articleId);
    ArticleResponseDto getArticle(Long articleId);
    SliceResponseDto<ArticlesResponseDto> getArticlesByCursor(ArticleScrollRequestDto articleScrollRequestDto);
}
