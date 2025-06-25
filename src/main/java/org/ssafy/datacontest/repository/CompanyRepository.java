package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.repository.custom.CompanyRepositoryCustom;

public interface CompanyRepository extends JpaRepository<Company, Long>, CompanyRepositoryCustom {
    Company findByLoginId(String email);
    boolean existsByLoginId(String email);
    Company findByCompanyId(Long companyId);
    // save(), saveAll(), findAll() 등 기본 제공
}
