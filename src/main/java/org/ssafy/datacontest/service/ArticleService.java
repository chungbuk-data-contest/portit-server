package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.SliceResponseDto;
import org.ssafy.datacontest.dto.article.*;
import org.ssafy.datacontest.dto.image.ImageUpdateDto;

import java.util.List;

public interface ArticleService {
    Long createArticle(ArticleRequestDto articleRequestDto, String userName) throws Exception;
    Long updateArticle(ArticleUpdateRequestDto articleRequestDto, String userName, Long articleId, List<ImageUpdateDto> imageIdList) throws Exception;
    void deleteArticle(Long articleId, String userName);
    ArticleDetailResponse getArticle(Long articleId);
    SliceResponseDto<ArticlesScrollResponse> getArticlesByCursor(ArticleScrollRequestDto articleScrollRequestDto);
}
