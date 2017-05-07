package com.airplane.passenger;

import java.util.List;
import java.util.UUID;

import com.airplane.flight.Flight;
import com.airplane.reservation.PlaneReservation;
import com.airplane.reservation.PlaneReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {

	@Autowired
	private PassengerRepoData passengerRepoData;
	@Autowired
	private PlaneReservationService reservationService;

	public Passenger getPassenger(int id){
		return passengerRepoData.findOne(id);
	}
	
	public List<Passenger> getAllPassengers(){
		return passengerRepoData.findAll();
	}
	
	public Passenger createPassenger(Passenger pass){
		try{
			passengerRepoData.save(pass);
		}catch(Exception e){
			pass = null;
		}
		return pass;
	}
	
	public boolean deletePassenger(int id){
		Passenger passenger=passengerRepoData.findOne(id);
		if(passenger==null)
			return false;

		List<PlaneReservation> reservations=passenger.getReservation();

		PlaneReservation allReservation[]=new PlaneReservation[reservations.size()];
		int i=0;

		for(PlaneReservation res:reservations)
			allReservation[i++]=res;

		try {
			for (int j=0;j<allReservation.length;j++) {
				reservationService.deleteReservation(allReservation[j].getOrderNumber());
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		try {
			passengerRepoData.delete(id);
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Passenger updatePassenger(int id, String firstName, String lastName, int age, String gender, String phone){
		System.out.println("before update");
		Passenger pas=passengerRepoData.findOne(id);
		System.out.println("Processed update");
		if(pas !=null){
			pas.setFirstname(firstName);
			pas.setLastname(lastName);
			pas.setGender(gender);
			pas.setAge(age);
			pas.setPhone(phone);
			passengerRepoData.save(pas);
		}
		return passengerRepoData.findOne(id);
	}

	public void addReservation(Passenger passenger,PlaneReservation reservation) {
		passenger.addReservation(reservation);
	}
}
