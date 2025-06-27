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

        // category 필터링
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            builder.and(article.category.in(request.getCategory()));
        }

        // keyword 필터링
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(article.title.containsIgnoreCase(request.getKeyword()));
            keywordBuilder.or(article.user.nickname.containsIgnoreCase(request.getKeyword()));
            builder.and(keywordBuilder);
        }

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
