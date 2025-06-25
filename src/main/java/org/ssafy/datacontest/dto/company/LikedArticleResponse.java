package org.ssafy.datacontest.dto.company;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikedArticleResponse {
    private Long articleId;
    private String articleTitle;
    private Long likeCount;
    private String thumbnailUrl;
    private boolean premium;
}
