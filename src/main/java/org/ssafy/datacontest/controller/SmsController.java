package org.ssafy.datacontest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.dto.sms.SmsRequestDto;
import org.ssafy.datacontest.service.SmsService;

@RestController
@RequestMapping("/sms")
public class SmsController {
    private final SmsService smsService;

    @Autowired
    public SmsController(SmsService smsService){
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> SendSMS(@RequestBody SmsRequestDto smsRequestDto){
        smsService.sendSms(smsRequestDto);
        return ResponseEntity.ok("문자를 전송했습니다.");
    }

//    @PostMapping("/verify")
//    public ResponseEntity<?> verifyCode(@RequestBody @Valid SmsVerifyDto smsVerifyDto){
//        boolean verify = smsService.verifyCode(smsVerifyDto);
//        if (verify) {
//            return ResponseEntity.ok("인증이 되었습니다.");
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증에 실패했습니다.");
//        }
//    }

}
