package org.ssafy.datacontest.dto.article;

import lombok.Getter;

@Getter
public class ArticleResponseDto {
    // TODO: 유저 정보
//    private List<String> user;

    // 작품 정보
    private ArticleDto article;

    public ArticleResponseDto(ArticleDto articleDto) {
        this.article = articleDto;
    }
}