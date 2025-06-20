package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.sms.SmsRequestDto;
import org.ssafy.datacontest.dto.sms.SmsVerifyDto;

public interface SmsService {
    void sendSms(SmsRequestDto smsRequestDto);
    boolean verifyCode(SmsVerifyDto verifyDto);
}
