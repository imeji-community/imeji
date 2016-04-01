package de.mpg.imeji.presentation.share;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.collaboration.invitation.Invitation;
import de.mpg.imeji.logic.collaboration.invitation.InvitationBusinessController;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController.ShareRoles;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.share.ShareBean.SharedObjectType;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ListUtils;

public class ShareListItem implements Serializable {
  private static final long serialVersionUID = -1637916656299359982L;
  private static final Logger LOGGER = Logger.getLogger(ShareListItem.class);
  private final User currentUser;
  private Invitation invitation;
  private User user;
  private UserGroup group;
  private String shareToUri;
  private SharedObjectType type;
  private List<String> roles = new ArrayList<String>();
  private String title;
  private String profileUri;

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
   * Constructor with a {@link Invitation}
   * 
   * @param user
   * @param isCollection
   * @param containerUri
   * @param profileUri
   * @param roles
   */
  public ShareListItem(Invitation invitation, SharedObjectType type, String containerUri,
      String profileUri, User currentUser) {
    this.invitation = invitation;
    this.type = type;
    this.shareToUri = containerUri;
    this.currentUser = currentUser;
    init(ShareBusinessController.transformRolesToGrants(invitation.getRoles(), containerUri),
        containerUri, profileUri);
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
    this.profileUri = profileUri;
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
   * According to the selected roles, add necessary roles (called from page as well)
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
    // transform abstract list to real list, to allow modifications
    roles = new ArrayList<>(roles);
  }

  public void revokeGrants() {
    roles.clear();
    update();
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
   * Update the invitation
   * 
   * @throws ImejiException
   */
  public void updateInvitation() throws ImejiException {
    if (invitation != null) {
      InvitationBusinessController invitationBC = new InvitationBusinessController();
      Invitation newInvitation =
          new Invitation(invitation.getInviteeEmail(), invitation.getObjectUri(), roles);
      invitationBC.invite(newInvitation);
    }
  }

  /**
   * Return all users in this items. This might be many user if the item contains a group
   * 
   * @return
   */
  public List<User> getUsers() {
    UserController controller = new UserController(Imeji.adminUser);
    List<User> users = new ArrayList<>();
    if (group != null) {
      for (URI uri : group.getUsers()) {
        try {
          users.add(controller.retrieve(uri));
        } catch (ImejiException e) {
          LOGGER.error("Error retrieving user:" + uri);
        }
      }
    }
    if (user != null) {
      users.add(user);
    }
    return users;
  }

  public List<SelectItem> getRolesMenu() {
    if (type == SharedObjectType.COLLECTION) {
      return ShareUtil.getCollectionRoleMenu(profileUri);
    }
    if (type == SharedObjectType.ALBUM) {
      return ShareUtil.getAlbumRoleMenu();
    }
    return ShareUtil.getItemRoleMenu();
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

  public String getTypeLabel() {
    return Imeji.RESOURCE_BUNDLE.getLabel(type.name().toLowerCase(), BeanHelper.getLocale());
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Invitation getInvitation() {
    return invitation;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
    checkRoles();
  }
}
