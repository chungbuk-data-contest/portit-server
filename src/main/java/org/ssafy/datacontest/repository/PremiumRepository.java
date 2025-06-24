package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Premium;

import java.util.Optional;

public interface PremiumRepository extends JpaRepository<Premium, Long> {
    Optional<Premium> findByArticle_ArtId(Long articleId);
}
