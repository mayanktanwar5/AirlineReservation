package com.airplane.passenger;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;


@Transactional
public interface PassengerRepoData extends JpaRepository<Passenger, Integer>{

		
}
