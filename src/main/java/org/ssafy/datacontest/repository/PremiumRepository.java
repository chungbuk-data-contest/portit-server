package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Premium;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PremiumRepository extends JpaRepository<Premium, Long> {
    Optional<Premium> findByArticle_ArtId(Long articleId);
    List<Premium> findAllByEndAtBefore(LocalDateTime time);
    Premium findByPayment_paymentId(Long paymentId);
}
