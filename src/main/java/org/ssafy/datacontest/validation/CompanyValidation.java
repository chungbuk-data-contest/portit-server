package org.ssafy.datacontest.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.ssafy.datacontest.dto.company.CompanyUpdateRequest;
import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.enums.IndustryType;
import org.ssafy.datacontest.enums.RegionType;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.CompanyRepository;

@Component
@RequiredArgsConstructor
public class CompanyValidation {

    private final CompanyRepository companyRepository;
    private final RegisterCommonValidation registerCommonValidation;

    public void validateCompany(CompanyRegisterRequest request) {
        registerCommonValidation.companyValidate(request);
        validateLoginIdDuplicate(request.getLoginId());
        validateCompanyName(request.getCompanyName());
        validateCompanyField(request.getCompanyField());
        validateCompanyLocation(request.getCompanyLoc());
        validateHiringStatus(request.getHiring());
    }

    public void validateUpdateCompany(CompanyUpdateRequest request) {
        validateCompanyName(request.getCompanyName());
        validateCompanyField(request.getCompanyField());
        validateCompanyLocation(request.getCompanyLoc());
        validateHiringStatus(request.getHiring());
    }

    private void validateLoginIdDuplicate(String loginId) {
        if (companyRepository.existsByLoginId(loginId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATED_ID);
        }
    }

    private void validateCompanyName(String companyName) {
        if(companyName == null || companyName.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_NAME);
        }
    }

    private void validateCompanyField(String companyField) {
        if (companyField == null || companyField.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_FIELD);
        }

        // 유효하지 않은 경우 fromAlias 내부에서 CustomException 발생
        IndustryType.fromAlias(companyField);
    }

    private void validateCompanyLocation(String companyLoc) {
        if (companyLoc == null || companyLoc.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_LOCATION);
        }

        // 유효하지 않은 경우 fromAlias 내부에서 CustomException 발생
        RegionType.fromAlias(companyLoc);
    }

    private void validateHiringStatus(Boolean hiring) {
        if (hiring == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.NULL_HIRING_STATUS);
        }
    }
}
