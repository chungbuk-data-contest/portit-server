package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.dto.company.ArticleLikeResponse;
import org.ssafy.datacontest.entity.Article;

public class ArticleLikeMapper {
    public static ArticleLikeResponse toResponse(boolean liked, Article article) {
        return ArticleLikeResponse.builder()
                .liked(liked)
                .likeCount(article.getLikeCount())
                .build();
    }
}
