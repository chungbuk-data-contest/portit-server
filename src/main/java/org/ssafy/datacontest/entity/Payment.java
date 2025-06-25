package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String paymentKey; // 토스페이 - 결제키값
    private String orderId; // 토스페이 - 주문번호
    private LocalDateTime approvedAt; // 토스페이 - 결제 승인 날짜
    private int totalAmount; // 토스페이 - 결제 금액

    @ManyToOne
    @JoinColumn(name = "art_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Payment(String paymentKey, LocalDateTime approvedAt, int totalAmount, String orderId, Article article, User user) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.article = article;
        this.user = user;
        this.approvedAt = approvedAt;
        this.totalAmount = totalAmount;
    }
}
