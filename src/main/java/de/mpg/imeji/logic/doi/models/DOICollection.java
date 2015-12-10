package de.mpg.imeji.logic.doi.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resource")
public class DOICollection {

    private DOIIdentifier identifier;
    private List<DOICreators> creators = new ArrayList<DOICreators>();    
    private List<DOITitle> titles = new ArrayList<DOITitle>();
    private String publisher;
    private String publicationYear;
 
    @XmlElement(name = "creators")
    public List<DOICreators> getCreators() {
      return creators;
    }
    public void setCreators(List<DOICreators> creators) {
      this.creators = creators;
    }
    
    @XmlElement(name = "titles")
    public List<DOITitle> getTitles() {
      return titles;
    }
    public void setTitles(List<DOITitle> titles) {
      this.titles = titles;
    }
    public String getPublisher() {
      return publisher;
    }
    public void setPublisher(String publisher) {
      this.publisher = publisher;
    }
    public String getPublicationYear() {
      return publicationYear;
    }
    public void setPublicationYear(String publicationYear) {
      this.publicationYear = publicationYear;
    }
    public DOIIdentifier getIdentifier() {
      return identifier;
    }
    public void setIdentifier(DOIIdentifier identifier) {
      this.identifier = identifier;
    }
    
    
}
