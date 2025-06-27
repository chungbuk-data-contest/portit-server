package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.dto.premium.PremiumResponse;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Payment;
import org.ssafy.datacontest.entity.Premium;

import java.time.LocalDateTime;

public class PremiumMapper {

    public static Premium toEntity(Article article, Payment payment) {
        return Premium.builder()
                .article(article)
                .payment(payment)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(14))
                .build();
    }

    public static PremiumResponse toResponse(Premium premium) {
        return PremiumResponse.builder()
                .message("프리미엄 등록이 완료되었습니다.")
                .articleId(premium.getArticle().getArtId())
                .startAt(premium.getStartAt())
                .endAt(premium.getEndAt())
                .build();
    }
}
