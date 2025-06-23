package org.ssafy.datacontest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.fcm.FcmTokenRequest;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.FcmService;

@Service
public class FcmServiceImpl implements FcmService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public FcmServiceImpl(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public void saveOrUpdateToken(FcmTokenRequest fcmTokenRequest, String loginId, String role) {
        String token = fcmTokenRequest.getFcmToken();
        if ("ROLE_USER".equals(role)) {
            User user = userRepository.findByLoginId(loginId);
            if(user == null){
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND);
            }

            if (!token.equals(user.getFcmToken())) {
                user.setFcmToken(token);
                userRepository.save(user);
            }

        } else if ("ROLE_COMPANY".equals(role)) {
            Company company = companyRepository.findByLoginId(loginId);
            if(company == null){
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.COMPANY_NOT_FOUND);
            }

            if (!token.equals(company.getFcmToken())) {
                company.setFcmToken(token);
                companyRepository.save(company);
            }
        }
    }
}
