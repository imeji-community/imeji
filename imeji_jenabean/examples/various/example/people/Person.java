package example.people;

import thewebsemantic.Uri;

public class Person {
	private Address address;
	private String name;
	private String openid;
	
	public Person(String uri) {
		openid = uri;
	}
	
	@Uri
	public String getOpenid() {
		return openid;
	}

	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
