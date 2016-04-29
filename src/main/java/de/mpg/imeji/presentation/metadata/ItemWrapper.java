package de.mpg.imeji.presentation.metadata;

import java.net.URI;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.Metadata;

/**
 * Bean for item element in the metadata editors
 *
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ItemWrapper {
  private MetadataSetWrapper mds;
  private Item item;
  private MetadataProfile profile;

  /**
   * Bean for item element in the metadata editors
   *
   * @param item
   */
  public ItemWrapper(Item item, MetadataProfile profile, boolean addEmtpyValue) {
    this.profile = profile;
    this.item = item;
    mds = new MetadataSetWrapper(this.item.getMetadataSet(), this.profile, addEmtpyValue);
  }

  /**
   * Get {@link ItemWrapper} as {@link Item}
   *
   * @return
   */
  public Item asItem() {
    item.getMetadataSet().getMetadata().clear();
    int pos = 0;
    for (MetadataWrapper smdb : mds.getTree().getList()) {
      Metadata md = smdb.asMetadata();
      md.setPos(pos);
      item.getMetadataSet().getMetadata().add(md);
      pos++;
    }
    return item;
  }

  /**
   * Add a Metadata of the same type as the passed metadata
   */
  public void addMetadata(MetadataWrapper smb) {
    MetadataWrapper newMd = smb.copyEmpty();
    newMd.addEmtpyChilds(profile);
    mds.getTree().add(newMd);
  }

  /**
   * Remove the active metadata
   */
  public void removeMetadata(MetadataWrapper smb) {
    mds.getTree().remove(smb);
    if (smb.getParent() != null) {
      smb.getParent().getChilds().remove(smb);
    }
    mds.addEmtpyValues();
  }

  /**
   * Clear the {@link Metadata} for one {@link Statement}: remove all {@link Metadata} and its
   * Childs and add an empty one
   *
   * @param st
   */
  public void clear(Statement st) {
    for (MetadataWrapper smd : mds.getTree().getList()) {
      if (st.getId().compareTo(smd.getStatement().getId()) == 0) {
        // Clear the childs
        for (MetadataWrapper child : mds.getTree().getChilds(smd.getTreeIndex())) {
          child.clear();
        }
        // clear the metadata
        smd.clear();
      }
    }
    // Remove all emtpy values
    mds.trim();
  }

  /**
   * Return the position of the last {@link MetadataWrapper} in the editor for this
   * {@link Statement}
   *
   * @param st
   * @return
   */
  public int getLastPosition(Statement st) {
    int p = 0;
    for (MetadataWrapper smd : mds.getTree().getList()) {
      if (st.getId().compareTo(smd.getStatement().getId()) == 0 && smd.getPos() > p) {
        p = smd.getPos();
      }
    }
    return p;
  }

  /**
   * get the thumbnail of the {@link Item}
   *
   * @return
   */
  public URI getThumbnail() {
    return item.getThumbnailImageUrl();
  }

  /**
   * get the filename of the {@link Item}
   *
   * @return
   */
  public String getFilename() {
    return item.getFilename();
  }

  /**
   * getter
   *
   * @return
   */
  public URI getProfile() {
    return item.getMetadataSet().getProfile();
  }

  /**
   * @return the mds
   */
  public MetadataSetWrapper getMds() {
    return mds;
  }

  /**
   * @param mds the mds to set
   */
  public void setMds(MetadataSetWrapper mds) {
    this.mds = mds;
  }
}
