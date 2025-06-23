package org.ssafy.datacontest.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.client.PublicApiClient;
import org.ssafy.datacontest.dto.company.ArticleLikeResponse;
import org.ssafy.datacontest.dto.publicApi.PublicCompanyDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.Like;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ArticleLikeMapper;
import org.ssafy.datacontest.mapper.ArticleMapper;
import org.ssafy.datacontest.mapper.CompanyMapper;
import org.ssafy.datacontest.repository.ArticleLikeRepository;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.service.CompanyService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final PublicApiClient publicApiClient;
    private final PasswordEncoder passwordEncoder;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;

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
                    .build();
            articleLikeRepository.save(newLike);
            article.increaseLikeCount();
        }

        articleRepository.save(article);

        return ArticleLikeMapper.toResponse(!like.isPresent(), article);
    }
}
