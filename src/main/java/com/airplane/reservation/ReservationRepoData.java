package com.airplane.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.transaction.Transactional;

@Transactional
public interface ReservationRepoData extends JpaRepository<PlaneReservation, Integer>{

    List<PlaneReservation> findByPassengerId(int passenger_id);

    @Query(value = "select reservation.* from reservation, flight_passengers, flight where flight_passengers.passengers_passenger_id=ifnull(?1,flight_passengers.passengers_passenger_id) and flight.from_source=ifnull(?2,flight.from_source) and flight.to_destination=ifnull(?3,flight.to_destination) and flight.flight_number=ifnull(?4,flight.flight_number) and reservation.passenger_id=flight_passengers.passengers_passenger_id group by passenger_id", nativeQuery = true)
    List<PlaneReservation> findByPassengerIdOrFlightNumberOrFromSourceOrToDestination(Integer passenger_id,String from, String to,String flightNumber);

    @Query(value="select * from reservation where order_number=?1",nativeQuery = true)
    PlaneReservation getReservation(int reservationId);
}
