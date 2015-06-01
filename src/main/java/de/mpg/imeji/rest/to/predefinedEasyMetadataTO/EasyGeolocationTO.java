package de.mpg.imeji.rest.to.predefinedEasyMetadataTO;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class EasyGeolocationTO implements Serializable{

	private static final long serialVersionUID = -3900598743915553519L;

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
	
//    @JsonCreator
//    public EasyGeolocationTO(Map<String,Object> props)
//    {
//      name = (String) props.get("name");
//      longitude = (double) props.get("longitude");
//      latitude = (double) props.get("latitude");
//    }
//    
    
    
    
    
    

}
