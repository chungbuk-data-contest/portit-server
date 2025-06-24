package org.ssafy.datacontest.dto.article;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ArticlesScrollResponse {
    List<ArticlesResponseDto> premiumArticles;
    List<ArticlesResponseDto> articles;
}
