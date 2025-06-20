package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.sms.SmsRequestDto;

public interface SmsService {
    void sendSms(SmsRequestDto smsRequestDto);
}
