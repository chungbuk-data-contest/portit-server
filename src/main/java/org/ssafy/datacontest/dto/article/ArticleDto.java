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
    private String category;
    private String externalLink;
    private boolean visible;
    private LocalDateTime createdAt;

    private List<ImageDto> images;
    private List<TagDto> tags;

    public static ArticleDto from(Article article, List<ImageDto> images, List<TagDto> tags) {
        return ArticleDto.builder()
                .articleId(article.getArtId())
                .title(article.getTitle())
                .description(article.getDescription())
                .category(article.getCategory().name()) // enum → 문자열
                .externalLink(article.getExternalLink())
                .visible(article.isVisible())
                .createdAt(article.getCreatedAt())
                .images(images)
                .tags(tags)
                .build();
    }
}
