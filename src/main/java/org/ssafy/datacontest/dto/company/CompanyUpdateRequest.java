package org.ssafy.datacontest.dto.company;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyUpdateRequest {
    private Long companyId;
    private String companyName;
    private Boolean hiring;
}
