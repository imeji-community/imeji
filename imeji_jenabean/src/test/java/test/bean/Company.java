package test.bean;

import thewebsemantic.binding.RdfBeanId;

public class Company extends RdfBeanId<Company> {
	
	private String name;
	private String address;
	private String telephone;
	private int employees;
	
	public Company(String id) {
		super(id);
	}
	
	public Company() {
		super();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public int getEmployees() {
		return employees;
	}
	public void setEmployees(int employees) {
		this.employees = employees;
	}

}
