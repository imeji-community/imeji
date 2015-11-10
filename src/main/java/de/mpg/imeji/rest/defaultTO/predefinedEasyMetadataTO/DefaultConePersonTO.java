package de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.rest.defaultTO.DefaultOrganizationTO;

/**
 * TO for {@link ConePerson}
 * 
 * @author bastiens
 * 
 */
public class DefaultConePersonTO implements Serializable {
  private static final long serialVersionUID = 1645461293418318845L;

  private String familyName;
  private String givenName;
  private List<DefaultOrganizationTO> organizations = new ArrayList<DefaultOrganizationTO>();

  /**
   * @return the familyName
   */
  public String getFamilyName() {
    return familyName;
  }

  /**
   * @param familyName the familyName to set
   */
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  /**
   * @return the givenName
   */
  public String getGivenName() {
    return givenName;
  }

  /**
   * @param givenName the givenName to set
   */
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  /**
   * @return the organizations
   */
  public List<DefaultOrganizationTO> getOrganizations() {
    return organizations;
  }

  /**
   * @param organizations the organizations to set
   */
  public void setOrganizations(List<DefaultOrganizationTO> organizations) {
    this.organizations = organizations;
  }



}
