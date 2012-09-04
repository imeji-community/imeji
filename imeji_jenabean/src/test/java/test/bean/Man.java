package test.bean;

import thewebsemantic.RdfProperty;
import thewebsemantic.Uri;
import thewebsemantic.binding.RdfBean;

public class Man extends RdfBean<Man>{

	private String name;
	private String description;
	private String uri;
	
	public Man(String uri) {
		this.uri = uri;
	}
	
	@Uri
	public String uri() {
		return uri;
	}
	
	@RdfProperty("http://foreign.ontology#name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@RdfProperty("http://foreign.ontology#description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
