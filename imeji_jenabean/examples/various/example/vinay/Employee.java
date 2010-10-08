package example.vinay;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;


@NamedNativeQueries(
	{@NamedNativeQuery(
		query="SELECT ?s WHERE {?s a <http://example.vinay/Employee>}",
		name="Employee.ALL",
		resultClass=Employee.class
	)
		

})

@Entity
public class Employee {
	
	@Id
	@GeneratedValue
	int id;
	String fistName;
	String lastName;
	Role role;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFistName() {
		return fistName;
	}
	public void setFistName(String fistName) {
		this.fistName = fistName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String toString() {
		return "my name is " + fistName + " " + lastName + " and I am a " + role;
	}

}
