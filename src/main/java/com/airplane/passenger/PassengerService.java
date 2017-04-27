package com.airplane.passenger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {
	@Autowired
	private PassengerRepository passengerRepository;
	public void createPassenger(Passenger passenger)
	{
		
		//Passenger passenger = new Passenger(firstname, lastname, age, gender, phone);
		
		passengerRepository.save(passenger);
		System.out.println(passenger.getId());
		
		
	}
}
