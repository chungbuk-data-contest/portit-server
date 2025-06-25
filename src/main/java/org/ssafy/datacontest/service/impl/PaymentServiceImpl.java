package org.ssafy.datacontest.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.payment.PaymentResponse;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

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
}
