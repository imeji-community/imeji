package de.mpg.imeji.rest.to.predefinedMetadataTO;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.mpg.imeji.rest.to.MetadataTO;
import de.mpg.j2j.annotations.j2jDataType;

@XmlRootElement
@j2jDataType("http://imeji.org/terms/metadata#date")
@XmlType (propOrder = {
		"date"
		})
public class DateTO extends MetadataTO{
	
    private String date;
 
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}


    
	
    

}
