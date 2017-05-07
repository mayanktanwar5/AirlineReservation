package com.airplane.excep;

import org.springframework.stereotype.Component;

@Component
public class AirlineResponse {

	private String code;
	private String msg;
	
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	
	public String getMsg() {
		return msg;
	}
	
}
