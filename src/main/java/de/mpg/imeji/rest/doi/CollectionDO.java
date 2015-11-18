package de.mpg.imeji.rest.doi;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resource")
public class CollectionDO {

    private IdentifierDO identifier;
    private List<CreatorsDO> creators = new ArrayList<CreatorsDO>();    
    private List<TitleDO> titles = new ArrayList<TitleDO>();
    private String publisher;
    private String publicationYear;
 
    @XmlElement(name = "creators")
    public List<CreatorsDO> getCreators() {
      return creators;
    }
    public void setCreators(List<CreatorsDO> creators) {
      this.creators = creators;
    }
    
    @XmlElement(name = "titles")
    public List<TitleDO> getTitles() {
      return titles;
    }
    public void setTitles(List<TitleDO> titles) {
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
    public IdentifierDO getIdentifier() {
      return identifier;
    }
    public void setIdentifier(IdentifierDO identifier) {
      this.identifier = identifier;
    }
    
    
}
