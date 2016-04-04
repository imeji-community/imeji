/**
 * 
 */
package de.mpg.imeji.logic.ingest.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.mpg.imeji.logic.resource.vo.MetadataProfile;

/**
 * @author hnguyen
 */
@XmlRootElement(name = "metadataProfiles", namespace = "http://imeji.org/terms/")
public class MetadataProfiles {
  private List<MetadataProfile> metadataProfile;

  public MetadataProfiles() {}

  /**
   * @return the meta data profiles
   */
  @XmlElement(name = "mdprofile", namespace = "http://imeji.org/terms/")
  public List<MetadataProfile> getMetadataProfile() {
    return metadataProfile;
  }

  /**
   * @param items the items to set
   */
  public void setMetadataProfile(List<MetadataProfile> metadataProfile) {
    this.metadataProfile = metadataProfile;
  }
}
