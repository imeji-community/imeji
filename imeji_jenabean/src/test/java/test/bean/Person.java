package test.bean;

import java.rmi.server.UID;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import thewebsemantic.RdfProperty;
import thewebsemantic.Uri;
import thewebsemantic.binding.RdfBean;

public class Person extends RdfBean<Person> {
	private String firstName;
	private String lastName;
	private int age;
	private Date birthday;
	
	@RdfProperty(transitive=true)
	private Collection<Person> ancestors;
	private Collection<Person> friends;
	private Collection<Person> colleagues;
	
	private String uri;
	
	public Person() {
		uri = "http://example.org/" + new UID().toString();
		ancestors = new LinkedList<Person>();
	}
	
	public Person(String uri) {
		this.uri = uri;
	}

	@Uri
	public String uri() {
		return uri;
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
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public Date getBirthday() {
		return birthday;
	}
	
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	@RdfProperty(transitive=true)
	public Collection<Person> getAncestors() {
		return ancestors;
	}
	
	
	public void setAncestors(Collection<Person> ancestors) {
		this.ancestors = ancestors;
	}
	
	public void addAncestor(Person p) {
		ancestors.add(p);
	}

	@RdfProperty(symmetric=true)
	public Collection<Person> getFriends() {
		return friends;
	}

	public void setFriends(Collection<Person> friends) {
		this.friends = friends;
	}

	public Collection<Person> getColleagues() {
		return colleagues;
	}

	public void setColleagues(Collection<Person> colleagues) {
		this.colleagues = colleagues;
	}
}
