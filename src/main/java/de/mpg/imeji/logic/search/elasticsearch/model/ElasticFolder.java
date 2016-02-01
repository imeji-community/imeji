package de.mpg.imeji.logic.search.elasticsearch.model;

import de.mpg.imeji.logic.vo.CollectionImeji;

/**
 * The elastic Version of a {@link CollectionImeji}
 * 
 * @author bastiens
 * 
 */
public class ElasticFolder extends ElasticContainerProperties {

  private String profile;

  public ElasticFolder(CollectionImeji c) {
    super(c);

    if (c.getProfile() != null ) {
      this.setProfile(c.getProfile().toString());
    }
    else
    {
      this.setProfile("");
    }

    this.setSpace(String.valueOf(c.getSpace()));
  }

  /**
   * @return the profile
   */
  public String getProfile() {
    return profile;
  }

  /**
   * @param profile the profile to set
   */
  public void setProfile(String profile) {
    this.profile = profile;
  }

}
