package de.mpg.imeji.rest.to.predefinedMetadataTO;

import de.mpg.imeji.rest.to.MetadataTO;

public class LinkTO extends MetadataTO{
	
	private String link;
	private String url;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	

}
