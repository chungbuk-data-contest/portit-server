package org.ssafy.datacontest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/send")
    public ResponseEntity<Void> registerFcmToken(@RequestBody FcmTokenRequest fcmTokenRequest,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        fcmService.saveOrUpdateToken(fcmTokenRequest, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
