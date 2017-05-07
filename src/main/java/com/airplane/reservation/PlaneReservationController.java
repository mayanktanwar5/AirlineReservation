package com.airplane.reservation;

import com.airplane.excep.ImportantException;
import com.airplane.flight.Flight;
import com.airplane.passenger.Passenger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/reservation")
public class PlaneReservationController {

    @Autowired
    private PlaneReservationService reservationService;

    @GetMapping(value="/{id}")
    public ResponseEntity getReservation(@PathVariable int id) throws ImportantException {
        PlaneReservation resv = reservationService.getReservation(id);
    	JSONObject jsonObj = formatReservation(resv);
        if(resv==null){
            ImportantException e = new ImportantException();
            e.setCode(404);
            e.setMessage("The reservation with id "+id+" was not found, Enter a valid reservation Id");
            throw e;
        }
        return new ResponseEntity(jsonObj.toString(), HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity reserve(@RequestParam(value = "passengerId") int passengerID,@RequestParam(value="flightLists") List<String> flightLists) throws ImportantException, JSONException {
        PlaneReservation reservation=reservationService.makeReservation(passengerID,flightLists);
        JSONObject jsonResObj = formatReservation(reservation);
        if(reservation==null){
            ImportantException e = new ImportantException();
            e.setCode(404);
            e.setMessage("The requested reservarion request for passenger with id "+passengerID+" could not be performed. Possible Reason : Either passenger or flight not present or conflict detected.");
            throw e;
        }
        return new ResponseEntity(XML.toString(jsonResObj), HttpStatus.OK);
    }

    @GetMapping
    public List<PlaneReservation> searchReservation(@RequestParam(value = "passengerId", required = false) Integer passengerID,
                                               @RequestParam(value="from",required = false) String from,
                                               @RequestParam(value="to", required = false) String to,
                                               @RequestParam(value="flightNumber",required = false) String flightNumber) throws ImportantException {

        List<PlaneReservation> resevList= reservationService.searchReservation(passengerID,from,to,flightNumber);
        if(resevList==null){
            ImportantException e = new ImportantException();
            e.setCode(404);
            e.setMessage("No results found for your search, Seacrh with different keyword");
            throw e;
        }
        return resevList;
    }

    @DeleteMapping(value = "/{id}")
    public boolean deleteReservation(@PathVariable Integer id) throws ImportantException {
    	//reservationService.deleteReservation(id);
        if(!reservationService.deleteReservation(id)){
            ImportantException e = new ImportantException();
            e.setCode(404);
            e.setMessage("Reservation ID "+id+" was not found in database ");
            throw e;
        }

        return true;
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity updateReservation(@PathVariable Integer id,
                                         @RequestParam(value = "flightsAdded", required = false) List<String> flightsAdded,
                                         @RequestParam(value = "flightsRemoved", required = false) List<String> flightsRemoved) throws ImportantException {
        if(flightsAdded!=null && flightsAdded.size()==0) {
            ImportantException e = new ImportantException();
            e.setCode(404);
            e.setMessage("Reservation could not be updated. Add flights in the flightsAdded parameter.");
            throw e;

        }
        if(flightsRemoved!=null && flightsRemoved.size()==0){
            ImportantException e = new ImportantException();
            e.setCode(404);
            e.setMessage("Reservation could not be updated. Add flights in the flightsRemoved parameter.");
            throw e;
        }

        if (flightsAdded != null) {
            if (!reservationService.addFlights(id, flightsAdded)){
                ImportantException e = new ImportantException();
                e.setCode(404);
                e.setMessage("Reservation could not be updated. Please check for conflicts");
                throw e;
            }
        }
        if (flightsRemoved != null) {
            if (!reservationService.removeFlights(id, flightsRemoved)){
                ImportantException e = new ImportantException();
                e.setCode(404);
                e.setMessage("Flight cannot be removed from the reservation");
                throw e;
            }
        }
        JSONObject res_Object = formatReservation(reservationService.getReservation(id));
        return new ResponseEntity(res_Object.toString(), HttpStatus.OK);
    }
    
    public JSONObject formatReservation(PlaneReservation reservation){
    	JSONObject resut_json = new JSONObject();
    	try{
    		JSONObject jsonObject = new JSONObject();
	    	Field map = jsonObject.getClass().getDeclaredField("map");
			map.setAccessible(true);//because the field is private final...
			map.set(jsonObject, new LinkedHashMap<>());
			map.setAccessible(false);//return flag
			jsonObject.put("orderNumber", Integer.toString(reservation.getOrderNumber()));
			jsonObject.put("price", Integer.toString(reservation.getPrice()));
			Passenger passenger = reservation.getPassenger();
			JSONObject passengerJSON = new JSONObject();
			Field passMap = passengerJSON.getClass().getDeclaredField("map");
			passMap.setAccessible(true);//because the field is private final...
			passMap.set(passengerJSON, new LinkedHashMap<>());
			passMap.setAccessible(false);//return flag
			passengerJSON.put("id", Integer.toString(passenger.getPassengerId()));
			passengerJSON.put("firstname", passenger.getFirstname());
			passengerJSON.put("lastname", passenger.getLastname());
			passengerJSON.put("age", Integer.toString(passenger.getAge()));
			passengerJSON.put("gender", passenger.getGender());
			passengerJSON.put("phone", passenger.getPhone());
			jsonObject.put("passenger", passengerJSON);
			List<JSONObject> flightList = new ArrayList<JSONObject>();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
			Set<Flight> listF = reservation.getFlights();
			if(listF != null){
				for(Flight flight: listF){
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
					pMap.setAccessible(true);//because the field is private final...
					pMap.set(plane, new LinkedHashMap<>());
					pMap.setAccessible(false);//return flag
					plane.put("capacity", flight.getPlane().getCapacity());
					plane.put("model", flight.getPlane().getModel());
					plane.put("manufacturer", flight.getPlane().getManufacturer());
					plane.put("yearOfManufacture", flight.getPlane().getYearOfManufacture());
					flightObj.put("plane", plane);
					JSONObject f =new JSONObject();
					f.put("flight", flightObj);
					flightList.add(f);
				}
			}
			JSONArray flJsArray = new JSONArray(flightList);
			jsonObject.put("flights", flJsArray);
			resut_json.put("reservation", jsonObject);
		}catch(Exception e){
			System.out.println(e);
		}
    	return resut_json;
    }


}
