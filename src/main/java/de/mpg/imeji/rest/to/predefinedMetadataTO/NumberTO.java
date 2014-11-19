package de.mpg.imeji.rest.to.predefinedMetadataTO;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.mpg.imeji.rest.to.MetadataTO;

@XmlRootElement
@XmlType (propOrder = {	 
		"number"
		})
public class NumberTO extends MetadataTO{
	 private double number;

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}
	 

}
