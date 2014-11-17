package de.mpg.imeji.rest.to.predefinedMetadataTO;

import de.mpg.imeji.rest.to.MetadataTO;

public class PublicationTO extends MetadataTO{
	
	private String publication;
	
	private String format;

	public String getPublication() {
		return publication;
	}

	public void setPublication(String publication) {
		this.publication = publication;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	
	

}
