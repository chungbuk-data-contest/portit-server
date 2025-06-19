package org.ssafy.datacontest.dto.article;

import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.enums.Category;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ArticleDto {

    private Long articleId;
    private String title;
    private String description;
    private String externalLink;
    private boolean visible;
    private LocalDateTime createdAt;
    private Category category;

    private List<ImageDto> images;
    private List<TagDto> tags;

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
}
