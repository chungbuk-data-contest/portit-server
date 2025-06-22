package org.ssafy.datacontest.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.entity.Company;

public class CompanyMapper {

    public static Company toEntity(CompanyRegisterRequest request, PasswordEncoder passwordEncoder) {
        return Company.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNum(request.getPhoneNum())
                .profileImage(request.getProfileImage())
                .role("ROLE_COMPANY")
                .companyName(request.getCompanyName())
                .companyDescription(request.getCompanyDescription())
                .companyField(request.getCompanyField())
                .companyLoc(request.getCompanyLoc())
                .hiring(request.getHiring())
                .companyLink(request.getCompanyLink())
                .build();
    }
}