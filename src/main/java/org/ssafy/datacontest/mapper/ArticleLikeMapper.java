package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.dto.company.ArticleLikeResponse;
import org.ssafy.datacontest.dto.company.LikedArticleResponse;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Company;

public class ArticleLikeMapper {
    public static ArticleLikeResponse toResponse(boolean liked, Article article) {
        return ArticleLikeResponse.builder()
                .liked(liked)
                .likeCount(article.getLikeCount())
                .build();
    }

    public static LikedArticleResponse toLikedArticleResponse(Article article) {
        return LikedArticleResponse.builder()
                .articleId(article.getArtId())
                .articleTitle(article.getTitle())
                .likeCount(article.getLikeCount())
                .thumbnailUrl(article.getThumbnailUrl())
                .build();
    }
}
