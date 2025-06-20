package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.dto.register.RegisterRequest;

public interface AuthService {
    void userSignUp(RegisterRequest registerRequest);

    void companySignUp(CompanyRegisterRequest companyRegisterRequest);
}
