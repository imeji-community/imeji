package test.fieldaccess;

import java.util.Calendar;
import java.util.Date;

import thewebsemantic.Id;
import thewebsemantic.RdfProperty;
import thewebsemantic.binding.RdfBean;

public class AllTypes extends RdfBean<AllTypes> {
	
	@RdfProperty("http://xmlns.com/foaf/0.1/name")
	public String name;
	public int age;
	public long ssn;
	public float salary;
	public double debt;
	public short version;
	public Date birthday;
	public Calendar appointment;
	public String[] nicknames;
	public transient int nothing;
	
	@Id
	private transient int id;
	
	
	public AllTypes(String s) {
		id = Integer.parseInt(s);
	}
	

	public int id() {
		return id;
	}


	
}
