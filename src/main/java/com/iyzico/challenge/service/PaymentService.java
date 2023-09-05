package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;


@Service
public class PaymentService {

    private SeatService seatService;

    private IyzicoPaymentService iyzicoPaymentService;

    private SeatRepository seatRepository;

    public PaymentService(SeatService seatService, IyzicoPaymentService iyzicoPaymentService, SeatRepository seatRepository) {
        this.seatService = seatService;
        this.iyzicoPaymentService = iyzicoPaymentService;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public synchronized boolean purchaseSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId).orElse(null);
        synchronized (seat) {
            if (!seat.isBooked()) {
                seat.setBooked(true);
                seatRepository.save(seat);
                return true;
            } else
                return false;
        }
    }

    @Transactional
    public boolean call(BigDecimal price, Long seatNum) {
        if (purchaseSeat(seatNum)) {
            iyzicoPaymentService.pay(price);
            return true;
        } else {
            return false;
        }
    }
}
