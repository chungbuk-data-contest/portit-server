package org.ssafy.datacontest.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.dto.register.UserRegisterRequest;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;

@Component
public class RegisterCommonValidation {

    public void userValidate(UserRegisterRequest request){
        validateLoginId(request.getLoginId());
        validatePassword(request.getPassword());
        validateNickname(request.getNickname());
        validatePhoneNum(request.getPhoneNum());
    }

    public void companyValidate(CompanyRegisterRequest request){
        validateLoginId(request.getLoginId());
        validatePassword(request.getPassword());
        validatePhoneNum(request.getPhoneNum());
    }
    private void validateLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_EMAIL);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank() || password.length() < 8) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PASSWORD);
        }
    }

    private void validatePhoneNum(String phoneNum) {
        if (phoneNum == null || phoneNum.isBlank() || !phoneNum.matches("^\\d{10,11}$")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PHONE_NUMBER);
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_NICKNAME);
        }
    }
}