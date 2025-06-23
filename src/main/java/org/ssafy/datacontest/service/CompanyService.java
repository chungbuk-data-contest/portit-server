package org.ssafy.datacontest.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.client.PublicApiClient;
import org.ssafy.datacontest.dto.publicApi.PublicCompanyDto;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.enums.IndustryType;
import org.ssafy.datacontest.enums.RegionType;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.CompanyMapper;
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
    private final PasswordEncoder passwordEncoder;

    @Transactional // 중간에 하나라도 실패하면 전부 롤백
    public void fetchAndSaveCompanies() {
        // api 부르기
        List<PublicCompanyDto> publicCompany = publicApiClient.fetchData();

        // random 값으로 나머지 필드 채우기
        List<Company> companies = CompanyMapper.publicDataToEntity(publicCompany, passwordEncoder);

        companyRepository.saveAll(companies);
        log.info("기업 {}건 저장됨.", companies.size());
    }


}
