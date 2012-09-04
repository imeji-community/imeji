package test.bean;

import java.net.URI;

import thewebsemantic.Id;

public class URITestBean {
	URI uri;
	URI secondaryURI;
	String name;
	String id;
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}
	public URI getSecondaryURI() {
		return secondaryURI;
	}
	public void setSecondaryURI(URI secondaryURI) {
		this.secondaryURI = secondaryURI;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
