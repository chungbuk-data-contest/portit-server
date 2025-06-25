package org.ssafy.datacontest.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.ssafy.datacontest.dto.company.LikedArticleResponse;
import org.ssafy.datacontest.dto.register.UserRegisterRequest;
import org.ssafy.datacontest.dto.user.UserAlertResponse;
import org.ssafy.datacontest.dto.user.UserResponse;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.Like;
import org.ssafy.datacontest.entity.User;

import java.util.List;

public class UserMapper {
    public static User toEntity(UserRegisterRequest userRegisterRequest, PasswordEncoder passwordEncoder) {
        return User.builder()
                .loginId(userRegisterRequest.getLoginId())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .nickname(userRegisterRequest.getNickname())
                .phoneNum(userRegisterRequest.getPhoneNum())
                .profileImage(userRegisterRequest.getProfileImage())
                .role("ROLE_USER")
                .deleted(false)
                .build();
    }

    public static UserResponse toResponse(User user, List<LikedArticleResponse> myArticles) {
        return UserResponse.builder()
                .userId(user.getId())
                .userNickname(user.getNickname())
                .userLoginId(user.getLoginId())
                .myArticles(myArticles)
                .build();
    }

    public static UserAlertResponse toAlertResponse(Company company, Like like, Article article) {
        return UserAlertResponse.builder()
                .companyId(company.getCompanyId())
                .companyName(company.getCompanyName())
                .articleTitle(article.getTitle())
                .readed(like.isReaded())
                .build();
    }
}
