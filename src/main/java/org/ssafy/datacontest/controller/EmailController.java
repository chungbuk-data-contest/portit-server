package org.ssafy.datacontest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.email.EmailVerify;
import org.ssafy.datacontest.service.EmailService;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        emailService.sendEmail(email);
        return ResponseEntity.ok("Email sent successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailVerify emailVerify) {
        boolean verify = emailService.verifyEmail(emailVerify);
        if (verify) {
            return ResponseEntity.ok("인증이 되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증에 실패했습니다.");
        }
    }
}
