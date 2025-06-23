package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.sms.SmsRequest;
import org.ssafy.datacontest.dto.sms.SmsVerify;

public interface SmsService {
    void sendSms(SmsRequest smsRequest);
    boolean verifyCode(SmsVerify verifyDto);
}
