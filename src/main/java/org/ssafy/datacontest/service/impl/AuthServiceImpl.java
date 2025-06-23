package org.ssafy.datacontest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.register.CompanyRegisterRequest;
import org.ssafy.datacontest.dto.register.UserRegisterRequest;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.CompanyMapper;
import org.ssafy.datacontest.mapper.UserMapper;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.AuthService;
import org.ssafy.datacontest.validation.CompanyValidation;
import org.ssafy.datacontest.validation.UserValidation;

@Service
public class AuthServiceImpl implements AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidation userValidation;
    private final CompanyValidation companyValidation;

    @Autowired
    public AuthServiceImpl(CompanyRepository companyRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserValidation userValidation,
                           CompanyValidation companyValidation) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidation = userValidation;
        this.companyValidation = companyValidation;
    }

    @Override
    public void userSignUp(UserRegisterRequest userRegisterRequest) {
        userValidation.validateUser(userRegisterRequest);
        User user = UserMapper.toEntity(userRegisterRequest, passwordEncoder);
        userRepository.save(user);
    }

    @Override
    public void companySignUp(CompanyRegisterRequest companyRegisterRequest) {
        companyValidation.validateCompany(companyRegisterRequest);
        Company company = CompanyMapper.toEntity(companyRegisterRequest, passwordEncoder);
        companyRepository.save(company);
    }

    @Override
    public void checkUserLoginId(String loginId) {
        boolean existsUser = userRepository.existsByLoginId(loginId);
        boolean existsCompany = companyRepository.existsByLoginId(loginId);
        if(existsUser || existsCompany) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATED_ID);
        }
    }

    @Override
    public void checkUserNickname(String nickname) {
        boolean existsUser = userRepository.existsByNickname(nickname);
        if(existsUser) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATED_NICKNAME);
        }
    }
}
