package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class IyzicoPaymentService {

    private Logger logger = LoggerFactory.getLogger(IyzicoPaymentService.class);

    private BankService bankService;
    private PaymentRepository paymentRepository;
    public IyzicoPaymentService(BankService bankService, PaymentRepository paymentRepository) {
        this.bankService = bankService;
        this.paymentRepository = paymentRepository;
    }

    @Async
    public CompletableFuture<Boolean> payment(BigDecimal price) {
        try {
            BankPaymentRequest request = new BankPaymentRequest();
            request.setPrice(price);
            BankPaymentResponse response = bankService.pay(request);

            //insert records
            Payment payment = new Payment();
            payment.setBankResponse(response.getResultCode());
            payment.setPrice(price);
            paymentRepository.save(payment);
            logger.info("Payment saved successfully!");
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            logger.error("An error occurred during payment: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }
    public void pay(BigDecimal price) {
        CompletableFuture<Boolean> paymentResult = payment(price);
        try {
            Boolean success = paymentResult.get();
            if (success) {
                logger.info("Payment completed successfully.");
            } else {
                logger.error("Payment failed.");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("An error occurred while waiting for payment result: " + e.getMessage());
        }
    }
}
