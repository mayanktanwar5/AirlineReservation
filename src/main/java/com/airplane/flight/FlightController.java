package com.airplane.flight;
import com.airplane.excep.ImportantException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private FlightService serviceFlight;

    @GetMapping(value = "/allFlights")
    public List<Flight> getAllFlights(@RequestParam(value="xml") String xml) throws ImportantException {

        List<Flight> results= serviceFlight.getAllFlights();
        if(results==null){
            ImportantException e = new ImportantException();
            e.setCode(404);
            e.setMessage("Sorry we Could not find flights you are looking for come back later.");
            throw e;
        }
        return results;
    }

    @GetMapping(value = "/{id}")
    public Flight getFlight(@PathVariable String id) throws ImportantException {

        Flight resultFlight= serviceFlight.getFlight(id);
        if(resultFlight==null){
            ImportantException e = new ImportantException();
            e.setCode(404);
            e.setMessage("No flight with "+id+ "was found in db");
            throw e;
        }
        return resultFlight;
    }

    @PostMapping(value="/{id}")
    public Flight saveOrUpdateFlight(@PathVariable String id,
                                      @RequestParam(value="price") int price,
                                      @RequestParam(value="from") String from,
                                      @RequestParam(value="to") String to,
                                      @RequestParam(value="departureTime") String departureTime,
                                      @RequestParam(value="arrivalTime") String arrivalTime,
                                      @RequestParam(value="description") String description,
                                      @RequestParam(value="capacity") int capacity,
                                      @RequestParam(value="model") String model,
                                      @RequestParam(value="manufacturer") String manufacturer,
                                      @RequestParam(value="yearOfManufacture") int yearOfManufacture
                                      ) throws ImportantException {

        Flight flight=new Flight();

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH");
        Date dep = null, arr = null;
        try{
            dep = dateFormatter.parse(departureTime);
            arr = dateFormatter.parse(arrivalTime);
        }
        catch (Exception e) {
//            System.out.println("Date Format error "+e);
        }

        flight.setNumber(id);
        flight.setPrice(price);
        flight.setFromSource(from);
        flight.setToDestination(to);
        flight.setDepartureTime(dep);
        flight.setArrivalTime(arr);
        flight.setDescription(description);
        flight.setCapacity(capacity);
        flight.setSeatsLeft(capacity);
        flight.setModel(model);
        flight.setManufacturer(manufacturer);
        flight.setYearOfManufacture(yearOfManufacture);

        

        Flight flightResult= serviceFlight.saveOrUpdateFlight(flight);

        if(flightResult==null){
            ImportantException e = new ImportantException();
            e.setCode(404);
            e.setMessage("No file was updated in the database");
            throw e;
        }
        return flightResult;
    }

    @DeleteMapping("/{id}")
    public boolean deleteFlight(@PathVariable String id) throws ImportantException {

        if(!serviceFlight.deleteFlight(id)){
            ImportantException e = new ImportantException();
            e.setCode(404);
            e.setMessage("Couldnot deleet the flight, enter the correct flight Id");
            throw e;
        }
        return true;
    }
}
