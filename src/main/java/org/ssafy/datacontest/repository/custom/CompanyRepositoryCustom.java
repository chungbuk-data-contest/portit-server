package org.ssafy.datacontest.repository.custom;

import org.springframework.data.domain.Slice;
import org.ssafy.datacontest.dto.company.CompanyScrollRequest;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.enums.IndustryType;

import java.util.List;

public interface CompanyRepositoryCustom {
    Slice<Company> findCompaniesByScrollRequest(CompanyScrollRequest req);
    List<Company> findRandomCompany(IndustryType industryType);
}
