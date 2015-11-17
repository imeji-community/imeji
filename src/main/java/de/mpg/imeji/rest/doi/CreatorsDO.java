package de.mpg.imeji.rest.doi;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreatorsDO {
  
  private CreatorDO creator = new CreatorDO();
  
  public CreatorsDO(){
   
  }
  
  public CreatorsDO(String creatorName){
    creator.setCreatorName(creatorName);
  }  

  public CreatorDO getCreator(){
    return creator;
  }
  
  public void setCreator(CreatorDO creator){
    this.creator = creator;
  }

  
  

}
