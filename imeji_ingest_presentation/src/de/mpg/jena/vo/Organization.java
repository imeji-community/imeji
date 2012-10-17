/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo;

import java.io.Serializable;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://purl.org/escidoc/metadata/profiles/0.1/")
@RdfType("organizationalunit")
@Embedded
public class Organization implements Serializable, Comparable<Organization>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2930022237448415538L;


	private String name;
	
	
	private String description;

	
	private String identifier;
	
	
	private String city;
	
	
	private String country;
	
	private int pos = 0;
	
	
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

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int compareTo(Organization o) {
		if (o.getPos() > this.pos) return -1;
    	else if (o.getPos() == this.pos) return 0;
    	else return 1;
	}
	
	

}
