package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.dto.payment.PaymentResponse;
import org.ssafy.datacontest.dto.register.CustomUserDetails;
import org.ssafy.datacontest.service.PaymentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name="Payment", description = "결제 관련 API")
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("")
    @Operation(
            summary = "결제 정보 조회",
            description = "해당 유저에 대한 결제 내역을 조회합니다."
    )
    public ResponseEntity<List<PaymentResponse>> getPayments(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(paymentService.getPayments(userDetails.getUsername()));
    }

}
