package org.ssafy.datacontest.dto.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerify {
    private String email;
    private String certificationCode;
}
