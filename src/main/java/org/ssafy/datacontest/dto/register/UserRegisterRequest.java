package org.ssafy.datacontest.dto.register;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequest {
    private String loginId;
    private String password;
    private String nickname;
    private String phoneNum;
    private String profileImage;
}
