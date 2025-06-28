package org.ssafy.datacontest.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.ssafy.datacontest.dto.payment.PaymentResponse;
import org.ssafy.datacontest.dto.payment.TossPaymentPrepareRequest;
import org.ssafy.datacontest.dto.payment.TossPaymentPrepareResponse;
import org.ssafy.datacontest.dto.payment.TossPaymentRequest;
import org.ssafy.datacontest.dto.payment.TossPaymentResponse;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Payment;
import org.ssafy.datacontest.entity.Premium;
import org.ssafy.datacontest.entity.User;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.PaymentMapper;
import org.ssafy.datacontest.mapper.PremiumMapper;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.PaymentRepository;
import org.ssafy.datacontest.repository.PremiumRepository;
import org.ssafy.datacontest.repository.UserRepository;
import org.ssafy.datacontest.service.PaymentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {


    @Value("${toss.secret.key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final PremiumRepository premiumRepository;

    @Override
    public List<PaymentResponse> getPayments(String username) {
        User user = userRepository.findByLoginId(username);
        if(user == null) throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND);

        List<Payment> payments = paymentRepository.findByUser_Id(user.getId());
        if(payments.isEmpty()) return new ArrayList<>();

        List<PaymentResponse> response = new ArrayList<>();
        for(Payment payment : payments) {
            Premium premium = premiumRepository.findByPayment_paymentId(payment.getPaymentId());
            if(premium == null) continue;

            Article article = articleRepository.findDeletedArticleById(premium.getArticle().getArtId());
            if(article == null) continue;

            response.add(PaymentMapper.toResponse(payment, article, premium));
        }

        return response;
    }

    @Override
    public TossPaymentPrepareResponse preparePayment(TossPaymentPrepareRequest request, String username) {
        Article article = articleRepository.findByArtId(request.getArticleId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.ARTICLE_NOT_FOUND));

        User user = userRepository.findByLoginId(username);
        if (user == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND);
        }
        if (article.isPremium()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.PREMIUM_ARTICLE);
        }

        if (paymentRepository.existsByUserAndArticleAndStatus(user, article, "READY")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_PURCHASE);
        }

        String orderNum = generateOrderNumber();

        Payment payment = PaymentMapper.toEntity(article, user, orderNum);
        paymentRepository.save(payment);

        return TossPaymentPrepareResponse.builder()
                .orderNum(orderNum)
                .amount(payment.getTotalAmount())
                .articleId(payment.getArticle().getArtId())
                .build();
    }

    public static String generateOrderNumber() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(dtf);

        int randomNumber = new Random().nextInt(99999); // 0~99999
        return "ORDER-" + timestamp + "-" + randomNumber;
    }
    @Override
    public TossPaymentResponse confirmPayment(TossPaymentRequest request) throws JsonProcessingException {
        Article article = articleRepository.findByArtId(request.getArticleId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.ARTICLE_NOT_FOUND));

        Payment payment = paymentRepository.findByOrderNum(request.getOrderId());
        if (payment == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PAYMENT_NOT_FOUND);
        }

        if(payment.getTotalAmount() != request.getAmount()){
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.AMOUNT_MISMATCH);
        }

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
            ResponseEntity<TossPaymentResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, TossPaymentResponse.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                payment.updateStatus("PAID");
                payment.setApprovedAt(LocalDateTime.now());
                paymentRepository.save(payment);
                article.updatePremium(true);
                Premium premium = premiumRepository.save(PremiumMapper.toEntity(article, payment));
                premiumRepository.save(premium);
            }

            return response.getBody();
        } catch (HttpClientErrorException e){
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(e.getResponseBodyAsString());
            String errorCode = root.path("code").asText();
            String errorMessage = root.path("message").asText();

            throw new CustomException(HttpStatus.BAD_REQUEST, errorCode, errorMessage);
        }
    }
}
