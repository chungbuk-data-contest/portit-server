package org.ssafy.datacontest.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.ssafy.datacontest.dto.register.UserRegisterRequest;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.UserRepository;

@Component
public class UserValidation {

    private final UserRepository userRepository;

    @Autowired
    public UserValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUser(UserRegisterRequest request) {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        validateNickname(request.getNickname());
        validatePhoneNum(request.getPhoneNum());
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_EMAIL);
        }
        if (!email.contains("@")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_EMAIL);
        }
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATED_EMAIL);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PASSWORD);
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMPTY_NICKNAME);
        }
    }

    private void validatePhoneNum(String phoneNum) {
        if (phoneNum == null || !phoneNum.matches("^\\d{10,11}$")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PHONE_NUMBER);
        }
    }
}