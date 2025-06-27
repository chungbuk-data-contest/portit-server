package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.SliceResponseDto;
import org.ssafy.datacontest.dto.article.*;
import org.ssafy.datacontest.dto.gpt.GptRequest;
import org.ssafy.datacontest.dto.image.ImageUpdateDto;

import java.util.List;

public interface ArticleService {
    Long createArticle(ArticleRequest articleRequest, String userName) throws Exception;
    ArticleDetailResponse getArticle(Long articleId, String userName);
    SliceResponseDto<ArticleListResponse> getArticlesByCursor(ArticleScrollRequest articleScrollRequest);
    Long updateArticle(ArticleUpdateRequest articleRequestDto, String userName, Long articleId, List<ImageUpdateDto> imageIdList) throws Exception;
    void deleteArticle(Long articleId, String userName);
    List<String> generateTags(GptRequest gptRequest);
    List<MyArticlesResponse> getMyArticles(String userName, Long companyId);
}
