package de.mpg.imeji.rest.to.predefinedMetadataTO;

import de.mpg.imeji.rest.to.MetadataTO;
import de.mpg.imeji.rest.to.PersonTO;

public class ConePersonTO extends MetadataTO{
	private PersonTO person;

	public PersonTO getPerson() {
		return person;
	}

	public void setPerson(PersonTO person) {
		this.person = person;
	}


}
