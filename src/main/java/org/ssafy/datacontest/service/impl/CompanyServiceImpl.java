package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.client.PublicApiClient;
import org.ssafy.datacontest.dto.SliceResponseDto;
import org.ssafy.datacontest.dto.company.*;
import org.ssafy.datacontest.dto.publicApi.PublicCompanyDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.Like;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ArticleLikeMapper;
import org.ssafy.datacontest.mapper.ArticleMapper;
import org.ssafy.datacontest.mapper.CompanyMapper;
import org.ssafy.datacontest.repository.ArticleLikeRepository;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.CompanyService;
import org.ssafy.datacontest.util.FcmUtil;
import org.ssafy.datacontest.validation.CompanyValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PublicApiClient publicApiClient;
    private final PasswordEncoder passwordEncoder;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final CompanyValidation companyValidation;
    private final FcmUtil fcmUtil;

    @Transactional // 중간에 하나라도 실패하면 전부 롤백
    public void fetchAndSaveCompanies() {
        // api 부르기
        List<PublicCompanyDto> publicCompany = publicApiClient.fetchData();

        // random 값으로 나머지 필드 채우기
        List<Company> companies = CompanyMapper.publicDataToEntity(publicCompany, passwordEncoder);

        companyRepository.saveAll(companies);
        log.info("기업 {}건 저장됨.", companies.size());
    }

    @Override
    @Transactional
    public ArticleLikeResponse toggleLike(Long articleId, String companyName) {
        Company company = companyRepository.findByLoginId(companyName);
        Article article = articleRepository.findByArtId(articleId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.ARTICLE_NOT_FOUND));
        Optional<Like> like = articleLikeRepository.findByCompany_CompanyIdAndArticle_ArtId(company.getCompanyId(), articleId);

        if (like.isPresent()) {
            articleLikeRepository.delete(like.get());
            article.decreaseLikeCount();
        } else {
            Like newLike = Like.builder()
                    .company(company)
                    .article(article)
                    .readed(false)
                    .user(article.getUser())
                    .build();
            articleLikeRepository.save(newLike);
            sendLikeNotification(company, article);
            article.increaseLikeCount();
        }

        articleRepository.save(article);

        return ArticleLikeMapper.toResponse(!like.isPresent(), article);
    }

    private void sendLikeNotification(Company company, Article article) {
        String targetToken = article.getUser().getFcmToken();

        if (targetToken == null || targetToken.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FCM_TOKEN_NOT_FOUND);
        }

        String title = "작품에 관심이 등록되었어요!";
        String body = String.format("'%s' 작품에 '%s' 기업이 관심을 표현했습니다.", article.getTitle(), company.getCompanyName());
        fcmUtil.sendMessage(targetToken, title, body);
        log.info("FCM Notification send succes");
    }

    @Override
    public SliceResponseDto<CompanyScrollResponse> getCompaniesByCursor(CompanyScrollRequest companyScrollRequest) {
        Slice<Company> companies = companyRepository.findNextPageByCompanyName(companyScrollRequest);
        List<Company> companyList = companies.getContent();

        List<CompanyScrollResponse> dtoList = companyList.stream()
                .map(company -> CompanyMapper.toCompanyScrollResponse(
                        company
                )).toList();

        return new SliceResponseDto<>(dtoList, companies.hasNext());
    }

    @Override
    public CompanyResponse getCompany(String companyName) {
        Company company = companyRepository.findByLoginId(companyName);

        if(company == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_USER);
        }

        List<Like> likedArticles = articleLikeRepository.findByCompany_CompanyId(company.getCompanyId());

        List<LikedArticleResponse> likedArticleResponses = new ArrayList<>();
        if (!likedArticles.isEmpty()) {
            likedArticleResponses = likedArticles.stream()
                    .map(like -> {
                        Article article = articleRepository.findByArtId(like.getArticle().getArtId())
                                .orElse(null);
                        if (article != null) {
                            return ArticleLikeMapper.toLikedArticleResponse(article);
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        }

        return CompanyMapper.toCompanyResponse(company, likedArticleResponses);
    }

    @Override
    @Transactional
    public Long updateCompany(CompanyUpdateRequest companyUpdateRequest, String companyName) {
        Company company = companyRepository.findByLoginId(companyName);

        if(company == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_USER);
        }

        companyValidation.validateUpdateCompany(companyUpdateRequest);

        company.updateCompany(companyUpdateRequest);
        return company.getCompanyId();
    }
}
