package org.ssafy.datacontest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.sms.SmsRequestDto;
import org.ssafy.datacontest.service.SmsService;
import org.ssafy.datacontest.util.SmsCertificationUtil;

@Service
public class SmsServiceImpl implements SmsService {
    private final SmsCertificationUtil smsCertificationUtil;

    @Autowired
    public SmsServiceImpl(SmsCertificationUtil smsCertificationUtil) {
        this.smsCertificationUtil = smsCertificationUtil;
    }

    @Override // SmsService 인터페이스 메서드 구현
    public void sendSms(SmsRequestDto smsRequestDto) {
        String phoneNum = smsRequestDto.getPhoneNum(); // SmsrequestDto에서 전화번호를 가져온다.
        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        smsCertificationUtil.sendSMS(phoneNum, certificationCode); // SMS 인증 유틸리티를 사용하여 SMS 발송
    }
}
