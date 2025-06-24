package org.ssafy.datacontest.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.repository.PremiumRepository;

@Component
public class PremiumValidation {
    private final PremiumRepository premiumRepository;

    public PremiumValidation(PremiumRepository premiumRepository) {
        this.premiumRepository = premiumRepository;
    }

    public void isRegisteredPremium(Long articleId) {
        if(premiumRepository.findByArticle_ArtId(articleId).isPresent()){
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.REGISTERED_ARTICLE_PREMIUM);
        }
    }
}
