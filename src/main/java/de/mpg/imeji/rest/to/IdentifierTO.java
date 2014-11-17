package de.mpg.imeji.rest.to;

import java.io.Serializable;

public class IdentifierTO implements Serializable{


	private static final long serialVersionUID = 1633020264591234236L;
	
	private String type = "imeji";
	
	private String value;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
