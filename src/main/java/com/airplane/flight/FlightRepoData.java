package com.airplane.flight;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface FlightRepoData extends JpaRepository<Flight, String>{

}
