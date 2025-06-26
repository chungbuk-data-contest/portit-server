package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ssafy.datacontest.dto.fcm.FcmTokenRequest;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.service.FcmService;


@Tag(name = "FCM")
@RestController
@RequestMapping("/fcm")
public class FcmController {

    private final FcmService fcmService;

    @Autowired
    public FcmController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    @Operation(
            summary = "FCM 토큰 등록",
            description = """
        로그인 후 클라이언트에서 발급받은 **FCM 토큰을 서버에 등록**하는 API.  
        `Authorization` 헤더에 **AccessToken**이 있어야 하며,  
        등록한 토큰은 **알림 전송 시 사용됨**.

        ✅ 요청 전제조건  
        - 클라이언트가 Firebase에서 FCM 토큰을 발급받아야 함.  
        - 로그인 후 발급된 AccessToken을 Authorization 헤더에 담아야 함.
        """
    )
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
