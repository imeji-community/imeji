package de.mpg.imeji.logic.search.elasticsearch.model;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;

/**
 * The Elastic representation of a {@link Person}
 * 
 * @author bastiens
 * 
 */
public class ElasticPerson {
  private String familyname;
  private String givenname;
  private String completename;
  private String identifier;
  private List<ElasticOrganization> organization = new ArrayList<>();

  /**
   * Default Constructor;
   */
  public ElasticPerson() {}

  /**
   * Constructor for a {@link Person}
   * 
   * @param p
   */
  public ElasticPerson(Person p) {
    setPerson(p);
  }

  /**
   * Set the {@link Person} to this {@link ElasticPerson}
   * 
   * @param p
   */
  public void setPerson(Person p) {
    this.familyname = p.getFamilyName();
    this.givenname = p.getGivenName();
    this.identifier = p.getIdentifier();
    this.completename = p.getCompleteName();
    for (Organization org : p.getOrganizations()) {
      organization.add(new ElasticOrganization(org));
    }
  }


  /**
   * @return the familyname
   */
  public String getFamilyname() {
    return familyname;
  }


  /**
   * @param familyname the familyname to set
   */
  public void setFamilyname(String familyname) {
    this.familyname = familyname;
  }


  /**
   * @return the givenname
   */
  public String getGivenname() {
    return givenname;
  }


  /**
   * @param givenname the givenname to set
   */
  public void setGivenname(String givenname) {
    this.givenname = givenname;
  }


  /**
   * @return the completename
   */
  public String getCompletename() {
    return completename;
  }


  /**
   * @param completename the completename to set
   */
  public void setCompletename(String completename) {
    this.completename = completename;
  }


  /**
   * @return the identifier
   */
  public String getIdentifier() {
    return identifier;
  }


  /**
   * @param identifier the identifier to set
   */
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }


  /**
   * @return the organisation
   */
  public List<ElasticOrganization> getOrganization() {
    return organization;
  }


  /**
   * @param organisation the organisation to set
   */
  public void setOrganization(List<ElasticOrganization> organization) {
    this.organization = organization;
  }


}
