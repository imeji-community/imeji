package de.mpg.imeji.rest.to.predefinedEasyMetadataTO;

import java.io.Serializable;

public class EasyConePersonTO implements Serializable{
	private static final long serialVersionUID = 1645461293418318845L;

	private String familyName;
	
	private String givenName;

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
	
	
	

}
