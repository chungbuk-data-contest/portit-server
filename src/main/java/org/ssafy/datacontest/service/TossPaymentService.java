package org.ssafy.datacontest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ssafy.datacontest.dto.payment.TossPaymentPrepareRequest;
import org.ssafy.datacontest.dto.payment.TossPaymentPrepareResponse;
import org.ssafy.datacontest.dto.payment.TossPaymentRequest;
import org.ssafy.datacontest.dto.payment.TossPaymentResponse;

public interface TossPaymentService {
    TossPaymentResponse confirmPayment(TossPaymentRequest request) throws JsonProcessingException;

    TossPaymentPrepareResponse preparePayment(TossPaymentPrepareRequest request);
}
