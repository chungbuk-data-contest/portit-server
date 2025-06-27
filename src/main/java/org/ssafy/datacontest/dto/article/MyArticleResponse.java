package org.ssafy.datacontest.dto.article;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyArticleResponse {
    private Long articleId;
    private String thumbnailUrl;
    private String articleTitle;
    private Long likeCount;
    private boolean chatting;
}
