package org.ssafy.datacontest.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.fcm.FcmTokenRequest;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.FcmService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class FcmServiceImpl implements FcmService {
    public static final String FIREBASE_KEY_FILE = "firebase/portit-firebase.json";
    private static final String API_URL ="https://fcm.googleapis.com/v1/projects/portit-ac611/messages:send";
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public FcmServiceImpl(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @PostConstruct
    public void initialize() {
        try {
            FirebaseApp firebaseApp = null;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            //Firebase app이 다시 로딩되지 않도록.
            if(firebaseApps != null && !firebaseApps.isEmpty()){
                for(FirebaseApp app : firebaseApps){
                    if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                        firebaseApp = app;
                    }
                }
            }else{
                //Firebase initialize
                InputStream credential = new ClassPathResource(FIREBASE_KEY_FILE).getInputStream();
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(credential))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (FileNotFoundException e) {
            log.error("Firebase ServiceAccountKey FileNotFoundException" + e.getMessage());
        } catch (IOException e) {
            log.error("FirebaseOptions IOException" + e.getMessage());
        }
    }

    @Override
    public void saveOrUpdateToken(FcmTokenRequest fcmTokenRequest, String loginId, String role) {
        String token = fcmTokenRequest.getFcmToken();
        if ("ROLE_USER".equals(role)) {
            User user = userRepository.findByLoginId(loginId);
            if(user == null){
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND);
            }

            if (!token.equals(user.getFcmToken())) {
                user.setFcmToken(token);
                userRepository.save(user);
            }

        } else if ("ROLE_COMPANY".equals(role)) {
            Company company = companyRepository.findByLoginId(loginId);
            if(company == null){
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.COMPANY_NOT_FOUND);
            }

            if (!token.equals(company.getFcmToken())) {
                company.setFcmToken(token);
                companyRepository.save(company);
            }
        }
    }

    public boolean sendMessage(String targetToken, String title, String body) throws IOException {
        Notification noti = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(noti)
                .build();

        log.info("title : {}", message.toString());

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            // Response is a message ID string.
            log.info("Successfully sent message: {}", response);
            return true;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendNotificationWithDataMessage(String targetToken, String title, String body, String data1, String data2) throws IOException {

        Notification noti = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(noti)
                .putData("data1", data1)
                .putData("data2", data2)
                .build();

        log.info("title : {}", message.toString());

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            // Response is a message ID string.
            log.info("Successfully sent message: {}", response);
            return true;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
