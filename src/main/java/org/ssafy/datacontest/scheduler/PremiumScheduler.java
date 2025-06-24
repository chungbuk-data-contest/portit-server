package org.ssafy.datacontest.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Premium;
import org.ssafy.datacontest.repository.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PremiumScheduler {

    private final PremiumRepository premiumRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 00:00 실행
    @Transactional
    public void checkExpiredPremiums() {
        List<Premium> expired = premiumRepository.findAllByEndAtBefore(LocalDateTime.now());
        for (Premium p : expired) {
            Article article = p.getArticle();
            article.setPremium(false);
            premiumRepository.delete(p);
        }
    }
}
