package test.bean;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.Symmetric;

@Namespace("http://example.org/")
public class A {

	@Symmetric
	private Collection<A> friends = new LinkedList<A>();
	private String name;
	private float salary;
	private String id;
	
	public A() {}
	
	public A(String id) {
		this.id = id;
	}
	
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Symmetric
	public Collection<A> getFriends() {
		return friends;
	}
	public void setFriends(Collection<A> friends) {
		this.friends = friends;
	}
	public void addFriend(A a) {
		friends.add(a);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getSalary() {
		return salary;
	}
	public void setSalary(float salary) {
		this.salary = salary;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if ((o == null) || (o.getClass() != this.getClass()))
			return false;
		return equals((A)o);
	}
	
	private boolean equals(A o) {
		return (o.id != null) ? o.id.equals(id) : o.id == id;
	}
	

	

}
