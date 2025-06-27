package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.ssafy.datacontest.entity.Payment;

import java.util.List;

@Component
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUser_Id(Long userId);
    Payment findByArticle_artId(Long artId);
    Payment findByOrderNum(String orderNum);
}
