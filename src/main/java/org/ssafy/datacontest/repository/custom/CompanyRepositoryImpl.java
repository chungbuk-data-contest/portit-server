package org.ssafy.datacontest.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.ssafy.datacontest.dto.company.CompanyScrollRequest;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.QArticle;
import org.ssafy.datacontest.entity.QCompany;
import org.ssafy.datacontest.enums.IndustryType;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Company> findNextPageByCompanyName(CompanyScrollRequest req) {
        QCompany company = QCompany.company;
        BooleanBuilder builder = new BooleanBuilder();

        if (req.getKeyword() == null || req.getKeyword().isBlank()) {
            if (req.getCompanyField() != null && !req.getCompanyField().isEmpty()) {
                builder.and(company.companyField.in(req.getCompanyField()));
            }
            if(req.getCompanyLoc() != null && !req.getCompanyLoc().isEmpty()) {
                builder.and(company.companyLoc.in(req.getCompanyLoc()));
            }
            if(req.getHiring() != null){
                builder.and(company.hiring.eq(req.getHiring()));
            }
        } else {
            builder.and(company.companyName.containsIgnoreCase(req.getKeyword()));
        }

        if(req.getCompanyId() != null && req.getCompanyId() >= 0) {
            builder.and(company.companyId.gt(req.getCompanyId()));
        }

        List<Company> result = queryFactory
                .selectFrom(company)
                .orderBy(company.companyId.asc())
                .where(builder)
                .limit(req.getSize() + 1)
                .fetch();

        boolean hasNext = result.size() > req.getSize();
        if (hasNext) result.remove(result.size() - 1);

        return new SliceImpl<>(result, PageRequest.of(0, req.getSize()), hasNext);
    }

    @Override
    public List<Company> findRandomCompany(IndustryType industryType) {
        QCompany company = QCompany.company;

        BooleanBuilder builder = new BooleanBuilder();

        if(industryType != null) {
            builder.and(company.companyField.eq(industryType));
        }

        return queryFactory
                .selectFrom(company)
                .where(builder)
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(3)
                .fetch();
    }

}
