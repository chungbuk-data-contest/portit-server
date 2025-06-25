package org.ssafy.datacontest.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private Long articleId;
    private String articleName;
    private String thumbnailUrl;
    private boolean premium;
    private int price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime payDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

}
