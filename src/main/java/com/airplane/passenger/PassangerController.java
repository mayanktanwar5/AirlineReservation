package com.airplane.passenger;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.airplane.excep.ImportantException;
import com.airplane.flight.*;
import com.airplane.reservation.*;


@RestController
@RequestMapping("/airline")
public class PassangerController {

	@Autowired
	private PassengerService passengerService; 
	
	@GetMapping(value="/allPassengers")
	public List<Passenger> getAllPassengers(@RequestParam(value="xml") String xml){
		return passengerService.getAllPassengers();
	}
	
	@GetMapping("/passenger/{id}")
	public ResponseEntity/*Passenger*/ getPassenger(@PathVariable int id, @RequestParam(value="json", required=false) String json, @RequestParam(value="xml", required=false) boolean xml) throws Exception{
		if(passengerService.getPassenger(id) ==  null)
		{
			ImportantException e = new ImportantException();
			if(xml == true){
				e.setXml(true);
			}
			e.setCode(404);
			e.setMessage("Sorry passenger with "+id+" couldnot be found, please retry with correct id");
			throw e;
		}
		
		JSONObject responseObj = passengerXML(passengerService.getPassenger(id));
		
		if(xml == true){
			System.out.println("Inside xml response");
			return new ResponseEntity(XML.toString(responseObj), HttpStatus.OK);
		}
		
		return new ResponseEntity(responseObj.toString(), HttpStatus.OK);
		//return ResponseEntity.ok(res.toString());
	}
		
	@PostMapping("/passenger")
	public ResponseEntity createPassenger(@RequestParam(value="firstname", required=true) String firstName, @RequestParam(value="lastname", required=true) String lastName, @RequestParam(value="age", required=true) int age, @RequestParam(value="gender", required=true) String gender, @RequestParam(value="phone", required=true) String phone) throws Exception{
		Passenger passenger = new Passenger();
		passenger.setFirstname(firstName);
		passenger.setLastname(lastName);
		passenger.setGender(gender);
		passenger.setAge(age);
		passenger.setPhone(phone);
		if(passengerService.createPassenger(passenger) ==  null)
		{
			
			ImportantException e = new ImportantException();
			e.setCode(404);
			e.setMessage("The mobile number is already registered with us please enter a new number");
			throw e;
		}
		ModelMap resultMap = new ModelMap();
		resultMap.addAttribute("passenger", passengerFormat(passenger));
		return new ResponseEntity(resultMap, HttpStatus.OK);
	}
	
