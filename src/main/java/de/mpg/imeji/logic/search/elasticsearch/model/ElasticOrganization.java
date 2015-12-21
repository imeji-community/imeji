package de.mpg.imeji.logic.search.elasticsearch.model;

import de.mpg.imeji.logic.vo.Organization;

/**
 * The Elastic representation for an {@link Organization}
 * 
 * @author bastiens
 * 
 */
public class ElasticOrganization {
  private String name;
  private String city;
  private String country;
  private String description;

  /**
   * Constructor for a {@link Organization}
   * 
   * @param org
   */
  public ElasticOrganization(Organization org) {
    this.name = org.getName();
    this.description = org.getDescription();
    this.city = org.getCity();
    this.country = org.getCountry();
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
   * @return the city
   */
  public String getCity() {
    return city;
  }

  /**
   * @param city the city to set
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * @return the country
   */
  public String getCountry() {
    return country;
  }

  /**
   * @param country the country to set
   */
  public void setCountry(String country) {
    this.country = country;
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
