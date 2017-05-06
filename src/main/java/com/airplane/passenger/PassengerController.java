package com.airplane.passenger;

import java.util.HashMap;
import java.util.Map;

//import org.hibernate.annotations.common.util.impl.Log_.logger;

//import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PassengerController {
	@Autowired
	private PassengerService passengerService;
	
	/** 
	 * CREATE PASSENGER - /passenger - METHOD - POST
	 * @author mayanktanwar
	 * @param firstname
	 * @param lastname
	 * @param age
	 * @param gender
	 * @param phone
	 * @return Passenger
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(method=RequestMethod.POST,value="/passenger") 
	public ResponseEntity createPassenger( @RequestBody Passenger passenger) 
	{		
	try{
		return passengerService.createPassenger(passenger);
	}
	catch(Exception ex){
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity(getErrorResponse("400",ex.getMessage()), responseHeaders, HttpStatus.BAD_REQUEST);
	}
		
	}
		/**
	 * Builds a hashmap for bad requests.
	 * @param errorcode
	 * @param error
	 * @return HashMap<String, String>
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, Map> getErrorResponse(String errorcode, String error) {
		HashMap<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("code", errorcode);
		errorMap.put("msg", error);
		HashMap<String, Map> errorResponse = new HashMap<String, Map>();
		errorResponse.put("Badrequest", errorMap);
		return errorResponse;
	}
	
}
