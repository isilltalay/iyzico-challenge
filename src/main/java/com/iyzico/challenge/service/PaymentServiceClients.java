package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentServiceClients {

    private IyzicoPaymentService iyzicoPaymentService;

    public PaymentServiceClients(IyzicoPaymentService iyzicoPaymentService) {
        this.iyzicoPaymentService = iyzicoPaymentService;
    }

    @Async
    public CompletableFuture<String> call(BigDecimal price) {
        iyzicoPaymentService.pay(price);
        return CompletableFuture.completedFuture("success");
    }
}
