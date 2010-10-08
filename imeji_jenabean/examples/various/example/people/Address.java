package example.people;

import java.net.URLEncoder;
import java.security.MessageDigest;

import sun.misc.BASE64Encoder;
import thewebsemantic.Id;

public class Address {
	private String city;
	private String street;
	private String id;
	
	public Address() {
		id = sha();
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getStreet() {
		return street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
	
	@Id
	public String id() {return id;}
	
	private String sha() {
		byte[] bytes = (city + street).getBytes();
		try {
			byte[] b = MessageDigest.getInstance("SHA").digest(bytes);
			String s = new BASE64Encoder().encode(b);
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {} 
		return null;
	}
}
