package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.requests.FlightSeatRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;
    @Mock
    private SeatRepository seatRepository;

    private Flight existingFlight;

    @BeforeEach
    public void setUp() {
        existingFlight = new Flight();
        existingFlight.setId(2L);
        existingFlight.setFlightName("Flight77");
        existingFlight.setPrice(BigDecimal.valueOf(2500L));
        existingFlight.setDescription("FlightDesc-2");
        when(flightRepository.findById(existingFlight.getId())).thenReturn(Optional.of(existingFlight));
    }
    @Test
    public void testAddFlight() {

        FlightSeatRequest flightDto = new FlightSeatRequest();
        flightDto.setPrice(BigDecimal.valueOf(2500L));
        flightDto.setFlightName("Test Flight");
        flightDto.setDescription("Test Description");

        Flight savedFlight = new Flight();
       // savedFlight.setId(10L);
        savedFlight.setPrice(flightDto.getPrice());
        savedFlight.setFlightName(flightDto.getFlightName());
        savedFlight.setDescription(flightDto.getDescription());

        when(flightRepository.save(any(Flight.class))).thenReturn(savedFlight);

        FlightSeatRequest addedFlight = flightService.addFlight(flightDto);

        verify(flightRepository, times(1)).save(any(Flight.class));

       // assertEquals(savedFlight.getId(), addedFlight.getId());
        assertEquals(savedFlight.getPrice(), addedFlight.getPrice());
        assertEquals(savedFlight.getFlightName(), addedFlight.getFlightName());
        assertEquals(savedFlight.getDescription(), addedFlight.getDescription());
    }


    @Test
    public void testDeleteFlight() {
        Long flightIdToDelete = 1L;
        doNothing().when(flightRepository).deleteById(anyLong());
        flightService.deleteFlight(flightIdToDelete);
    }

    @Test
    public void testUpdateExistingFlight() {
        FlightSeatRequest updatedFlightDto = new FlightSeatRequest();
        updatedFlightDto.setFlightName("New Flight Name");
        updatedFlightDto.setPrice(BigDecimal.valueOf(150.0));
        updatedFlightDto.setDescription("New Flight Description");

        when(flightRepository.findById(existingFlight.getId())).thenReturn(Optional.of(existingFlight));

        FlightSeatRequest result = flightService.updateFlight(existingFlight.getId(), updatedFlightDto);

        verify(flightRepository, times(1)).save(existingFlight);

        assertEquals(updatedFlightDto, result);
        assertEquals(updatedFlightDto.getFlightName(), existingFlight.getFlightName());
        assertEquals(updatedFlightDto.getPrice(), existingFlight.getPrice());
        assertEquals(updatedFlightDto.getDescription(), existingFlight.getDescription());

    }
    @Test
    public void testUpdateNonexistentFlight() {
        Long nonExistentFlightId = 2L;

        FlightSeatRequest updatedFlightDto = new FlightSeatRequest();
        updatedFlightDto.setFlightName("New Flight Name");
        updatedFlightDto.setPrice(BigDecimal.valueOf(150.0));
        updatedFlightDto.setDescription("New Flight Description");

        when(flightRepository.findById(nonExistentFlightId)).thenReturn(Optional.empty());

        FlightSeatRequest result = flightService.updateFlight(nonExistentFlightId, updatedFlightDto);

        verify(flightRepository, never()).save(any(Flight.class));

        assertEquals(null, result);
    }

    @Test
    public void testGetAllFlights() {
        List<Flight> mockFlights = new ArrayList<>();
        mockFlights.add(new Flight("Flight1", "Description1", BigDecimal.valueOf(1000)));
        mockFlights.add(new Flight("Flight2", "Description2", BigDecimal.valueOf(2000)));
        mockFlights.add(new Flight("Flight3", "Description3", BigDecimal.valueOf(3000)));

        when(flightRepository.findAll()).thenReturn(mockFlights);

        List<Flight> actualFlights = flightService.getAllFlight();

        assertEquals(mockFlights, actualFlights);
    }

    @Test
    public void getFlightById() {
        Long flightId = 2L;

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(existingFlight));

        Flight actualFlight = flightService.getFlightById(flightId);
        assertEquals(existingFlight, actualFlight);
    }

    @Test
    public void testGetFlightDetails() {

        List<Seat> availableSeats = new ArrayList<>();
        Seat seat = new Seat();
        seat.setSeatNumber("5");
        seat.setBooked(false);
        seat.setFlight(existingFlight);
        availableSeats.add(seat);

        when(flightRepository.findById(existingFlight.getId())).thenReturn(Optional.of(existingFlight));
        when(seatRepository.findByFlightIdAndIsBooked(existingFlight.getId(), false)).thenReturn(availableSeats);

        FlightSeatRequest dto = flightService.getFlightDetails(existingFlight.getId());

        assertEquals(existingFlight.getFlightName(), dto.getFlightName());
        assertEquals(existingFlight.getPrice(), dto.getPrice());
        assertEquals(existingFlight.getDescription(), dto.getDescription());
        List<String> expectedSeatNumbers = Arrays.asList("5");
        assertEquals(expectedSeatNumbers, dto.getAvailableSeats());
    }

}
