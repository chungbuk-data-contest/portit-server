package org.ssafy.datacontest.dto.payment;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentPrepareResponse {
    private String orderNum;
    private int amount;
    private Long articleId;
}
