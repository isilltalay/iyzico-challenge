package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.repository.SeatRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@EnableAsync
public class PaymentServiceTest {

    @InjectMocks
    private IyzicoPaymentService iyzicoPaymentService;
    @InjectMocks
    private FlightService flightService;
    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private SeatRepository seatRepository;

    @Test
    public void testPurchaseSeat_Successful() {
        Seat mockSeat = new Seat();
        mockSeat.setBooked(false);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(mockSeat));
        when(seatRepository.save(mockSeat)).thenReturn(mockSeat);

        boolean result = paymentService.purchaseSeat(1L);

        assertTrue(result);
        assertTrue(mockSeat.isBooked());

        verify(seatRepository, times(1)).save(mockSeat);

    }

    @Test
    public void testPurchaseSeat_AlreadyBooked() {
        Seat seat = new Seat();
        seat.setBooked(true);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));

        boolean result = paymentService.purchaseSeat(1L);

        assertFalse(result);
        assertTrue(seat.isBooked());

    }

    @Test
    public void testFindSeatById() {
        Long seatId = 1L;

        Seat mockSeat = new Seat();
        mockSeat.setId(seatId);
        mockSeat.setSeatNumber("5");
        mockSeat.setBooked(false);

        when(seatRepository.findById(seatId)).thenReturn(Optional.of(mockSeat));

        Seat actualSeat = seatRepository.findById(seatId).orElse(null);

        Assert.assertEquals(mockSeat, actualSeat);
    }

    @Test
    public void testCall_PurchaseSuccess_PaymentSuccess() {
        Seat mockSeat = new Seat();
        BigDecimal price = BigDecimal.valueOf(2500.00);

        Long seatNum = 2L;
        // mockSeat.setBooked(true);
        when(seatRepository.save(mockSeat)).thenReturn(mockSeat);

        when(seatRepository.findById(seatNum)).thenReturn(Optional.of(mockSeat));
        when(paymentService.purchaseSeat(seatNum)).thenReturn(true);

        boolean result = paymentService.call(price, seatNum);

        assertTrue(result);

        verify(paymentService, times(1)).purchaseSeat(seatNum);
        verify(iyzicoPaymentService, times(1)).pay(price);
    }

    @Test
    public void testCall_PurchaseFailure_PaymentNotCalled() {
        BigDecimal price = BigDecimal.valueOf(100);
        Long seatNum = 1L;

        when(paymentService.purchaseSeat(seatNum)).thenReturn(false);

        boolean result = paymentService.call(price, seatNum);

        assertFalse(result);

        verify(paymentService, times(1)).purchaseSeat(seatNum);
        verify(iyzicoPaymentService, never()).pay(price);
    }
}
