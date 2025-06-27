package org.ssafy.datacontest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ssafy.datacontest.dto.payment.PaymentResponse;
import org.ssafy.datacontest.dto.payment.TossPaymentPrepareRequest;
import org.ssafy.datacontest.dto.payment.TossPaymentPrepareResponse;
import org.ssafy.datacontest.dto.payment.TossPaymentRequest;
import org.ssafy.datacontest.dto.payment.TossPaymentResponse;

import java.util.List;

public interface PaymentService {
    List<PaymentResponse> getPayments(String username);

    TossPaymentPrepareResponse preparePayment(TossPaymentPrepareRequest request);

    TossPaymentResponse confirmPayment(TossPaymentRequest request) throws JsonProcessingException;
}
