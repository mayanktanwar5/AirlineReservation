package com.airplane.excep;

import org.springframework.stereotype.Component;

@Component
public class ImportantException extends Exception{

	public int code;
	public String message;
	public boolean xml;
	
	
	public boolean isXml() {
		return xml;
	}
	public void setXml(boolean xml) {
		this.xml = xml;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	
	
	public int getCode() {
		return code;
	}
}