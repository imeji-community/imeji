package test.bean;

import thewebsemantic.Namespace;


@Namespace("http://test#")
public class Profile {
	String firstName;
	String lastName;
	String id;
	
	public Profile() {
	}
	
	public Profile(String id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	

	
}
