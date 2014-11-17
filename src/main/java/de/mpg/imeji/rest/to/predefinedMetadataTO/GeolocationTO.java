package de.mpg.imeji.rest.to.predefinedMetadataTO;

import de.mpg.imeji.rest.to.MetadataTO;

public class GeolocationTO extends MetadataTO{
	private String name;
	private double longitude;
	private double latitude;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	
	
	

}
