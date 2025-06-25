package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.payment.PaymentResponse;

import java.util.List;

public interface PaymentService {
    List<PaymentResponse> getPayments(String username);
}
