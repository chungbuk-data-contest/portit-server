package org.ssafy.datacontest.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentRequest {
    private String paymentKey;
    private Long articleId;
    private String orderId;
    private int amount;
}
