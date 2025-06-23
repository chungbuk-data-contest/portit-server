package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.dto.register.UserRegisterRequest;

public interface AuthService {
    void userSignUp(UserRegisterRequest userRegisterRequest);

    void companySignUp(CompanyRegisterRequest companyRegisterRequest);

    void checkUserLoginId(String loginId);

    void checkUserNickname(String nickname);
}
