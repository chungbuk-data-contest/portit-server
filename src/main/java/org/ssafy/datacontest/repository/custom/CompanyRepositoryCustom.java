package org.ssafy.datacontest.repository.custom;

import org.springframework.data.domain.Slice;
import org.ssafy.datacontest.dto.company.CompanyScrollRequest;
import org.ssafy.datacontest.entity.Company;

public interface CompanyRepositoryCustom {
    Slice<Company> findNextPageByCompanyName(CompanyScrollRequest req);
}
