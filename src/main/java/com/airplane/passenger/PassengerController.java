package com.airplane.passenger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.XML;

//import org.hibernate.annotations.common.util.impl.Log_.logger;

//import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.airplane.reservation.*;

import edu.sjsu.models.Flight;
import edu.sjsu.models.Passenger;
import edu.sjsu.models.Reservation;

@RestController
public class PassengerController {
	@Autowired
	private PassengerService passengerService;
	
	@Autowired
	private ReservationService reservationService;
	
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
	 * GET PASSENGER - /passenger/{id}?xml=true - METHOD -GET
	 * Gets ID as pathvariable and xml as request param. Returns JSON or XML
	 * @param id
	 * @param xml
	 * @return 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/passenger/{id}")
	public ResponseEntity getPassenger(@PathVariable("id") String id,
								  	   @RequestParam(value = "xml", required = false) String xml) {
		try {

			Passenger passenger = passengerService.getPassenger(id);
			if(passenger != null) {

				List<Reservation> reservations = reservationService.getReservations(passenger);
				
				JSONObject json = new JSONObject(buildPassengerResponse(passenger, reservations));
			
				if(xml != null) {
					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.setContentType(MediaType.APPLICATION_XML);
					return new ResponseEntity(XML.toString(json), responseHeaders, HttpStatus.OK);
				}
				else {
					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.setContentType(MediaType.APPLICATION_JSON);
					return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.OK);
				}	
			}
			else {
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.setContentType(MediaType.APPLICATION_JSON);
				String error = "Sorry, the requested passenger with id "+id+" does not exist";
				return new ResponseEntity(getErrorResponse("404",error), responseHeaders, HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity(getErrorResponse("400", ex.getMessage()), responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	
	
	
public Map<String, Object> buildPassengerResponse(Passenger passenger, List<Reservation> reservations) {
		
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("id", passenger.getId());
		response.put("firstname", passenger.getFirstname());
		response.put("lastname", passenger.getLastname());
		response.put("age", passenger.getAge());
		response.put("gender", passenger.getGender());
		response.put("phone", passenger.getPhone());
		
		List<Map<String, Object>> reservation = new ArrayList<>();
		SimpleDateFormat target = new SimpleDateFormat("yyyy-MM-dd-HH");
		for(Reservation r : reservations) {
			Map<String, Object> indReservation = new LinkedHashMap<>();
			indReservation.put("orderNumber", r.getOrderNumber());
			indReservation.put("price", r.getPrice());
			
			List<Map<String, Object>>flight = new ArrayList<>();
			for(Flight f : r.getFlights()) {
				Map<String, Object> indFlight = new LinkedHashMap<>();
				indFlight.put("number", f.getNumber());
				indFlight.put("price", f.getPrice());
				indFlight.put("from", f.getFrom());
				indFlight.put("to", f.getTo());
				indFlight.put("departureTime", target.format(f.getDepartureTime()));
				indFlight.put("arrivalTime", target.format(f.getArrivalTime()));
				indFlight.put("description", f.getDescription());
				indFlight.put("plane", f.getPlane());
				flight.add(indFlight);
			}
			
			HashMap<String, Object> flMap = new LinkedHashMap<>();
			flMap.put("flight", flight);
			
			indReservation.put("flights", flMap);
			reservation.add(indReservation);
		}
		HashMap<String, Object> rMap = new LinkedHashMap<>();
		rMap.put("reservation", reservation);
		
		response.put("reservations",rMap);
		Map<String, Object> Response = new LinkedHashMap<>();
		Response.put("passenger", response);
		return Response;
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
