package de.mpg.escidoc.faces.metastore2.vo;

import java.net.URI;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://purl.org/escidoc/metadata/profiles/0.1/")
@RdfType("person")
@Embedded
public class Person {
	
	
	private String familyName;
	
	
	private String givenName;
	
	
	private String alternativeName;

	
	private String identifier;
	
	
	private URI role;
	

	private Organization organization;

	
	@RdfProperty("http://purl.org/escidoc/metadata/terms/0.1/family-name")
	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	@RdfProperty("http://purl.org/escidoc/metadata/terms/0.1/given-name")
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	@RdfProperty("http://purl.org/escidoc/metadata/terms/0.1/alternative-name")
	public String getAlternativeName() {
		return alternativeName;
	}

	public void setAlternativeName(String alternativeName) {
		this.alternativeName = alternativeName;
	}

	@RdfProperty("http://purl.org/dc/elements/1.1/identifier")
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@RdfProperty("http://purl.org/escidoc/metadata/terms/0.1/role")
	public URI getRole() {
		return role;
	}

	public void setRole(URI role) {
		this.role = role;
	}

	@RdfProperty("http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	

}
