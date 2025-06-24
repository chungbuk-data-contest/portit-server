package org.ssafy.datacontest.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.ssafy.datacontest.dto.company.CompanyScrollResponse;
import org.ssafy.datacontest.dto.publicApi.PublicCompanyDto;
import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.enums.IndustryType;
import org.ssafy.datacontest.enums.RegionType;
import org.ssafy.datacontest.util.RandomUtil;

import java.util.List;

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
                .companyLoc(RegionType.fromAlias(request.getCompanyLoc()))
                .hiring(request.getHiring())
                .companyLink(request.getCompanyLink())
                .build();
    }

    public static List<Company> publicDataToEntity(List<PublicCompanyDto> publicCompanyList, PasswordEncoder passwordEncoder) {
        List<Company> companies = publicCompanyList.stream()
                .map(dto -> {
                    Company company = new Company();
                    company.setCompanyName(dto.getCompanyName());
                    company.setCompanyDescription(dto.getCompanyDescription());
                    company.setCompanyLoc(RegionType.fromAlias(dto.getCompanyLoc()));
                    company.setSimpleAddress(dto.getSimpleAddress());
                    company.setCompanyField(IndustryType.fromAlias(dto.getCompanyField()));

                    // 랜덤 필드 생성
                    company.setLoginId(RandomUtil.email());
                    company.setPassword(passwordEncoder.encode("1234"));
                    company.setPhoneNum(RandomUtil.phone());
                    company.setHiring(true);
                    company.setProfileImage(null);
                    company.setCompanyLink(null);
                    company.setRole("ROLE_COMPANY");

                    return company;
                })
                .toList();

        return companies;
    }

    public static CompanyScrollResponse toCompanyScrollResponse(Company company) {
        return CompanyScrollResponse.builder()
                .companyId(company.getCompanyId())
                .companyName(company.getCompanyName())
                .companyDescription(company.getCompanyDescription())
                .companyLink(company.getCompanyLink())
                .companyLoc(company.getCompanyLoc())
                .companyField(company.getCompanyField())
                .hiring(company.getHiring())
                .build();
    }
}