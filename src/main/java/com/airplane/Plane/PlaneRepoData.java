package com.airplane.Plane;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.airplane.Plane.*;

@Transactional
public interface PlaneRepoData extends JpaRepository<Plane, Integer>{

}
