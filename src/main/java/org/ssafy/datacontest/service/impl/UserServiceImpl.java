package org.ssafy.datacontest.service.impl;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.company.LikedArticleResponse;
import org.ssafy.datacontest.dto.user.UserResponse;
import org.ssafy.datacontest.dto.user.UserUpdateRequest;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Like;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ArticleLikeMapper;
import org.ssafy.datacontest.mapper.UserMapper;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    public UserServiceImpl(UserRepository userRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public UserResponse getUser(String username, Long userId) {
        User user = userRepository.findByLoginId(username);

        if(user.getId() != userId){
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_USER);
        }

        List<Article> userArticles = articleRepository.findByUser_Id(userId);

        List<LikedArticleResponse> likedArticleResponses = new ArrayList<>();
        if (!userArticles.isEmpty()) {
            likedArticleResponses = userArticles.stream()
                    .map(ArticleLikeMapper::toLikedArticleResponse)
                    .toList();
        }

        return UserMapper.toResponse(user, likedArticleResponses);
    }

    @Override
    @Transactional
    public Long updateUser(UserUpdateRequest userUpdateRequest, String userName, Long userId) {
        User user = userRepository.findByLoginId(userName);

        if(user.getId() != userId){
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_USER);
        }

        user.updateUser(userUpdateRequest.getUserNickname());
        return userId;
    }
}
