package org.ssafy.datacontest.dto.register;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CompanyRegisterRequest {
    private String loginId;
    private String password;
    private String phoneNum;
    private String profileImage;

    private String companyName;         // 업체명
    private String companyDescription;  // 설명 or 주생산품
    private String companyField;        // 업종명
    private String companyLoc;          // 지역
    private Boolean hiring;
    private String companyLink;
}