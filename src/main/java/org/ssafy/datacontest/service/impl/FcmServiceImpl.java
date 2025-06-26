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
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.FcmService;
import org.ssafy.datacontest.util.FcmUtil;

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
    private final ArticleRepository articleRepository;
    private final FcmUtil fcmUtil;

    @Autowired
    public FcmServiceImpl(UserRepository userRepository,
                          CompanyRepository companyRepository,
                          ArticleRepository articleRepository,
                          FcmUtil fcmUtil) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.articleRepository = articleRepository;
        this.fcmUtil = fcmUtil;
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

    @Override
    public boolean sendLikeNotification(String loginId, Long articleId) {
        Article article = articleRepository.findByArtId(articleId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.ARTICLE_NOT_FOUND));
        String targetToken = article.getUser().getFcmToken();
        Company company = companyRepository.findByLoginId(loginId);

        if (targetToken == null || targetToken.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FCM_TOKEN_NOT_FOUND);
        }

        String title = "작품에 관심이 등록되었어요!";
        String body = String.format("'%s' 작품에 '%s' 기업이 관심을 표현했습니다.", article.getTitle(), company.getCompanyName());

        return fcmUtil.sendMessage(targetToken, title, body);
    }
}
