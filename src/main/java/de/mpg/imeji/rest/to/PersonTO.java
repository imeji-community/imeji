package de.mpg.imeji.rest.to;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@XmlRootElement
@XmlType(propOrder = {	 
		"position",
		"id",
		"familyName", 
		"givenName",
		"completeName",
		"alternativeName",
		"role",
		"identifiers",
		"organizations"
		})
@JsonInclude(Include.NON_NULL)
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
	
	private List<OrganizationTO> organizations = new ArrayList<OrganizationTO>();



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

	public List<OrganizationTO> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<OrganizationTO> organizations) {
		this.organizations = organizations;
	}


	
	

}
