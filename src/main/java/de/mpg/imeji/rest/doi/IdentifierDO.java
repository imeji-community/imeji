package de.mpg.imeji.rest.doi;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType
public class IdentifierDO {

  
  private String identifierType ="DOI";
  
  @XmlValue
  private String identifier = "1";
  
  
  public IdentifierDO(){
  }
  
  
  @XmlAttribute
  public String getIdentifierType() {
    return identifierType;
  }

  public void setIdentifierType(String identifierType) {
    this.identifierType = identifierType;
  }


  
  
}
