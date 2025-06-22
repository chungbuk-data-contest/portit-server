package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByLoginId(String email);

    boolean existsByLoginId(String email);
    // save(), saveAll(), findAll() 등 기본 제공
}
