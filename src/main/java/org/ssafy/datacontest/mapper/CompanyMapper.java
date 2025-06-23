package org.ssafy.datacontest.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.enums.IndustryType;

public class CompanyMapper {

    public static Company toEntity(CompanyRegisterRequest request, PasswordEncoder passwordEncoder) {
        IndustryType industryType = IndustryType.fromAlias(request.getCompanyField());

        return Company.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNum(request.getPhoneNum())
                .profileImage(request.getProfileImage())
                .role("ROLE_COMPANY")
                .companyName(request.getCompanyName())
                .companyDescription(request.getCompanyDescription())
                .companyField(industryType)
                .companyLoc(request.getCompanyLoc())
                .hiring(request.getHiring())
                .companyLink(request.getCompanyLink())
                .build();
    }
}