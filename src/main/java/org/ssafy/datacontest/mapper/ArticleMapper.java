package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.dto.article.*;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Tag;
import org.ssafy.datacontest.enums.Category;

import java.util.List;

public class ArticleMapper {

    public static Article toEntity(ArticleRequestDto dto){
        return Article.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .externalLink(dto.getExternalLink())
                .category(Category.valueOf(dto.getCategory()))
                .build();
    }

    public static ArticleDto toDto(Article article, List<ImageDto> images, List<TagDto> tags) {
        return ArticleDto.builder()
                .articleId(article.getArtId())
                .title(article.getTitle())
                .description(article.getDescription())
                .externalLink(article.getExternalLink())
                .createdAt(article.getCreatedAt())
                .category(article.getCategory())
                .images(images)
                .tags(tags)
                .build();
    }

    public static ArticlesResponseDto toArticlesResponseDto(Article article, String imageUrl, List<String> tagList){
        return ArticlesResponseDto.builder()
                .articleId(article.getArtId())
                .articleTitle(article.getTitle())
                .imageUrl(imageUrl)
                .tagList(tagList)
                .category(article.getCategory())
                .createdAt(article.getCreatedAt())
                .build();
    }
}
