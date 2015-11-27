package de.mpg.imeji.logic.doi.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DOICreator {
  
  private String creatorName;

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }
  
}
