package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.exceptions.SeatAlreadyBookedException;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.requests.AddSeatToFlightRequest;
import com.iyzico.challenge.requests.FlightSeatRequest;
import com.iyzico.challenge.requests.SeatUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private FlightRepository flightRepository;

    public ResponseEntity<String> addSeatToFlight(AddSeatToFlightRequest seatRequest){
        Seat seat = new Seat();
        Flight flight = flightRepository.findById(seatRequest.getFlightId()).orElse(null);
        String name  = seatRequest.getSeatNum();
        Seat currentSeat = seatRepository.findByFlightIdAndSeatNumber(flight.getId(),name);
        // aynı seatNum' ı flight'e eklediginde hata mesajı verilmesi icin refaktor yaptım.
        if (flight == null || currentSeat!=null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is a seat for this flight");
        }
            seat.setFlight(flight);
            seat.setSeatNumber(seatRequest.getSeatNum());
            seat.setBooked(seatRequest.isBooked());
            seatRepository.save(seat);
            return ResponseEntity.ok().build();
        }

    public void removeSeatFromFlight(Long seatId) throws SeatAlreadyBookedException {
        Seat seat = seatRepository.findById(seatId).orElse(null);
        if (!seat.isBooked()) {
            seatRepository.delete(seat);
        } else {
            throw new SeatAlreadyBookedException("Cannot remove a booked seat.");
        }
    }

    public ResponseEntity<Seat> updateSeat(Long seatId, SeatUpdateRequest seatUpdateRequest) {
        Seat seat = seatRepository.findById(seatId).orElse(null);

        if (seat == null || seat.getFlight() == null) {
            return ResponseEntity.badRequest().build();
        }

        seat.setSeatNumber(seatUpdateRequest.getNewSeatNumber());
        seat.setBooked(seatUpdateRequest.isNewBookedStatus());
        Seat updatedSeat = seatRepository.save(seat);

        return ResponseEntity.ok(updatedSeat);
    }

    public FlightSeatRequest getAvailableSeatsForFlight(Long flightId){
        Flight flight = flightRepository.findById(flightId).orElse(null);

        List<Seat> availableSeats = seatRepository.findByFlightIdAndIsBooked(flightId, false);
        List<String> availableSeatNumbers = availableSeats.stream().map(Seat::getSeatNumber).distinct().collect(Collectors.toList());

        FlightSeatRequest dto = new FlightSeatRequest();
        dto.setFlightName(flight.getFlightName());
        dto.setAvailableSeats(availableSeatNumbers);
        dto.setPrice(flight.getPrice());
        dto.setDescription(flight.getDescription());
        return dto;
    }

    public BigDecimal getSeatPrice(Long seatId){
        Seat seat = seatRepository.findById(seatId).orElse(null);
        Flight flight  = seat.getFlight();
        return flight.getPrice();
    }

    public Seat getOneSeat(Long id){
        return seatRepository.findById(id).orElse(null);
    }

}
