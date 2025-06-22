package org.ssafy.datacontest.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.CompanyRepository;

@Component
public class CompanyValidation {

    private final CompanyRepository companyRepository;
    private final RegisterCommonValidation registerCommonValidation;

    @Autowired
    public CompanyValidation(CompanyRepository companyRepository,
                             RegisterCommonValidation registerCommonValidation) {
        this.companyRepository = companyRepository;
        this.registerCommonValidation = registerCommonValidation;
    }

    public void validateCompany(CompanyRegisterRequest request) {
        registerCommonValidation.companyValidate(request);
        loginIdDuplicateValidate(request);
        validateCompanyName(request);
        validateCompanyField(request);
        validateCompanyLocation(request);
        validateHiringStatus(request);
    }

    private void loginIdDuplicateValidate(CompanyRegisterRequest request) {
        if (companyRepository.existsByLoginId(request.getLoginId())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATED_EMAIL);
        }
    }

    private void validateCompanyName(CompanyRegisterRequest request) {
        String companyName = request.getCompanyName();
        if(companyName == null || companyName.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_NAME);
        }
    }

    private void validateCompanyField(CompanyRegisterRequest request) {
        String companyField = request.getCompanyField();
        if (companyField == null || companyField.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_FIELD);
        }
    }

    private void validateCompanyLocation(CompanyRegisterRequest request) {
        String companyLoc = request.getCompanyLoc();
        if (companyLoc == null || companyLoc.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_COMPANY_LOCATION);
        }
    }

    private void validateHiringStatus(CompanyRegisterRequest request) {
        Boolean hiring = request.getHiring();
        if (hiring == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.NULL_HIRING_STATUS);
        }
    }
}
