package com.iyzico.challenge.controller;

import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.exceptions.SeatAlreadyBookedException;
import com.iyzico.challenge.requests.AddSeatToFlightRequest;
import com.iyzico.challenge.requests.FlightSeatRequest;
import com.iyzico.challenge.service.SeatService;
import com.iyzico.challenge.requests.SeatUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/seats")
public class SeatController {
    @Autowired
    private SeatService seatService;

    @PostMapping("/addSeatToFlight")
    public ResponseEntity<String> addSeatToFlight(@RequestBody AddSeatToFlightRequest seatRequest) {
        return seatService.addSeatToFlight(seatRequest);
    }

    @GetMapping("/flight/{flightId}/available")
    public FlightSeatRequest getAvailableSeatsForFlight(@PathVariable Long flightId) {
        FlightSeatRequest availableSeats = seatService.getAvailableSeatsForFlight(flightId);
        return availableSeats;
    }

    @DeleteMapping("/remove/{seatId}")
    public void removeSeatFromFlight(@PathVariable Long seatId) throws SeatAlreadyBookedException {
        seatService.removeSeatFromFlight(seatId);
    }

    @PutMapping("/update/{seatId}")
    public ResponseEntity<Seat> updateSeat(@PathVariable Long seatId, @RequestBody SeatUpdateRequest seatUpdateRequest) {
        return seatService.updateSeat(seatId, seatUpdateRequest);
    }

    @GetMapping("/{seatId}/price")
    public ResponseEntity<BigDecimal> getSeatPrice(@PathVariable Long seatId) {
        BigDecimal price = seatService.getSeatPrice(seatId);
        return ResponseEntity.ok(price);
    }
}
