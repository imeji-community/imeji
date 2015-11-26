package de.mpg.imeji.logic.doi.models;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DOICreators {
  
  private DOICreator creator = new DOICreator();
  
  public DOICreators(){
   
  }
  
  public DOICreators(String creatorName){
    creator.setCreatorName(creatorName);
  }  

  public DOICreator getCreator(){
    return creator;
  }
  
  public void setCreator(DOICreator creator){
    this.creator = creator;
  }

  
  

}
