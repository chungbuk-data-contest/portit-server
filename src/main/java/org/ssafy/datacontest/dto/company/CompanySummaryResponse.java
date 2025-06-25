package org.ssafy.datacontest.dto.company;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanySummaryResponse {
    private Long companyId;
    private String companyName;
    private String companyDescription;
    private boolean chatting;
}
