package de.mpg.escidoc.faces.metastore.vo;

import java.net.URI;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://purl.org/escidoc/metadata/profiles/0.1/")
@RdfType("organizationalunit")
@Embedded
public class Organization {


	
	private String name;
	
	
	private String description;

	
	private String identifier;
	
	
	private String city;
	
	
	private String country;
	
	
	
	@RdfProperty("http://purl.org/dc/elements/1.1/title")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@RdfProperty("http://purl.org/dc/elements/1.1/description")
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;

	}

	@RdfProperty("http://purl.org/dc/elements/1.1/identifier")
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@RdfProperty("http://purl.org/escidoc/metadata/terms/0.1/city")
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@RdfProperty("http://purl.org/escidoc/metadata/terms/0.1/country")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
