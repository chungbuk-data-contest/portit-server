package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.dto.sms.SmsRequestDto;
import org.ssafy.datacontest.dto.sms.SmsVerifyDto;
import org.ssafy.datacontest.service.SmsService;

@Tag(name = "SMS", description = "휴대폰 번호로 인증번호를 전송하고, 인증번호 검증 수행.")
@RestController
@RequestMapping("/sms")
public class SmsController {
    private final SmsService smsService;

    @Autowired
    public SmsController(SmsService smsService){
        this.smsService = smsService;
    }

    @Operation(
            summary = "SMS 인증번호 전송",
            description = "사용자의 휴대폰 번호로 인증번호 전송."
    )
    @PostMapping("/send")
    public ResponseEntity<?> SendSMS(@RequestBody SmsRequestDto smsRequestDto){
        smsService.sendSms(smsRequestDto);
        return ResponseEntity.ok("문자를 전송했습니다.");
    }

    @Operation(
            summary = "SMS 인증번호 검증",
            description = "사용자가 입력한 인증번호 검증."
    )
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody SmsVerifyDto smsVerifyDto){
        boolean verify = smsService.verifyCode(smsVerifyDto);
        if (verify) {
            return ResponseEntity.ok("인증이 되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증에 실패했습니다.");
        }
    }

}
