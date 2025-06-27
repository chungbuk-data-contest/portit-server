package org.ssafy.datacontest.dto.toss;

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
    private String orderId;
    private String amount;
}
