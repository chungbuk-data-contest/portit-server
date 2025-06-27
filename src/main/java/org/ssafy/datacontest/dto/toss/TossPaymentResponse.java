package org.ssafy.datacontest.dto.toss;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentResponse {
    private String mId;
    private String orderId;
    private String paymentKey;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private int totalAmount;
}