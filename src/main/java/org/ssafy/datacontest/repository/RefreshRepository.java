package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Refresh;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {
}
