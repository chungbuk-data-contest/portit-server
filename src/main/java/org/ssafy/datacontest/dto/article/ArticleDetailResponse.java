package org.ssafy.datacontest.dto.article;

import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.dto.company.CompanyRecommendDto;

import java.util.List;

@Getter
@Builder
public class ArticleDetailResponse {
    ArticleResponseDto article;
    boolean liked;
    List<CompanyRecommendDto> companies;
}
