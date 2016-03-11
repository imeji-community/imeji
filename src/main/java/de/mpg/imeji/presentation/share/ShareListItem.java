package de.mpg.imeji.presentation.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController.ShareRoles;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.share.ShareBean.SharedObjectType;
import de.mpg.imeji.presentation.util.ListUtils;

public class ShareListItem implements Serializable {
  private static final long serialVersionUID = -1637916656299359982L;
  private static final Logger LOGGER = Logger.getLogger(ShareListItem.class);
  private final User currentUser;
  private User user;
  private UserGroup group;
  private String shareToUri;
  private SharedObjectType type;
  private List<String> roles = new ArrayList<String>();
  private String title;

  /**
   * Constructor without User of Group (To be used as menu)
   * 
   * @param user
   * @param isCollection
   * @param containerUri
   * @param profileUri
   * @param roles
   */
  public ShareListItem(SharedObjectType type, String containerUri, String profileUri,
      User currentUser) {
    this.type = type;
    this.shareToUri = containerUri;
    this.currentUser = currentUser;
    init(new ArrayList<>(), containerUri, profileUri);
  }



  /**
   * Constructor with a {@link User}
   * 
   * @param user
   * @param isCollection
   * @param containerUri
   * @param profileUri
   * @param roles
   */
  public ShareListItem(User user, SharedObjectType type, String containerUri, String profileUri,
      String title, User currentUser) {
    this.user = user;
    this.type = type;
    this.shareToUri = containerUri;
    this.title = title;
    this.currentUser = currentUser;
    init((List<Grant>) user.getGrants(), containerUri, profileUri);
  }

  /**
   * Constructor with a {@link UserGroup}
   * 
   * @param group
   * @param isCollection
   * @param containerUri
   * @param profileUri
   * @param roles
   */
  public ShareListItem(UserGroup group, SharedObjectType type, String containerUri,
      String profileUri, String title, User currentUser) {
    this.setGroup(group);
    this.type = type;
    this.shareToUri = containerUri;
    this.title = title;
    this.currentUser = currentUser;
    init((List<Grant>) group.getGrants(), containerUri, profileUri);
  }

  /**
   * Initialize the menu
   * 
   * @param grants
   * @param uri
   * @param profileUri
   */
  private void init(List<Grant> grants, String uri, String profileUri) {
    roles = ShareBusinessController.transformGrantsToRoles((List<Grant>) grants, uri);
    if (profileUri != null) {
      List<String> profileRoles =
          ShareBusinessController.transformGrantsToRoles((List<Grant>) grants, profileUri);
      if (profileRoles.contains(ShareRoles.EDIT.toString())) {
        roles.add(ShareRoles.EDIT_PROFILE.toString());
      }
    }
    checkRoles();
  }

  /**
   * According to the selected roles, add necessary roles
   */
  public void checkRoles() {
    switch (type) {
      case COLLECTION:
        if (roles.contains("ADMIN")) {
          roles = Arrays.asList(ShareRoles.READ.toString(), ShareRoles.CREATE.toString(),
              ShareRoles.EDIT_ITEM.toString(), ShareRoles.DELETE_ITEM.toString(),
              ShareRoles.EDIT.toString(), ShareRoles.EDIT_PROFILE.toString(),
              ShareRoles.ADMIN.toString());
        } else {
          if (!roles.contains("READ"))
            roles.add(ShareRoles.READ.toString());
        }
        break;
      case ALBUM:
        if (roles.contains("ADMIN")) {
          roles = Arrays.asList(ShareRoles.READ.toString(), ShareRoles.CREATE.toString(),
              ShareRoles.EDIT.toString(), ShareRoles.ADMIN.toString());
        } else {
          if (!roles.contains("READ"))
            roles.add(ShareRoles.READ.toString());
        }
        break;
      case ITEM:
        roles = Arrays.asList(ShareRoles.READ.toString());
        break;
    }
  }

  public void revokeGrants() {
    roles.clear();
    update();
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
    checkRoles();
  }

  /**
   * Update {@link Grants} the {@link ShareListItem} according to the new roles. Return true if the
   * user grant have been modified
   * 
   * @return
   */
  public boolean update() {
    ShareBusinessController sbc = new ShareBusinessController();
    List<String> rolesBeforeUpdate = new ArrayList<>();
    List<String> rolesAfterUpdate = new ArrayList<>();
    try {
      if (user != null) {
        rolesBeforeUpdate = ShareBusinessController
            .transformGrantsToRoles((List<Grant>) user.getGrants(), shareToUri);
        sbc.shareToUser(currentUser, user, shareToUri, roles);
        rolesAfterUpdate = ShareBusinessController
            .transformGrantsToRoles((List<Grant>) user.getGrants(), shareToUri);
      } else if (group != null) {
        rolesBeforeUpdate = ShareBusinessController
            .transformGrantsToRoles((List<Grant>) group.getGrants(), shareToUri);
        sbc.shareToGroup(currentUser, group, shareToUri, roles);
        rolesAfterUpdate = ShareBusinessController
            .transformGrantsToRoles((List<Grant>) group.getGrants(), shareToUri);
      }
      return !ListUtils.equalsIgnoreOrder(rolesBeforeUpdate, rolesAfterUpdate);
    } catch (ImejiException e) {
      LOGGER.error("Error updating grants: ", e);
    }
    return false;
  }

  /**
   * @return the group
   */
  public UserGroup getGroup() {
    return group;
  }

  /**
   * @param group the group to set
   */
  public void setGroup(UserGroup group) {
    this.group = group;
  }

  /**
   * @return the shareToUri
   */
  public String getShareToUri() {
    return shareToUri;
  }

  /**
   * @param shareToUri the shareToUri to set
   */
  public void setShareToUri(String shareToUri) {
    this.shareToUri = shareToUri;
  }

  /**
   * @return the type
   */
  public SharedObjectType getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(SharedObjectType type) {
    this.type = type;
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
}
