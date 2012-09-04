package test.bean;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://foo.com/")
public class AnIDBean {

	private String id;
	private int age;
	private String address;
	private String city;
	private String zip;
	private String state;
	private float salary;
	
	@Id
	public String getId() {
		return id;
	}
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public float getSalary() {
		return salary;
	}

	public void setSalary(float salary) {
		this.salary = salary;
	}

	public AnIDBean(String s) {
		id = s;
	}
	
	
}
