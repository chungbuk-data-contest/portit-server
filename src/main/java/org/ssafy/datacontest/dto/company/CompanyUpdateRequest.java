package org.ssafy.datacontest.dto.company;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyUpdateRequest {
    private String companyName;
    private String companyField;
    private String companyLoc;
    private String companyDescription;
    private String companyLink;
    private Boolean hiring;
}
