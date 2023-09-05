package com.iyzico.challenge.repository;
import com.iyzico.challenge.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SeatRepository extends JpaRepository<Seat,Long>{
    List<Seat> findByFlightIdAndIsBooked(Long flightId, boolean isBooked);
    Seat findByFlightIdAndSeatNumber(Long flightId, String seatNum);
    Seat findBySeatNumber(String seatNumber);
}
