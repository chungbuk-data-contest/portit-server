package org.ssafy.datacontest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.dto.toss.PaymentPrepareRequest;
import org.ssafy.datacontest.dto.toss.PaymentPrepareResponse;
import org.ssafy.datacontest.dto.toss.PaymentRequest;
import org.ssafy.datacontest.dto.toss.PaymentResponse;
import org.ssafy.datacontest.service.TossPaymentService;

@RestController
@RequestMapping("/toss")
public class TossPaymentController {
    private final TossPaymentService tossPaymentService;

    @Autowired
    public TossPaymentController(TossPaymentService tossPaymentService) {
        this.tossPaymentService = tossPaymentService;
    }

    @PostMapping("/prepare")
    public ResponseEntity<PaymentPrepareResponse> preparePayment(@RequestBody PaymentPrepareRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(tossPaymentService.preparePayment(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@RequestBody PaymentRequest request) throws JsonProcessingException {
        PaymentResponse response = tossPaymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }
}
