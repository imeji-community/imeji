package de.mpg.imeji.logic.search.elasticsearch.model;

import org.apache.commons.lang.StringUtils;

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
  
  private String collectionsinspace;
  private String slug;
  private String title;
  private String description;
  /**
   * Constructor with an {@link Item}
   * 
   * @param item
   */
  public ElasticSpace(Space space) {
    super(space);
    this.collectionsinspace = StringUtils.join(space.getSpaceCollections().toArray(), ",");
    this.slug = space.getSlug();
    this.title = space.getTitle(); 
    this.description = space.getDescription();
  }
  /**
   * @return the collectionsinspace
   */
  public String getCollectionsinspace() {
    return collectionsinspace;
  }
  /**
   * @param collectionsinspace the collectionsinspace to set
   */
  public void setCollectionsinspace(String collectionsinspace) {
    this.collectionsinspace = collectionsinspace;
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
   * @return the title
   */
  public String getTitle() {
    return title;
  }
  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
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
  
  

}
