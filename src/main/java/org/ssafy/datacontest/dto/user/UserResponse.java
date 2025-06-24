package org.ssafy.datacontest.dto.user;

import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.dto.company.LikedArticleResponse;

import java.util.List;

@Getter
@Builder
public class UserResponse {
    private Long userId;
    private String userNickname;
    private String userLoginId;
    private List<LikedArticleResponse> myArticles;
}
