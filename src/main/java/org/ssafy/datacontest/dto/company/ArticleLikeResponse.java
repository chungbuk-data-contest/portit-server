package org.ssafy.datacontest.dto.company;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleLikeResponse {
    private boolean liked;
    private Long likeCount;
}
