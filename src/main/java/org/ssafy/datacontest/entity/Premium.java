package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "premium")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Premium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long premiumId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @CreationTimestamp
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Builder
    public Premium(Article article, Payment payment) {
        this.article = article;
        this.payment = payment;
        this.startAt = LocalDateTime.now();
        this.endAt = this.startAt.plusDays(14); // 프리미엄 2주
    }
}
