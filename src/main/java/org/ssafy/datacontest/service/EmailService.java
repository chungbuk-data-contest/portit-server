package org.ssafy.datacontest.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.ssafy.datacontest.dto.email.EmailVerify;

public interface EmailService {
    void sendEmail(String email);

    boolean verifyEmail(EmailVerify emailVerify);
}
