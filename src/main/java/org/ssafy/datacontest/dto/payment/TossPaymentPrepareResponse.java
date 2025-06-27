package org.ssafy.datacontest.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentPrepareResponse {
    private String orderId;
    private int amount;
    private Long articleId;
}
