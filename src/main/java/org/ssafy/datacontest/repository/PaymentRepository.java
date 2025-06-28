package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Payment;
import org.ssafy.datacontest.entity.User;

import java.util.List;

@Component
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUser_Id(Long userId);
    Payment findByArticle_artId(Long artId);
    Payment findByOrderNum(String orderNum);

    boolean existsByUserAndArticle(User user, Article article);

    boolean existsByUserAndArticleAndStatus(User user, Article article, String ready);
}
