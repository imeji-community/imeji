package de.mpg.imeji.logic.search.elasticsearch.model;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Person;

/**
 * Common Properties to {@link CollectionImeji} and {@link Album}
 * 
 * @author bastiens
 * 
 */
public class ElasticContainerProperties extends ElasticProperties {

  private String name;
  private String description;
  private String space;
  private List<String> pid;
  private List<ElasticPerson> author = new ArrayList<>();

  /**
   * Default Constructor
   * 
   * @param c
   */
  public ElasticContainerProperties(Container c) {
    super(c);
    this.setName(c.getMetadata().getTitle());
    this.setDescription(c.getMetadata().getDescription());
    if (c.getDoi() != null) {
      this.pid = new ArrayList<>();
      this.pid.add(c.getDoi());
    }
    for (Person p : c.getMetadata().getPersons()) {
      author.add(new ElasticPerson(p));
    }
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public List<ElasticPerson> getAuthor() {
    return author;
  }

  public void setAuthor(List<ElasticPerson> author) {
    this.author = author;
  }

  /**
   * @return the space
   */
  public String getSpace() {
    return space;
  }

  /**
   * @param space the space to set
   */
  public void setSpace(String space) {
    this.space = space;
  }

  public List<String> getPid() {
    return pid;
  }

  public void setPid(List<String> pid) {
    this.pid = pid;
  }
}
