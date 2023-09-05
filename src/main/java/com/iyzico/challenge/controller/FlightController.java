package com.iyzico.challenge.controller;

import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.requests.FlightSeatRequest;
import com.iyzico.challenge.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {
    @Autowired
    private FlightService flightService;

    @GetMapping
    public List<Flight> getAllFlight() {
        return flightService.getAllFlight();
    }

    /* @GetMapping("/{flightId}")
     public Flight getFlightById(@PathVariable Long flightId){
         return flightService.getFlightById(flightId);
     }*/
    @PostMapping("/addFlight")
    public FlightSeatRequest addFlight(@RequestBody FlightSeatRequest flightSeatRequest) {
        return flightService.addFlight(flightSeatRequest);
    }

    @DeleteMapping("/deleteFlight/{id}")
    public void deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
    }

    @PutMapping("/{flightId}")
    public ResponseEntity<FlightSeatRequest> updateFlight(@PathVariable Long flightId, @RequestBody FlightSeatRequest flight) {
        FlightSeatRequest flightSeatRequest = flightService.updateFlight(flightId, flight);
        if (flightSeatRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightSeatRequest> getFlightDetails(@PathVariable Long flightId) {
        FlightSeatRequest flightDetails = flightService.getFlightDetails(flightId);
        return ResponseEntity.ok(flightDetails);
    }
}
