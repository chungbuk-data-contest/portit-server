package org.ssafy.datacontest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.sms.SmsRequest;
import org.ssafy.datacontest.dto.sms.SmsVerify;
import org.ssafy.datacontest.repository.SmsRepository;
import org.ssafy.datacontest.service.SmsService;
import org.ssafy.datacontest.util.SmsCertificationUtil;

@Service
public class SmsServiceImpl implements SmsService {
    private final SmsCertificationUtil smsCertificationUtil;
    private final SmsRepository smsRepository;

    @Autowired
    public SmsServiceImpl(SmsCertificationUtil smsCertificationUtil,
                          SmsRepository smsRepository) {
        this.smsCertificationUtil = smsCertificationUtil;
        this.smsRepository = smsRepository;
    }

    @Override // SmsService 인터페이스 메서드 구현
    public void sendSms(SmsRequest smsRequest) {
        String phoneNum = smsRequest.getPhoneNum(); // SmsrequestDto에서 전화번호를 가져온다.
        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        smsCertificationUtil.sendSMS(phoneNum, certificationCode); // SMS 인증 유틸리티를 사용하여 SMS 발송
        smsRepository.createSmsCertification(phoneNum, certificationCode); // 인증 코드를 Redis에 저장
    }

    @Override // SmsService 인터페이스의 메서드를 구현
    public boolean verifyCode(SmsVerify smsVerify) {
        if (isVerify(smsVerify.getPhoneNum(), smsVerify.getCertificationCode())) { // 인증 코드 검증
            smsRepository.deleteSmsCertification(smsVerify.getPhoneNum()); // 검증이 성공하면 Redis에서 인증 코드 삭제
            return true; // 인증 성공 반환
        } else {
            return false; // 인증 실패 반환
        }
    }

    // 전화번호와 인증 코드를 검증하는 메서드
    public boolean isVerify(String phoneNum, String certificationCode) {
        return smsRepository.hasKey(phoneNum) && // 전화번호에 대한 키가 존재하고
                smsRepository.getSmsCertification(phoneNum).equals(certificationCode); // 저장된 인증 코드와 입력된 인증 코드가 일치하는지 확인
    }
}
