package test.bean;

import java.util.Date;

import thewebsemantic.Id;
import thewebsemantic.Namespace;


@Namespace("http://meanbean#")
public class MeanBean {
	private int age;
	private float salary;
	private String name;
	private Date created;
	
	
	@Id
	public String id() {
		return hashCode() + "";
	}
	public float getSalary() {
		return salary;
	}
	public void setSalary(float salary) {
		this.salary = salary;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
}
