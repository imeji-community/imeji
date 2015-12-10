package de.mpg.imeji.logic.search.elasticsearch.model;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Space;

/**
 * The object which is indexed in Elastic search <br/>
 * !!! IMPORTANT !!!<br/>
 * This File must be synchronized with resources/elasticsearch/ElasticItemsMapping.json
 * 
 * @author bastiens
 * 
 */
public class ElasticSpace extends ElasticProperties {

  private List<String> folder;
  private String slug;
  private String name;
  private String description;

  /**
   * Constructor with an {@link Item}
   * 
   * @param item
   */
  public ElasticSpace(Space space) {
    super(space);
    folder = new ArrayList<>(space.getSpaceCollections());
    this.slug = space.getSlug();
    this.name = space.getTitle();
    this.description = space.getDescription();
  }

  /**
   * @return the slug
   */
  public String getSlug() {
    return slug;
  }

  /**
   * @param slug the slug to set
   */
  public void setSlug(String slug) {
    this.slug = slug;
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

  public List<String> getFolder() {
    return folder;
  }

  public void setFolder(List<String> folder) {
    this.folder = folder;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
