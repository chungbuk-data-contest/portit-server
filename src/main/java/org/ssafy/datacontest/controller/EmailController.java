package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.email.EmailVerify;
import org.ssafy.datacontest.service.EmailService;

@Tag(name = "Email")
@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Operation(summary = "이메일 인증 코드 발송", description = "입력한 이메일 주소로 인증 코드 발송.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        emailService.sendEmail(email);
        return ResponseEntity.ok("Email sent successfully");
    }


    @Operation(summary = "이메일 인증 코드 검증", description = "이메일과 대조한 인증 코드가 유효한지 검증.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "인증 실패")
    })
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
