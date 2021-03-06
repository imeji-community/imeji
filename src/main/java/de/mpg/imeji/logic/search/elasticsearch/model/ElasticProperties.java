package de.mpg.imeji.logic.search.elasticsearch.model;

import java.util.Date;

import de.mpg.imeji.logic.vo.Properties;

/**
 * Elastic Search Entity for {@link Properties}
 *
 * @author bastiens
 *
 */
public class ElasticProperties {
  private final String id;
  private final String creator;
  private final Date created;
  private final Date modified;
  private final String status;

  /**
   * Constructor for {@link Properties}
   *
   * @param p
   */
  public ElasticProperties(Properties p) {
    this.id = p.getId().toString();
    this.created = p.getCreated().getTime();
    this.creator = p.getCreatedBy().toString();
    this.modified = p.getModified().getTime();
    this.status = p.getStatus().name();
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the createdBy
   */
  public String getCreator() {
    return creator;
  }

  /**
   * @return the created
   */
  public Date getCreated() {
    return created;
  }

  /**
   * @return the modified
   */
  public Date getModified() {
    return modified;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

}
