/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://purl.org/escidoc/metadata/profiles/0.1/")
@RdfType("person")
@Embedded
public class Person implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 44487075151251416L;


	private String familyName;
	
	
	private String givenName;
	
	private String completeName;
	
	private String alternativeName;

	
	private String identifier;
	
	
	private URI role;
	
	private int pos = 0;
	
	protected Collection<Organization> organizations = new LinkedList<Organization>();

	
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
	public Collection<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Collection<Organization> organizations) {
		this.organizations = organizations;
	}
	
	@RdfProperty("http://purl.org/escidoc/metadata/terms/0.1/complete-name")
	public String getCompleteName() {
		return completeName;
	}

	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
	

}
