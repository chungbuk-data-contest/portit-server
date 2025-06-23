package org.ssafy.datacontest.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.email.EmailVerify;
import org.ssafy.datacontest.repository.EmailRepository;
import org.ssafy.datacontest.repository.SmsRepository;
import org.ssafy.datacontest.service.EmailService;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailRepository emailRepository;
    private final SmsRepository smsRepository;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender,
                            EmailRepository emailRepository, SmsRepository smsRepository) {
        this.javaMailSender = javaMailSender;
        this.emailRepository = emailRepository;
        this.smsRepository = smsRepository;
    }

    @Override
    public void sendEmail(String email) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성

            String subject = "[Port-It] 이메일 인증번호";
            String body = "다음 인증번호를 입력해 인증을 완료해주세요: " + certificationCode;

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);

            javaMailSender.send(mimeMessage);
            emailRepository.createEmailCertification(email, certificationCode);
        }
        catch (MessagingException e){
            log.error("Error Sending email", e);
        }
    }

    @Override
    public boolean verifyEmail(EmailVerify emailVerify) {
        if (isVerify(emailVerify.getEmail(), emailVerify.getCertificationCode())) { // 인증 코드 검증
            emailRepository.deleteEmailCertification(emailVerify.getEmail()); // 검증이 성공하면 Redis에서 인증 코드 삭제
            return true; // 인증 성공 반환
        } else {
            return false; // 인증 실패 반환
        }
    }

    private boolean isVerify(String email, String certificationCode) {
        log.info("isVerify email: {}, certificationCode: {}", email, emailRepository.getEmailCertification(email));
        return emailRepository.hasKey(email)
                && emailRepository.getEmailCertification(email).equals(certificationCode);
    }
}
