package de.mpg.imeji.rest.to.predefinedMetadataTO;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.mpg.imeji.rest.to.MetadataTO;
import de.mpg.j2j.annotations.j2jDataType;

@XmlRootElement
@j2jDataType("http://imeji.org/terms/metadata#number")
@XmlType (propOrder = {
		"number"
		})
public class NumberTO extends MetadataTO{
	private static final long serialVersionUID = -6070724739245057290L;
	private double number;

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}
	 

}
