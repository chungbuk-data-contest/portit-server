package org.ssafy.datacontest.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
public class FcmUtil {

    private static final String FIREBASE_KEY_FILE = "firebase/portit-firebase.json";

    @PostConstruct
    public void initialize() {
        try {
            List<FirebaseApp> apps = FirebaseApp.getApps();
            if (apps == null || apps.isEmpty()) {
                InputStream credential = new ClassPathResource(FIREBASE_KEY_FILE).getInputStream();
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(credential))
                        .build();
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp initialized");
            } else {
                log.info("FirebaseApp already initialized");
            }
        } catch (IOException e) {
            log.error("Firebase initialization failed: {}", e.getMessage());
        }
    }

    public boolean sendMessage(String targetToken, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(notification)
                .build();

        return send(message);
    }

    public boolean sendNotificationWithDataMessage(String targetToken, String title, String body,
                                                   String data1, String data2) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(notification)
                .putData("data1", data1)
                .putData("data2", data2)
                .build();

        return send(message);
    }

    private boolean send(Message message) {
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: {}", response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("FCM 메시지 전송 실패: {}", e.getMessage());
            return false;
        }
    }
}