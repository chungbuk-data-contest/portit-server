package org.ssafy.datacontest.repository.custom;

import org.springframework.data.domain.Slice;
import org.ssafy.datacontest.dto.company.CompanyScrollRequest;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Company;

import java.util.List;

public interface CompanyRepositoryCustom {
    Slice<Company> findNextPageByCompanyName(CompanyScrollRequest req);
    List<Company> findRandomCompany(int count);
}
