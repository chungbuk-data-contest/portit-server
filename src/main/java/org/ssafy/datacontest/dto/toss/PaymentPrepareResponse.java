package org.ssafy.datacontest.dto.toss;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPrepareResponse {
    private String orderId;
    private int amount;
    private Long articleId;
}
