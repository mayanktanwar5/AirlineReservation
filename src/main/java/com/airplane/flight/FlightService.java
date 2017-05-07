package com.airplane.flight;

import com.airplane.passenger.Passenger;
import com.airplane.passenger.PassengerRepoData;
import com.airplane.reservation.PlaneReservation;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Service
public class FlightService {

    @Autowired
    private FlightRepoData flightRepoData;

    public Flight save(Flight flight) {
        try {
            flightRepoData.save(flight);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return flight;
    }
    public Flight saveOrUpdateFlight(Flight flight) {

        if(!flightRepoData.exists(flight.getNumber())) {
            try {
                flightRepoData.save(flight);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return flight;
        }

        Flight currentFlight=flightRepoData.getOne(flight.getNumber());
        List<Passenger> passengers=currentFlight.getPassengers();
        if(passengers==null) {
            System.out.println(" We havent found any passenger for the flight we are updating/Saving it now");
            return flight;
        }

        List<Date> newFlightTimming=new ArrayList<>();
        newFlightTimming.add(flight.getDepartureTime());
        newFlightTimming.add(flight.getArrivalTime());


        for(Passenger passenger:passengers){
            List<PlaneReservation> reservations=passenger.getReservation();
            for(PlaneReservation reservation:reservations){
                Set<Flight> bookedFlights=reservation.getFlights();
                for(Flight bookedFlight:bookedFlights){
                    if(bookedFlight != currentFlight){
                        List<Date> reservedTiming=new ArrayList<>();
                        reservedTiming.add(bookedFlight.getDepartureTime());
                        reservedTiming.add(bookedFlight.getArrivalTime());
                        if(overlap(newFlightTimming,reservedTiming))
                            return null;
                    }
                }
            }
        }

        try {
            flightRepoData.save(flight);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return flight;
    }

    private boolean overlap(List<Date> updateTime, List<Date> reservedTime) {
        if(updateTime.get(1).compareTo(reservedTime.get(0))<0) return false;
        if(updateTime.get(0).compareTo(reservedTime.get(1))>0) return false;
        return true;
    }

    public List<Flight> getAllFlights() {
        List<Flight> results=null;
        try{
            results=flightRepoData.findAll();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return results;
    }

    public Flight getFlight(String id) {
        try {
            Flight flight= flightRepoData.findOne(id);
            return flight;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteFlight(String id) {
        try {
            Flight flight=flightRepoData.findOne(id);
            if(flight==null){
                System.out.println("Flight id :"+id+" does not exist.");
                return false;
            }
            //System.out.println("Passengers  in flight :"+flight.getPassengers());
            if(flight.getPassengers()!=null && flight.getPassengers().size()>0)
                return false;
            flightRepoData.delete(id);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Passenger addPassenger(Flight flight, Passenger passenger) {
        if(flight.getSeatsLeft()<=0)
            return null;
        //System.out.println("Flight :"+flight+" passenger :"+passenger);
        flight.addPassenger(passenger);
        flight.setSeatsLeft(flight.getSeatsLeft()-1);
        flightRepoData.save(flight);
        return passenger;
    }

    public void setReservation(Flight flight,PlaneReservation reservation) {
        flight.setReservation(reservation);
    }
}
