package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.dto.payment.PaymentResponse;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Payment;
import org.ssafy.datacontest.entity.Premium;
import org.ssafy.datacontest.entity.User;

public class PaymentMapper {

    public static Payment toEntity(Article article, User user) {
        return Payment.builder()
                .article(article)
                .totalAmount(990)
                .user(user)
                .build();
    }

    public static PaymentResponse toResponse(Payment payment, Article article, Premium premium) {
        return PaymentResponse.builder()
                .articleId(article.getArtId())
                .articleName(article.getTitle())
                .premium(article.isPremium())
                .price(payment.getTotalAmount())
                .thumbnailUrl(article.getThumbnailUrl())
                .payDate(payment.getApprovedAt())
                .startDate(premium.getStartAt())
                .endDate(premium.getEndAt())
                .build();
    }
}
