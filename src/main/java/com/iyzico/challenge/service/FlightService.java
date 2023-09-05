package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.requests.FlightSeatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private SeatRepository seatRepository;

    public FlightSeatRequest addFlight(FlightSeatRequest flight) {
        Flight toSave = new Flight();
       // List<Seat> availableSeats = seatRepository.findByFlightIdAndIsBooked(toSave.getId(), false);
        toSave.setPrice(flight.getPrice());
        toSave.setFlightName(flight.getFlightName());
        toSave.setDescription(flight.getDescription());
        flightRepository.save(toSave);
        return flight;
    }
    public void deleteFlight(Long id){
        flightRepository.deleteById(id);
    }
    public FlightSeatRequest updateFlight(Long flightId, FlightSeatRequest flight){
        Optional<Flight> flight1 = flightRepository.findById(flightId);
        if(flight1.isPresent()){
            Flight flightToUpdate = flight1.get();
            flightToUpdate.setFlightName(flight.getFlightName());
            flightToUpdate.setDescription(flight.getDescription());
            flightToUpdate.setPrice(flight.getPrice());
            flightRepository.save(flightToUpdate);
            return flight;
        }
        else{
            return null;
        }
    }
    public List<Flight> getAllFlight(){
        return flightRepository.findAll();
    }

    public Flight getFlightById(Long flightId) {
        return flightRepository.findById(flightId).orElse(null);
    }
    public FlightSeatRequest getFlightDetails(Long flightId) {
        Flight flight = flightRepository.findById(flightId).orElse(null);
        List<Seat> availableSeats = seatRepository.findByFlightIdAndIsBooked(flightId,false);
        List<String> availableSeatNumbers = availableSeats.stream().map(Seat:: getSeatNumber).collect(Collectors.toList());
        FlightSeatRequest dto = new FlightSeatRequest();
        dto.setFlightName(flight.getFlightName());
        dto.setAvailableSeats(availableSeatNumbers);
        dto.setPrice(flight.getPrice());
        dto.setDescription(flight.getDescription());
        return dto;
    }
}
