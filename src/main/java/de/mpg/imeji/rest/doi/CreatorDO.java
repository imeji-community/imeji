package de.mpg.imeji.rest.doi;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreatorDO {
  
  private String creatorName;

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }
  
}
