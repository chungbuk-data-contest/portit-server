package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.premium.PremiumResponse;

public interface PremiumService {
    PremiumResponse registerPremium(Long articleId, String userName);
}
