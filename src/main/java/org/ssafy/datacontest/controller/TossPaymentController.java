package org.ssafy.datacontest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.dto.toss.TossPaymentRequest;
import org.ssafy.datacontest.dto.toss.TossPaymentResponse;
import org.ssafy.datacontest.service.TossPaymentService;

@RestController
@RequestMapping("/toss")
public class TossPaymentController {
    private final TossPaymentService tossPaymentService;

    @Autowired
    public TossPaymentController(TossPaymentService tossPaymentService) {
        this.tossPaymentService = tossPaymentService;
    }

    @PostMapping("/confirm")
    public ResponseEntity<TossPaymentResponse> confirmPayment(@RequestBody TossPaymentRequest request) throws JsonProcessingException {
        TossPaymentResponse response = tossPaymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }
}
