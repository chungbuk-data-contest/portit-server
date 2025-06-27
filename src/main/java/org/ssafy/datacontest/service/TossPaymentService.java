package org.ssafy.datacontest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ssafy.datacontest.dto.toss.TossPaymentRequest;
import org.ssafy.datacontest.dto.toss.TossPaymentResponse;

public interface TossPaymentService {
    TossPaymentResponse confirmPayment(TossPaymentRequest request) throws JsonProcessingException;
}
