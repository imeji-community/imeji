package de.mpg.imeji.rest.to.predefinedMetadataTO;

import de.mpg.imeji.rest.to.MetadataTO;

public class LicenseTO extends MetadataTO{
	
	private String license;
	
	private String url;



	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	

}
