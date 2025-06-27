package org.ssafy.datacontest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ssafy.datacontest.dto.toss.PaymentPrepareRequest;
import org.ssafy.datacontest.dto.toss.PaymentPrepareResponse;
import org.ssafy.datacontest.dto.toss.PaymentRequest;
import org.ssafy.datacontest.dto.toss.PaymentResponse;

public interface TossPaymentService {
    PaymentResponse confirmPayment(PaymentRequest request) throws JsonProcessingException;

    PaymentPrepareResponse preparePayment(PaymentPrepareRequest request);
}
