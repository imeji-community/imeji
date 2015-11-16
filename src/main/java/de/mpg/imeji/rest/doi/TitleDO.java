package de.mpg.imeji.rest.doi;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TitleDO {

  private String title;
  
  public TitleDO(){
  };
  
  public TitleDO(String title){
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
