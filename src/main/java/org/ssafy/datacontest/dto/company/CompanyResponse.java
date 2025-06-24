package org.ssafy.datacontest.dto.company;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CompanyResponse {
    // 기업명, 이메일, 기업 설명, 관심작품 List, 채용 여부
    private Long companyId;
    private String companyName;
    private String companyDescription;
    private String companyLoginId;
    private List<LikedArticleResponse> likedArticle;
    private boolean hiring;
}
