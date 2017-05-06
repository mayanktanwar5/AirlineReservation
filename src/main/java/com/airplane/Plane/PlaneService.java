package com.airplane.Plane;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class PlaneService {

	@Autowired
	private PlaneRepository planeRepository;

	public Plane getFlight(String number) {
		return planeRepository.getFlight(number);
	}

	public Plane createFlight(Plane flight) {
		return planeRepository.createFlight(flight);
	}

	/*
	 * Returns the list of flights from list of flight numbers
	 */
	public List<Plane> getFlights(String[] flightList) {
		List<Plane> flights = new ArrayList<>();
		for (String flightNumber : flightList) {
			flights.add(planeRepository.getFlight(flightNumber));
		}
		return flights;
	}

	public Boolean checkOverlap(String[] flightLists) throws Exception {
		List<Plane> flights = getFlights(flightLists);

		Boolean case1 = null, case2 = null;
		try {

			for (int i = 0; i < flights.size(); i++) {
				Plane flight = flights.get(i);
				for (int j = 0; j < flights.size(); j++) {
					if (i != j) {
						Plane checkFlight = flights.get(j);
						case1 = (flight.getDepartureTime().after(checkFlight.getDepartureTime())
								|| flight.getDepartureTime().equals(checkFlight.getDepartureTime()))
								&& (flight.getDepartureTime().before(checkFlight.getArrivalTime())
										|| flight.getDepartureTime().equals(checkFlight.getArrivalTime()));
						case2 = (flight.getArrivalTime().after(checkFlight.getDepartureTime())
								|| flight.getArrivalTime().equals(checkFlight.getDepartureTime()))
								&& (flight.getArrivalTime().before(checkFlight.getArrivalTime())
										|| flight.getArrivalTime().equals(checkFlight.getArrivalTime()));
						if (case1 || case2) {
							return true;
						}

					}
				}
			}
		} catch (NullPointerException e) {
			throw new Exception("Flight does not exists. Please check again");
		}
		return false;
	}

	public Boolean checkFlightAvailability(String flightNumber) {
		return planeRepository.getFlight(flightNumber).getSeatsLeft() > 0;
	}

	public Boolean checkFlightsAvailability(String[] flights) {

		Boolean result = true;
		for (String flightNumber : flights) {
			result = result && checkFlightAvailability(flightNumber);
		}
		return result;
	}

	public void deleteFlight(Plane flight) {
		planeRepository.deleteFlight(flight);
	}

	
}
