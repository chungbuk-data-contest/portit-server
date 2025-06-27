package org.ssafy.datacontest.dto.company;

import lombok.*;
import org.ssafy.datacontest.enums.IndustryType;
import org.ssafy.datacontest.enums.RegionType;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyScrollRequest {
    private Long companyId;
    private List<IndustryType> companyField;
    private List<RegionType> companyLoc;
    private Boolean hiring;
    private int size = 10;

    private String keyword;

    public void fillFields(List<IndustryType> companyField, List<RegionType> companyLoc) {
        this.companyField = companyField != null ? companyField : Collections.emptyList();
        this.companyLoc = companyLoc != null ? companyLoc : Collections.emptyList();
    }
}
