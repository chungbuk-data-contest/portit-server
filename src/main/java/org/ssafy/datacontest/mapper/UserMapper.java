package org.ssafy.datacontest.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.ssafy.datacontest.dto.register.RegisterRequest;
import org.ssafy.datacontest.entity.User;

public class UserMapper {
    public static User toEntity(RegisterRequest registerRequest, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .nickname(registerRequest.getNickname())
                .phoneNum(registerRequest.getPhoneNum())
                .profileImage(registerRequest.getProfileImage())
                .role("ROLE_USER")
                .build();
    }
}
