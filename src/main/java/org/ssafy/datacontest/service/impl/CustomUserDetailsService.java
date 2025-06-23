package org.ssafy.datacontest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
                                    CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        User user = userRepository.findByLoginId(loginId);

        if(user != null) {
            return new CustomUserDetails(user);
        }

        Company company = companyRepository.findByLoginId(loginId);
        if(company != null) {
            return new CustomUserDetails(company);
        }

        throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND);
    }
}