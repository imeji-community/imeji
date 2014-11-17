package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PersonTO implements Serializable{
	
	private static final long serialVersionUID = 2752588435466650389L;
	
	private int position;

	private String id;
	
	private String familyName;
	
	private String givenName;
	
	private String completeName;
	
	private String alternativeName;
	
	private String role = "author";
	
	private List<IdentifierTO> identifiers = new ArrayList<IdentifierTO>();
	
	private List<OrganizationTO> origanizations = new ArrayList<OrganizationTO>();



	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getCompleteName() {
		return completeName;
	}

	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}

	public String getAlternativeName() {
		return alternativeName;
	}

	public void setAlternativeName(String alternativeName) {
		this.alternativeName = alternativeName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<IdentifierTO> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(List<IdentifierTO> identifiers) {
		this.identifiers = identifiers;
	}

	public List<OrganizationTO> getOriganizations() {
		return origanizations;
	}

	public void setOriganizations(List<OrganizationTO> origanizations) {
		this.origanizations = origanizations;
	}
	
	
	

}
