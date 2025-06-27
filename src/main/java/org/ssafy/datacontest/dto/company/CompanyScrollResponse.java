package org.ssafy.datacontest.dto.company;

import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.enums.IndustryType;
import org.ssafy.datacontest.enums.RegionType;

@Getter
@Builder
public class CompanyScrollResponse {
    private Long companyId;
    private String companyLoginId;
    private String companyName;
    private String companyDescription;
    private IndustryType companyField;
    private Boolean hiring;
    private String companyLink;
    private RegionType companyLoc;
}
