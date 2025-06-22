package org.ssafy.datacontest.dto.article;

import lombok.Getter;

@Getter
public class ArticleImageDto {
    private Long artId;
    private String imageUrl;

    public ArticleImageDto(Long artId, String imageUrl) {
        this.artId = artId;
        this.imageUrl = imageUrl;
    }
}