	@PutMapping("/passenger/{id}")
	public ResponseEntity updatePassenger(@PathVariable int id, @RequestParam(value="firstname", required=true) String firstName, @RequestParam(value="lastname", required=true) String lastName, @RequestParam(value="age", required=true) int age, @RequestParam(value="gender", required=true) String gender, @RequestParam(value="phone", required=true) String phone) throws Exception{
		if(passengerService.updatePassenger(id, firstName, lastName, age, gender, phone) ==  null)
		{
			
			ImportantException e = new ImportantException();
			e.setCode(404);
			e.setMessage("Sorry we couldnot update your information");
			throw e;
		}
		ModelMap resultMap = new ModelMap();
		resultMap.addAttribute("passenger", passengerFormat(passengerService.updatePassenger(id, firstName, lastName, age, gender, phone)));
		return new ResponseEntity(resultMap, HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}") 
	public List<Passenger> deletePassenger(@PathVariable int id) throws ImportantException {
		;
		if(!passengerService.deletePassenger(id))
		{
			ImportantException e = new ImportantException();
			e.setCode(404);
			e.setMessage("We coulnot found the "+ id+ "in our database, make sure you enter a correct id");
			throw e;
		}
		return passengerService.getAllPassengers();
	}
	
	public JSONObject passengerXML(Passenger passenger){
		JSONObject jsonResult = new JSONObject();
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
			JSONObject jsonO = new JSONObject(passengerFormat(passenger));
			Field map = jsonO.getClass().getDeclaredField("map");
			map.setAccessible(true);//because the field is private final...
			map.set(jsonO, new LinkedHashMap<>());
			map.setAccessible(false);//return flag
			jsonO.put("id", Integer.toString(passenger.getPassengerId()));
			jsonO.put("firstname", passenger.getFirstname());
			jsonO.put("lastname", passenger.getLastname());
			jsonO.put("age", Integer.toString(passenger.getAge()));
			jsonO.put("gender", passenger.getGender());
			jsonO.put("phone", passenger.getPhone());
			List<PlaneReservation> reservations = passenger.getReservation();
			List<JSONObject> reservationList = new ArrayList<JSONObject>();
			if(reservations != null){
				for(PlaneReservation reservation:reservations){
					JSONObject reservationObject = new JSONObject();
					Field iMap = reservationObject.getClass().getDeclaredField("map");
					iMap.setAccessible(true);//because the field is private final...
					iMap.set(reservationObject, new LinkedHashMap<>());
					iMap.setAccessible(false);//return flag
					reservationObject.put("orderNumber", reservation.getOrderNumber());
					reservationObject.put("price", reservation.getPrice());
					List<JSONObject> flightList = new ArrayList<JSONObject>();
					for(Flight flight: reservation.getFlights()){
						JSONObject flightObj = new JSONObject();
						Field fMap = flightObj.getClass().getDeclaredField("map");
						fMap.setAccessible(true);//because the field is private final...
						fMap.set(flightObj, new LinkedHashMap<>());
						fMap.setAccessible(false);//return flag
						flightObj.put("number", flight.getNumber());
						flightObj.put("price", flight.getPrice());
						flightObj.put("from", flight.getFromSource());
						flightObj.put("to", flight.getToDestination());
						flightObj.put("departureTime", dateFormat.format(flight.getDepartureTime()));
						flightObj.put("arrivalTime", dateFormat.format(flight.getArrivalTime()));
						flightObj.put("description", flight.getDescription());
						JSONObject plane = new JSONObject();
						Field pMap = plane.getClass().getDeclaredField("map");
						fMap.setAccessible(true);//because the field is private final...
						fMap.set(plane, new LinkedHashMap<>());
						fMap.setAccessible(false);//return flag
						plane.put("capacity", flight.getPlane().getCapacity());
						plane.put("model", flight.getPlane().getModel());
						plane.put("manufacturer", flight.getPlane().getManufacturer());
						plane.put("yearOfManufacture", flight.getPlane().getYearOfManufacture());
						flightObj.put("plane", plane);
						JSONObject f =new JSONObject();
						f.put("flight", flightObj);
						flightList.add(f);
					}
					JSONObject resList = new JSONObject();
					JSONArray flJsArray = new JSONArray(flightList);
					reservationObject.put("flights", flJsArray);
					resList.put("reservation", reservationObject);
					reservationList.add(resList);
				}
			}
			JSONArray resJsArray = new JSONArray(reservationList);
			jsonO.put("reservations", resJsArray);
			jsonResult.put("passenger", jsonO);
		}catch(Exception e){
			System.out.println(e);
		}
		return jsonResult;
	}

	
	public ModelMap passengerFormat(Passenger passenger){
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("id", passenger.getPassengerId());
		modelMap.addAttribute("firstname", passenger.getFirstname());
		modelMap.addAttribute("lastname", passenger.getLastname());
		modelMap.addAttribute("age", passenger.getAge());
		modelMap.addAttribute("gender", passenger.getGender());
		modelMap.addAttribute("phone", passenger.getPhone());
		List<PlaneReservation> reservations = new ArrayList<PlaneReservation>();
		reservations=passenger.getReservation();
		List<ModelMap> reservationList = new ArrayList<ModelMap>();
		if(reservations != null){
			for(PlaneReservation reservation:reservations){
				ModelMap reservationModelMap = new ModelMap();
				reservationModelMap.addAttribute("orderNumber", reservation.getOrderNumber());
				reservationModelMap.addAttribute("price", reservation.getPrice());
				List<ModelMap> flightList = new ArrayList<ModelMap>();
				for(Flight flight: reservation.getFlights()){
					ModelMap flightMap = new ModelMap();
					flightMap.addAttribute("flight", flight);
					flightList.add(flightMap);
				}
				ModelMap resList = new ModelMap();
				reservationModelMap.addAttribute("flights", flightList);
				resList.addAttribute("reservation", reservationModelMap);
				reservationList.add(resList);
			}
		}
		modelMap.addAttribute("reservations", reservationList);
		return modelMap;
	}
	
}