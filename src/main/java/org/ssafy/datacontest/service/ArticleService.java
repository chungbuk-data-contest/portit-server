package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.SliceResponseDto;
import org.ssafy.datacontest.dto.article.*;

import java.util.List;

public interface ArticleService {
    Long createArticle(ArticleRequestDto articleRequestDto, String userName);
    Long updateArticle(ArticleUpdateRequestDto articleRequestDto, String userName, Long articleId, List<ImageUpdateDto> imageIdList);
    void deleteArticle(Long articleId, String userName);
    ArticleResponseDto getArticle(Long articleId);
    SliceResponseDto<ArticlesResponseDto> getArticlesByCursor(ArticleScrollRequestDto articleScrollRequestDto);
}
