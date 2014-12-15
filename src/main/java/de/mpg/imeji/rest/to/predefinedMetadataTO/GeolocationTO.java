package de.mpg.imeji.rest.to.predefinedMetadataTO;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.mpg.imeji.rest.to.MetadataTO;
import de.mpg.j2j.annotations.j2jDataType;

@XmlRootElement
@j2jDataType("http://imeji.org/terms/metadata#geolocation")
@XmlType (propOrder = {	 
		"name",
		"longitude",
		"latitude"
		})
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
