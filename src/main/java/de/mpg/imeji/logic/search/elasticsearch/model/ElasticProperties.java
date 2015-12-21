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
  private String id;
  private String creator;
  private String lastEditor;
  private Date created;
  private Date modified;
  private String status;

  /**
   * Constructor for {@link Properties}
   * 
   * @param p
   */
  public ElasticProperties(Properties p) {
    this.id = p.getId().toString();
    this.setCreated(p.getCreated().getTime());
    this.creator = p.getCreatedBy().toString();
    this.setModified(p.getModified().getTime());
    this.status = p.getStatus().name();
  }


  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the createdBy
   */
  public String getCreator() {
    return creator;
  }

  /**
   * @param createdBy the createdBy to set
   */
  public void setCreator(String createdBy) {
    this.creator = createdBy;
  }

  /**
   * @return the modifiedBy
   */
  public String getLastEditor() {
    return lastEditor;
  }

  /**
   * @param modifiedBy the modifiedBy to set
   */
  public void setLastEditor(String modifiedBy) {
    this.lastEditor = modifiedBy;
  }

  /**
   * @return the created
   */
  public Date getCreated() {
    return created;
  }

  /**
   * @param created the created to set
   */
  public void setCreated(Date created) {
    this.created = created;
  }


  /**
   * @return the modified
   */
  public Date getModified() {
    return modified;
  }


  /**
   * @param modified the modified to set
   */
  public void setModified(Date modified) {
    this.modified = modified;
  }


  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }


  /**
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

}
