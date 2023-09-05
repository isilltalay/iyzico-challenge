package com.iyzico.challenge.controller;

import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.service.IyzicoPaymentService;
import com.iyzico.challenge.service.PaymentService;
import com.iyzico.challenge.service.PaymentServiceClients;
import com.iyzico.challenge.service.SeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;
    @Autowired
    SeatService seatService;

    @PostMapping("/purchase/{seatId}")
    @Async
    public ResponseEntity<String> purchaseSeat(@PathVariable Long seatId) {
        Seat seat = seatService.getOneSeat(seatId);
        try {
            if (seat != null) {
                BigDecimal price = seat.getFlight().getPrice();
                boolean result = paymentService.call(price, seatId);
                if (result) {
                    logger.info("Seat purchased successfully");
                    return ResponseEntity.ok("Seat purchased successfully");

                } else {
                    logger.error("Seat is already booked");
                    ResponseEntity.badRequest().body("Seat is already booked");
                }
            }
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Seat is booked");
            logger.error("Seat is booked");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Seat is booked");
    }
}