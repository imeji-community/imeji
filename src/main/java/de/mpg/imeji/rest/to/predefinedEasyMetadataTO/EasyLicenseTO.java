package de.mpg.imeji.rest.to.predefinedEasyMetadataTO;

import java.io.Serializable;

public class EasyLicenseTO implements Serializable{
	
	private static final long serialVersionUID = 4218264604177901345L;

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
