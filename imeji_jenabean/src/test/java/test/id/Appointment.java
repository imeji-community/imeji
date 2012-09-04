package test.id;

import thewebsemantic.Id;

public class Appointment {
	
	@Id private DateId time;
	String desc;
	public DateId getTime() {
		return time;
	}

	public void setTime(DateId time) {
		this.time = time;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}