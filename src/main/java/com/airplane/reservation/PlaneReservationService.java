package com.airplane.reservation;

import com.airplane.flight.Flight;
import com.airplane.flight.FlightRepoData;
import com.airplane.flight.FlightService;
import com.airplane.passenger.Passenger;
import com.airplane.passenger.PassengerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.FieldRetrievingFactoryBean;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Vivek Agarwal on 5/2/2017.
 */

@Service
public class PlaneReservationService {
    @Autowired
    ReservationRepoData reservationRepository;
    @Autowired
    FlightService flightService;
    @Autowired
    PassengerService passengerService;

    public PlaneReservation getReservation(int id) {
        PlaneReservation reservation=null;
        try {
            reservation = reservationRepository.findOne(id);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return reservation;
    }

    public PlaneReservation makeReservation(int passengerID, List<String> flightLists) {
        List<Flight> flightsToBeReserved=new ArrayList<>();
        int cost=0;
        Passenger passenger=passengerService.getPassenger(passengerID);

        if(passenger==null) {
            System.out.println("Passenger with id :"+passengerID+" not present!");
            return null;
        }

        System.out.println("List of flights------:"+flightLists+"\n\n\n");
        for(String flightId : flightLists){
            Flight flight=flightService.getFlight(flightId);
            if(flight==null) {
                System.out.println("Flight with flightId :"+flightId+" not present!");
                return null;
            }
            cost+=flight.getPrice();
            flightsToBeReserved.add(flight);
        }

        for(int i=0;i<flightsToBeReserved.size();i++){
            for(int j=i+1;j<flightsToBeReserved.size();j++){
                if(flightsToBeReserved.get(i).conflict(flightsToBeReserved.get(j)))
                {
                    System.out.println("Conflict while reserving flight :"+flightsToBeReserved.get(i).toString()+"\n\nand\n\n"+flightsToBeReserved.get(j).toString());
                    return null;
                }
            }
            List<PlaneReservation> otherReservations=passenger.getReservation();
            for(PlaneReservation reservation:otherReservations){
                if(overlap(reservation,flightsToBeReserved.get(i))) {
                    System.out.println("Rservation conflict with other reservation :"+reservation.toString());
                    return null;
                }
            }
            if(flightService.addPassenger(flightsToBeReserved.get(i),passenger)==null) {
                System.out.println("Unable to add passenger to flight :"+flightsToBeReserved.get(i).toString());
                return null;
            }
        }

        PlaneReservation reservation=new PlaneReservation();

        for(Flight flight:flightsToBeReserved){
            System.out.println("Flight :"+flight+" \n\n");
            reservation.addFlight(flight);
        }

        reservation.setPassenger(passenger);
        reservation.setPrice(cost);

        System.out.println("Passenger who booked this reservation :"+reservation.getPassenger().getFirstname());

        try {
            reservationRepository.save(reservation);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        System.out.print(reservation.getFlights());

        return reservation;
    }

    public List<PlaneReservation> searchReservation(Integer passengerID, String from, String to, String flightNumber) {
        List<PlaneReservation> searchResults=null;
        try {
            searchResults = reservationRepository.findByPassengerIdOrFlightNumberOrFromSourceOrToDestination(passengerID, from, to, flightNumber);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return searchResults;
    }

    public boolean deleteReservation(Integer id) {
        try {
            PlaneReservation reservation=reservationRepository.findOne((int)id);
            if(reservation==null){
                System.out.println("Reservation with id :"+id+" does not exist!");
                return false;
            }
            Set<Flight> reservedFlights=reservation.getFlights();
            Flight flights[]=new Flight[reservedFlights.size()];
            int i=0;
            for(Flight flight:reservedFlights)
                flights[i++]=flight;

            for(Flight flight:flights){
                flight.removePassenger(reservation.getPassenger());
//                flight.setSeatsLeft(flight.getSeatsLeft()+1);
                flightService.save(flight);
            }

            try {
                System.out.println("Deleting Reservation now.. \n\n\n");
                reservationRepository.delete((int) id);
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean addFlights(Integer id, List<String> flightsAdded) {
        PlaneReservation reservation=reservationRepository.getReservation(id);
        if(reservation==null) return false;

        for(String flightNumber:flightsAdded){
            Flight flight=flightService.getFlight(flightNumber);
            if(flight==null)
                return false;
            if(!overlap(reservation,flight)) {
                List<PlaneReservation> otherReservations=reservation.getPassenger().getReservation();
                for(PlaneReservation otherReservation:otherReservations){
                    if(overlap(otherReservation,flight))
                        return false;
                }
                reservation.addFlight(flight);
            }
            else
                return false;
        }
        try {
            reservationRepository.save(reservation);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean overlap(PlaneReservation reservation, Flight flight) {
        Set<Flight> flights=reservation.getFlights();

        for(Flight bookedFlight:flights){
            if(bookedFlight.conflict(flight)) {
             System.out.println("Conflict between flight \n\n"+flight.toString()+" \n\nand \n\n"+bookedFlight.toString());
                return true;
            }
        }

        return false;
    }


    public boolean removeFlights(Integer id, List<String> flightsRemoved) {
        PlaneReservation reservation=reservationRepository.getReservation(id);
        if(reservation==null) return false;
        Set<Flight> bookedFlights=reservation.getFlights();

        for(String flightNumber:flightsRemoved){
            Flight flight=flightService.getFlight(flightNumber);
            if(flight==null || !bookedFlights.contains(flight))
                return false;
            bookedFlights.remove(flight);
        }

        try {
            reservationRepository.save(reservation);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
