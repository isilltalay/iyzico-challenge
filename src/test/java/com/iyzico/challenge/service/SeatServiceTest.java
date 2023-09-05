package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.exceptions.SeatAlreadyBookedException;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.requests.AddSeatToFlightRequest;
import com.iyzico.challenge.requests.FlightSeatRequest;
import com.iyzico.challenge.requests.SeatUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class SeatServiceTest {
    @InjectMocks
    private SeatService seatService;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private SeatRepository seatRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void addSeatToFlight() {
        Flight mockFlight = new Flight();
        mockFlight.setId(11L);

        AddSeatToFlightRequest request = new AddSeatToFlightRequest();
        request.setFlightId(11L);
        request.setSeatNum("A1");
        request.setBooked(false);

        when(flightRepository.findById(11L)).thenReturn(Optional.of(mockFlight));
        ResponseEntity<String> response = seatService.addSeatToFlight(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void removeSeatFromFlight() throws SeatAlreadyBookedException {
        Seat mockSeat = new Seat();
        mockSeat.setBooked(false);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(mockSeat));

        seatService.removeSeatFromFlight(1L);

        verify(seatRepository, Mockito.times(1)).delete(mockSeat);
    }

    @Test
    void updateSeat() {
        Seat mockSeat = new Seat();
        mockSeat.setId(1L);
        mockSeat.setFlight(new Flight());

        SeatUpdateRequest updateRequest = new SeatUpdateRequest();
        updateRequest.setNewSeatNumber("B1");
        updateRequest.setNewBookedStatus(true);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(mockSeat));
        when(seatRepository.save(any(Seat.class))).thenReturn(mockSeat);

        ResponseEntity<Seat> response = seatService.updateSeat(1L, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("B1", response.getBody().getSeatNumber());
        assertTrue(response.getBody().isBooked());
    }

    @Test
    void getAvailableSeatsForFlight() {
        Flight mockFlight = new Flight();
        mockFlight.setFlightName("Test Flight");
        mockFlight.setPrice(BigDecimal.valueOf(100));
        mockFlight.setDescription("Test description");

        List<Seat> mockAvailableSeats = new ArrayList<>();
        mockAvailableSeats.add(new Seat(mockFlight, "A1", false));
        mockAvailableSeats.add(new Seat(mockFlight, "A2", false));

        when(flightRepository.findById(1L)).thenReturn(Optional.of(mockFlight));
        when(seatRepository.findByFlightIdAndIsBooked(1L, false)).thenReturn(mockAvailableSeats);

        FlightSeatRequest response = seatService.getAvailableSeatsForFlight(1L);

        assertEquals("Test Flight", response.getFlightName());
        assertEquals(2, response.getAvailableSeats().size());
        assertEquals(BigDecimal.valueOf(100), response.getPrice());
        assertEquals("Test description", response.getDescription());
    }

    @Test
    void getSeatPrice() {
        Seat mockSeat = new Seat();
        mockSeat.setFlight(new Flight());
        mockSeat.getFlight().setPrice(BigDecimal.valueOf(150));

        when(seatRepository.findById(1L)).thenReturn(Optional.of(mockSeat));

        BigDecimal price = seatService.getSeatPrice(1L);

        assertEquals(BigDecimal.valueOf(150), price);
    }

    @Test
    void getOneSeat() {
        Seat mockSeat = new Seat();
        mockSeat.setId(1L);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(mockSeat));

        Seat seat = seatService.getOneSeat(1L);
        assertNotNull(seat);
        assertEquals(Long.valueOf(1), seat.getId());
    }
}