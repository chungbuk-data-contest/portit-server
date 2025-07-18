package org.ssafy.datacontest.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.ssafy.datacontest.dto.register.UserRegisterRequest;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.UserRepository;

@Component
public class UserValidation {

    private final UserRepository userRepository;
    private final RegisterCommonValidation registerCommonValidation;

    @Autowired
    public UserValidation(UserRepository userRepository,
                          RegisterCommonValidation registerCommonValidation) {
        this.userRepository = userRepository;
        this.registerCommonValidation = registerCommonValidation;
    }

    public void validateUser(UserRegisterRequest request) {
        registerCommonValidation.userValidate(request);
        loginIdDuplicateValidate(request);
        nicknameDuplicateValidate(request);
    }

    private void loginIdDuplicateValidate(UserRegisterRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATED_ID);
        }
    }

    private void nicknameDuplicateValidate(UserRegisterRequest request) {
        if(userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATED_NICKNAME);
        }
    }
}