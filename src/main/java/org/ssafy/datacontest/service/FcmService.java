package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.fcm.FcmTokenRequest;

public interface FcmService {
    void saveOrUpdateToken(FcmTokenRequest fcmTokenRequest, String username, String role);

    void sendLikeNotification(String loginId, Long articleId);
}
