package org.ssafy.datacontest.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.ssafy.datacontest.dto.toss.PaymentPrepareRequest;
import org.ssafy.datacontest.dto.toss.PaymentPrepareResponse;
import org.ssafy.datacontest.dto.toss.PaymentRequest;
import org.ssafy.datacontest.dto.toss.PaymentResponse;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.service.TossPaymentService;

import java.util.HashMap;
import java.util.Map;

@Service
public class TossPaymentServiceImpl implements TossPaymentService {

    @Value("${toss.secret.key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public PaymentResponse confirmPayment(PaymentRequest request) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, "");

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", request.getPaymentKey());
        body.put("orderId", request.getOrderId());
        body.put("amount", request.getAmount());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try{
            String url = "https://api.tosspayments.com/v1/payments/confirm";
            ResponseEntity<PaymentResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, PaymentResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e){
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(e.getResponseBodyAsString());
            String errorCode = root.path("code").asText();
            String errorMessage = root.path("message").asText();

            throw new CustomException(HttpStatus.BAD_REQUEST, errorCode, errorMessage);
        }
    }

    @Override
    public PaymentPrepareResponse preparePayment(PaymentPrepareRequest request) {
        return null;
    }
}
