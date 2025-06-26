package org.ssafy.datacontest.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.ssafy.datacontest.dto.article.ArticleScrollRequestDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.QArticle;
import org.ssafy.datacontest.enums.SortType;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Article> findNextPageByCursor(ArticleScrollRequestDto request) {
        QArticle article = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(article.deleted.eq(false));

        // 원래 코드(시은)
        // 카테고리 필터링
        if (request.getKeyword() == null || request.getKeyword().isBlank()) {
            if (request.getCategory() != null && !request.getCategory().isEmpty()) {
                builder.and(article.category.in(request.getCategory()));
            }
        }
        // 키워드 필터링 ( 제목 + 작성자명 )
        else {
            builder.and(article.title.containsIgnoreCase(request.getKeyword())
                    .or(article.user.nickname.containsIgnoreCase(request.getKeyword())));
        }


        // TODO: 시은이가 코드랑 주석 확인하기
        // 내가 바꾼 코드(동근)
        // 앱에서 기획 카테고리를 선택하고, keyword에 "test" 를 입력했을 때
        // 기획 카테고리에서 keyword에 test 가 존재하는 작품이 아닌,
        // 카테고리는 제외하고 keyword에 test가 포함된 작품을 다 반환함.
        // 승훈이 의도는 카테고리랑 keyword 둘 다 만족하는 작품을 반환받고 싶어하는데
        // 둘이 얘기해보고 둘 중 하나로 바꾼 다음 지워
//        // category 항상 적용
//                if (request.getCategory() != null && !request.getCategory().isEmpty()) {
//                    builder.and(article.category.in(request.getCategory()));
//                }
//
//        // keyword 있으면 검색 필터 추가
//                if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
//                    builder.and(article.title.containsIgnoreCase(request.getKeyword())
//                            .or(article.user.nickname.containsIgnoreCase(request.getKeyword())));
//                }
        // 정렬 기준별 커서 조건 추가
        switch (request.getSortType()) {
            case POPULAR -> {
                if (request.getLastLikes() != null && request.getLastTitle() != null) {
                    builder.and(article.likeCount.lt(request.getLastLikes())
                            .or(article.likeCount.eq(request.getLastLikes()).and(article.title.lt(request.getLastTitle()))));
                }
            }
            case LATEST -> {
                if (request.getLastUpdateTime() != null && request.getLastTitle() != null) {
                    builder.and(article.createdAt.lt(request.getLastUpdateTime())
                            .or(article.createdAt.eq(request.getLastUpdateTime()).and(article.title.lt(request.getLastTitle()))));
                }
            }
        }

        // 정렬 순서 정의
        List<OrderSpecifier<?>> orderSpecifiers = getArticleOrderSpecifiers(article, request.getSortType());

        List<Article> result = queryFactory
                .selectFrom(article)
                .join(article.user).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .limit(request.getSize() + 1)
                .fetch();

        boolean hasNext = result.size() > request.getSize();
        if (hasNext) result.remove(result.size() - 1);

        return new SliceImpl<>(result, PageRequest.of(0, request.getSize()), hasNext);
    }

    @Override
    public List<Article> findRandomPremiumArticles(int count) {
        QArticle article = QArticle.article;

        return queryFactory
                .selectFrom(article)
                .where(article.premium.eq(true).and(article.deleted.eq(false)))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(count)
                .fetch();
    }

    private List<OrderSpecifier<?>> getArticleOrderSpecifiers(QArticle article, SortType sortType) {
        return switch (sortType) {
            case POPULAR -> List.of(
                    new OrderSpecifier<>(Order.DESC, article.likeCount),
                    new OrderSpecifier<>(Order.DESC, article.title)
            );
            case LATEST -> List.of(
                    new OrderSpecifier<>(Order.DESC, article.createdAt),
                    new OrderSpecifier<>(Order.DESC, article.title)
            );
        };
    }

}
