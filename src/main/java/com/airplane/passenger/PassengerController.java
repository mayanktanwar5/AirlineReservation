package com.airplane.passenger;

//import org.hibernate.annotations.common.util.impl.Log_.logger;

//import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PassengerController {
	@Autowired
	private PassengerService passengerService;
	@RequestMapping(method=RequestMethod.POST,value="/passenger") 
	public void createPassenger( @RequestBody Passenger passenger) 
	{		
	try{
		passengerService.createPassenger(passenger);
	}
	catch(Exception ex){
		
		System.out.println(ex.getMessage());
	}
		
	}
}
