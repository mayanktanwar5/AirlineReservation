package com.airplane.passenger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class PassengerService {
	@Autowired
	private PassengerRepository passengerRepository;
	public ResponseEntity createPassenger(Passenger passenger)
	{
		
		//Passenger passenger = new Passenger();
		
		passengerRepository.save(passenger);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity(passenger, responseHeaders, HttpStatus.OK);
		//System.out.println(passenger.getId());
	}
}
