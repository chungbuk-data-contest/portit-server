package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.dto.article.ArticleRequestDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.enums.Category;

public class ArticleMapper {

    public static Article toEntity(ArticleRequestDto dto, String videoUrl){
        return Article.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .userId(dto.getUserId())
                .videoFileUrl(videoUrl)
                .category(Category.valueOf(dto.getCategory()))
                .externalLink(dto.getExternalLink())
                .visible(dto.isVisible()).build();
    }
}
