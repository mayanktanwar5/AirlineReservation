package com.airplane.excep;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyExceptionHandler {


	@ExceptionHandler(ImportantException.class)
	public ResponseEntity customException(ImportantException e)throws Exception{
		AirlineResponse resp = new AirlineResponse();
		resp.setCode(Integer.toString(e.getCode()));
		resp.setMsg(e.getMessage());
		if(e.isXml()){
			
			return new ResponseEntity(resp , HttpStatus.BAD_REQUEST);
		}
		if(!e.isXml()){
			
			ModelMap modelMap =new ModelMap();
			modelMap.addAttribute("Bad Request", resp);
			return new ResponseEntity(modelMap , HttpStatus.BAD_REQUEST);
		}
		return null;
	}
	
}
