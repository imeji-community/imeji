package test.bean;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://example.org#")
public class AnnotationTester {
	public String email;
	public String fullName;

	@RdfProperty("http://xmlns.com/foaf/0.1/mbox")
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@RdfProperty("http://xmlns.com/foaf/0.1/name")
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
