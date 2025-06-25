package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.article.ArticlesResponseDto;
import org.ssafy.datacontest.dto.premium.PremiumResponse;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Payment;
import org.ssafy.datacontest.entity.Premium;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ArticleMapper;
import org.ssafy.datacontest.mapper.PaymentMapper;
import org.ssafy.datacontest.mapper.PremiumMapper;
import org.ssafy.datacontest.repository.*;
import org.ssafy.datacontest.service.PremiumService;
import org.ssafy.datacontest.validation.ArticleValidation;
import org.ssafy.datacontest.validation.PremiumValidation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final ArticleRepository articleRepository;
    private final PaymentRepository paymentRepository;
    private final TagRepository tagRepository;
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
        paymentRepository.save(PaymentMapper.toEntity(article, user));
        Payment payment = paymentRepository.findByArticle_artId(article.getArtId());
        article.updatePremium(true);
        Premium premium = premiumRepository.save(PremiumMapper.toEntity(article, payment));

        return PremiumMapper.toResponse(premium);
    }

    @Override
    public List<ArticlesResponseDto> getPremiumArticles() {
        List<Article> premiumArticles = articleRepository.findRandomPremiumArticles(4);
        List<Long> premiumIds = premiumArticles.stream()
                .map(Article::getArtId)
                .toList();

        Map<Long, List<String>> premiumTagMap = getTagMapByArticleIds(premiumIds);
        List<ArticlesResponseDto> premiumDtoList = mapArticlesToDtoList(premiumArticles, premiumTagMap);

        return premiumDtoList;
    }

    private List<ArticlesResponseDto> mapArticlesToDtoList(List<Article> articles, Map<Long, List<String>> tagMap) {
        return articles.stream()
                .map(article -> ArticleMapper.toArticlesResponseDto(
                        article,
                        tagMap.getOrDefault(article.getArtId(), List.of())
                ))
                .toList();
    }

    private Map<Long, List<String>> getTagMapByArticleIds(List<Long> articleIds) {
        List<Object[]> rawTagData = tagRepository.findTagsByArticleIds(articleIds);

        return rawTagData.stream()
                .collect(Collectors.groupingBy(
                        row -> ((Number) row[0]).longValue(),
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));
    }
}
