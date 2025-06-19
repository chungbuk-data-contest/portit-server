package org.ssafy.datacontest.dto.register;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.ssafy.datacontest.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String email;
    private String password;
    private String nickname;
    private String phoneNum;
    private String profileImage;

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
