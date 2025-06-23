package org.ssafy.datacontest.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.client.PublicApiClient;
import org.ssafy.datacontest.dto.publicApi.PublicCompanyDto;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.enums.IndustryType;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.util.RandomUtil;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final PublicApiClient publicApiClient;

    @Transactional // 중간에 하나라도 실패하면 전부 롤백
    public void fetchAndSaveCompanies() {
        // api 부르기
        List<PublicCompanyDto> publicCompany = publicApiClient.fetchData();

        // random 값으로 나머지 필드 채우기
        List<Company> companies = maapingCompanies(publicCompany);

        // 필수 필드 체크 후 DB 저장
        for (Company company : companies) {
            validateCompany(company);
        }

        companyRepository.saveAll(companies);
        log.info("기업 {}건 저장됨.", companies.size());
    }

    // 매핑
    public List<Company> maapingCompanies(List<PublicCompanyDto> publicCompanyList) {
        List<Company> companies = publicCompanyList.stream()
                .map(dto -> {
                    Company company = new Company();
                    company.setCompanyName(dto.getCompanyName());
                    company.setCompanyDescription(dto.getCompanyDescription());
                    company.setCompanyLoc(dto.getCompanyLoc());
                    company.setCompanyField(IndustryType.fromAlias(dto.getCompanyField()));

                    // 랜덤 필드 생성
                    company.setLoginId(RandomUtil.email());
                    company.setPassword("1234");
                    company.setPhoneNum(RandomUtil.phone());
                    company.setHiring(false);
                    company.setProfileImage(null);   // 없으면 null 가능
                    company.setCompanyLink(null);    // 필요시 기본값 설정
                    company.setRole("ROLE_COMPANY");

                    return company;
                })
                .toList();

        return companies;
    }

    // 필수 필드 체크
    public void validateCompany(Company company) {
        validateEmail(company.getLoginId());
        validateName(company.getCompanyName());
        validateLoc(company.getCompanyLoc());
        validateField(company.getCompanyField());
        validatePassword(company.getPassword());
        validatePhoneNum(company.getPhoneNum());
    }

    public void validateEmail(String email){
        if(email == null || email.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_EMAIL);
        }
    }

    public void validatePassword(String password){
        if(password == null || password.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_PASSWORD);
        }
    }

    public void validatePhoneNum(String phoneNum){
        if(phoneNum == null || phoneNum.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_PHONE_NUMBER);
        }
    }

    public void validateName(String companyName){
        if(companyName == null || companyName.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_NAME);
        }
    }

    public void validateLoc(String companyLoc){
        if(companyLoc == null || companyLoc.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_LOCATION);
        }
    }

    public void validateField(IndustryType companyField) {
        if (companyField == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_FIELD);
        }

        boolean isValid = Arrays.stream(IndustryType.values())
                .anyMatch(type -> type == companyField);

        if (!isValid) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_COMPANY_FIELD);
        }
    }


}
