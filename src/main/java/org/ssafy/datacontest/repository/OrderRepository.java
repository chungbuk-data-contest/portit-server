package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderNum(String orderNum);
}
