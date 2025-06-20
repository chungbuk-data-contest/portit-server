package org.ssafy.datacontest.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.ssafy.datacontest.dto.register.UserRegisterRequest;
import org.ssafy.datacontest.entity.User;

public class UserMapper {
    public static User toEntity(UserRegisterRequest userRegisterRequest, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .nickname(userRegisterRequest.getNickname())
                .phoneNum(userRegisterRequest.getPhoneNum())
                .profileImage(userRegisterRequest.getProfileImage())
                .role("ROLE_USER")
                .build();
    }
}
