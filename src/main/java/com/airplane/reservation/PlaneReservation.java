package com.airplane.reservation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.airplane.flight.Flight;
import com.airplane.passenger.Passenger;

@Entity
@Table(name = "reservation")
public class PlaneReservation {

    @Id
    @GeneratedValue
    @Column(name="order_number")
    private int orderNumber;

    private int price;

    @ManyToOne
    @JsonBackReference
    private Passenger passenger;

	@ManyToMany()
	@JoinTable(name="reservation_flight", joinColumns= {@JoinColumn(name="order_number")},
			inverseJoinColumns = {@JoinColumn(name="flight_number")})
	@JsonManagedReference
	private Set<Flight> flights=new HashSet<>();

    public int getOrderNumber() {
        return orderNumber;
    }
    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public Passenger getPassenger() {
        return passenger;
    }
    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Set<Flight> getFlights(){return flights;}


    public void addFlight(Flight flight) {
        if(this.flights==null)
        this.flights.add(flight);
    }
}