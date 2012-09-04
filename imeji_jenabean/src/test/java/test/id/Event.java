package test.id;

import java.util.Date;

import thewebsemantic.Id;

public class Event {

	@Id private Date eventid;
	private String name;
	private String location;
	
	public Event() {}
	
	public Event(Date d) {
		eventid = d;
	}

	public Date id() {
		return eventid;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
