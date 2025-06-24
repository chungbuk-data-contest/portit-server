package org.ssafy.datacontest.dto.company;

import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.enums.IndustryType;

@Getter
@Builder
public class CompanyRecommendDto {
    private String companyName;
    private IndustryType companyField;
    private String companyLink;
}
