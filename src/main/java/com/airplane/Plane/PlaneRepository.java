package com.airplane.Plane;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;



public class PlaneRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	  
	/*
	 * Get a Flight 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Plane getFlight(String number) {
		return entityManager.find(Plane.class, number);
	}
	
	public Plane createFlight(Plane flight) {
		return entityManager.merge(flight);
	}
	
	public void deleteFlight(Plane flight) {
		entityManager.remove(flight);
	}

	
}
