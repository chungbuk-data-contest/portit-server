package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.dto.article.*;
import org.ssafy.datacontest.dto.image.ImageDto;
import org.ssafy.datacontest.dto.tag.TagDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.Category;

import java.util.List;

public class ArticleMapper {

    public static Article toEntity(ArticleRequestDto dto, User user, String thumbnailUrl) {
        return Article.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .externalLink(dto.getExternalLink())
                .category(Category.valueOf(dto.getCategory()))
                .user(user)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

    public static ArticleResponseDto toArticleResponseDto(Article article, List<ImageDto> fileUrls, List<TagDto> tags, User user) {
        return ArticleResponseDto.builder()
                .articleId(article.getArtId())
                .title(article.getTitle())
                .description(article.getDescription())
                .externalLink(article.getExternalLink())
                .createdAt(article.getCreatedAt())
                .category(article.getCategory())
                .thumbnailUrl(article.getThumbnailUrl())
                .fileUrl(fileUrls)
                .tagList(tags)
                .likeCount(article.getLikeCount())
                .userName(user.getNickname())
                .userLoginId(user.getLoginId())
                .build();
    }

    public static ArticlesResponseDto toArticlesResponseDto(Article article, List<String> tagList){
        return ArticlesResponseDto.builder()
                .articleId(article.getArtId())
                .articleTitle(article.getTitle())
                .imageUrl(article.getThumbnailUrl())
                .tagList(tagList)
                .category(article.getCategory())
                .createdAt(article.getCreatedAt())
                .userName(article.getUser().getNickname())
                .build();
    }
}
