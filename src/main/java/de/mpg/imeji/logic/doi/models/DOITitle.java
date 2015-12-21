package de.mpg.imeji.logic.doi.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DOITitle {

  private String title;
  
  public DOITitle(){
  };
  
  public DOITitle(String title){
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
