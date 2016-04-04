/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.resource.vo.CollectionImeji;
import de.mpg.imeji.logic.resource.vo.Metadata;
import de.mpg.imeji.logic.resource.vo.MetadataProfile;
import de.mpg.imeji.logic.util.MetadataFactory;

/**
 * Session with objects related to {@link CollectionImeji}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CollectionSessionBean {
  private CollectionImeji active = null;
  private MetadataProfile profile = null;
  private String selectedMenu = "SORTING";
  private String filter = "all";
  private List<Metadata> metadataTypes = null;

  /**
   * Constructor
   */
  public CollectionSessionBean() {
    try {
      init();
    } catch (Exception e) {
      throw new RuntimeException("Error initializing collection session:", e);
    }
  }

  /**
   * Initialize the session objects
   * 
   * @throws Exception
   */
  public void init() throws Exception {
    active = new CollectionImeji();
    profile = new MetadataProfile();
    metadataTypes = new ArrayList<Metadata>();
    for (Metadata.Types t : Metadata.Types.values()) {
      metadataTypes.add(MetadataFactory.createMetadata(t));
    }
  }

  /**
   * @return the active
   */
  public CollectionImeji getActive() {
    return active;
  }

  /**
   * @param active the active to set
   */
  public void setActive(CollectionImeji active) {
    this.active = active;
  }

  /**
   * @return the selectedMenu
   */
  public String getSelectedMenu() {
    return selectedMenu;
  }

  /**
   * @param selectedMenu the selectedMenu to set
   */
  public void setSelectedMenu(String selectedMenu) {
    this.selectedMenu = selectedMenu;
  }

  /**
   * getter
   * 
   * @return
   */
  public String getFilter() {
    return filter;
  }

  /**
   * setter
   * 
   * @param filter
   */
  public void setFilter(String filter) {
    this.filter = filter;
  }

  /**
   * setter
   * 
   * @param metadataTypes
   */
  public void setMetadataTypes(List<Metadata> metadataTypes) {
    this.metadataTypes = metadataTypes;
  }

  /**
   * getter
   * 
   * @return
   */
  public List<Metadata> getMetadataTypes() {
    return metadataTypes;
  }

  /**
   * getter
   * 
   * @return
   */
  public MetadataProfile getProfile() {
    return profile;
  }

  /**
   * setter
   * 
   * @param profile
   */
  public void setProfile(MetadataProfile profile) {
    this.profile = profile;
  }
}
