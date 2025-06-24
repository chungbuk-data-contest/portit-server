package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.premium.PremiumResponse;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Premium;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.PremiumMapper;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.PremiumRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.PremiumService;
import org.ssafy.datacontest.validation.ArticleValidation;
import org.ssafy.datacontest.validation.PremiumValidation;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final ArticleRepository articleRepository;
    private final ArticleValidation articleValidation;
    private final PremiumValidation premiumValidation;

    @Override
    @Transactional
    public PremiumResponse registerPremium(Long articleId, String userName) {
        // 유저 권한 확인
        User user = userRepository.findByLoginId(userName);

        log.info(user.toString());
        Article article = articleRepository.findByArtId(articleId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.ARTICLE_NOT_FOUND));

        articleValidation.checkUserAuthorizationForArticle(user, article);
        premiumValidation.isRegisteredPremium(articleId);

        // 결제 로직

        // DB 등록
        article.updatePremium(true);
        Premium premium = premiumRepository.save(PremiumMapper.toEntity(article));

        return PremiumMapper.toResponse(premium);
    }
}
