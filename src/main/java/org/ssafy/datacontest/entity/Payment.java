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
    private String orderNum; // 토스페이 - 주문번호
    @Setter
    private LocalDateTime approvedAt; // 토스페이 - 결제 승인 날짜
    private int totalAmount; // 토스페이 - 결제 금액
    private String status; // 결제 상태 (예: 성공, 실패, 준비 등)

    @ManyToOne
    @JoinColumn(name = "art_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Payment(String paymentKey, LocalDateTime approvedAt, int totalAmount, String orderNum, Article article, User user, String status) {
        this.paymentKey = paymentKey;
        this.orderNum = orderNum;
        this.article = article;
        this.user = user;
        this.approvedAt = approvedAt;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

}
