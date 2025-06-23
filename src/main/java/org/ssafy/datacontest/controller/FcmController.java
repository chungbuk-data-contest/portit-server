package org.ssafy.datacontest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.dto.fcm.FcmTokenRequest;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.service.FcmService;

@RestController
@RequestMapping("/fcm")
public class FcmController {

    private final FcmService fcmService;

    @Autowired
    public FcmController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping("/token")
    public ResponseEntity<Void> registerFcmToken(@RequestBody FcmTokenRequest fcmTokenRequest,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        // ROLE_USER, ROLE_COMPANY 중 하나만 있다고 가정할 때
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // "ROLE_USER" 등
                .findFirst()                      // 여러 개 중 첫 번째
                .orElse("ROLE_USER");             // 없을 경우 기본값

        fcmService.saveOrUpdateToken(fcmTokenRequest, userDetails.getUsername(), role);
        return ResponseEntity.ok().build();
    }
}
