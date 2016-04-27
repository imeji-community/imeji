package de.mpg.imeji.presentation.beans;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.controller.resource.UserController;
import de.mpg.imeji.logic.controller.resource.UserGroupController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.album.AlbumBean;
import de.mpg.imeji.presentation.collection.CollectionListItem;

/**
 * Abstract Bean for Status informations (Status + Shared)
 * 
 * @author bastiens
 *
 */
@ManagedBean(name = "StatusBean")
@RequestScoped
public class StatusBean extends SuperViewBean implements Serializable {
  private static final long serialVersionUID = 3560140124183947655L;
  private Status status;
  private String owner;
  private boolean show = false;
  private boolean showManage = false;
  private List<String> users = new ArrayList<>();
  private List<String> groups = new ArrayList<>();
  private String linkToSharePage;
  private static final int COLLABORATOR_LIST_MAX_SIZE = 5;
  private int collaboratorListSize = 0;
  private boolean hasMoreCollaborator = false;

  /**
   * Method called from the JSF compomnent
   * 
   * @param o
   */
  public void init(Object o) {
    if (o instanceof Properties) {
      initialize((Properties) o);
    } else if (o instanceof CollectionListItem) {
      initialize(((CollectionListItem) o).getCollection());
    } else if (o instanceof AlbumBean) {
      initialize(((AlbumBean) o).getAlbum());
    }
  }

  /**
   * Initialize the AbstractBean
   */
  private void initialize(Properties properties) {
    reset();
    if (properties != null) {
      status = properties.getStatus();
      if (AuthUtil.staticAuth().hasReadGrant(getSessionUser(), properties)) {
        users = getUserSharedWith(properties);
        groups = getGroupSharedWith(properties);
        showManage = AuthUtil.staticAuth().administrate(getSessionUser(), properties)
            && !(properties instanceof MetadataProfile);
      }
      linkToSharePage = initLinkToSharePage(properties.getId());
      show = true;
    }
  }

  /**
   * Reset this bean
   */
  private void reset() {
    status = null;
    owner = null;
    show = false;
    showManage = false;
    users = new ArrayList<>();
    groups = new ArrayList<>();
    linkToSharePage = null;
  }

  /**
   * Find all users the object is shared with
   * 
   * @param p
   * @return
   */
  private List<String> getUserSharedWith(Properties p) {
    List<String> l = new ArrayList<>();
    for (User user : findAllUsersWithReadGrant(p)) {
      if (!l.contains(user.getPerson().getCompleteName())) {
        if (!p.getCreatedBy().toString().equals(user.getId().toString())) {
          l.add(user.getPerson().getCompleteName());
          collaboratorListSize++;
        } else {
          owner = user.getPerson().getCompleteName();
        }
      }
      if (collaboratorListSize >= COLLABORATOR_LIST_MAX_SIZE) {
        hasMoreCollaborator = true;
        break;
      }
    }
    return l;
  }



  /**
   * Find all groups the object is shared with
   * 
   * @param properties
   * @return
   */
  private List<String> getGroupSharedWith(Properties properties) {
    List<String> l = new ArrayList<>();
    for (UserGroup group : findAllGroupsWithReadGrant(properties)) {
      if (!l.contains(group.getName())) {
        l.add(group.getName());
        collaboratorListSize++;
      }
      if (collaboratorListSize >= COLLABORATOR_LIST_MAX_SIZE) {
        hasMoreCollaborator = true;
        return l;
      }

    }
    return l;
  }

  /**
   * Find all Users the object is shared with
   * 
   * @param p
   * @return
   */
  private List<User> findAllUsersWithReadGrant(Properties p) {
    UserController uc = new UserController(Imeji.adminUser);
    List<User> l = new ArrayList<>(uc.searchByGrantFor(p.getId().toString()));
    if (p instanceof Item) {
      l.addAll(uc.searchByGrantFor(((Item) p).getCollection().toString()));
    }
    return l;
  }

  /**
   * Find all Groups the object is shared with
   * 
   * @param p
   * @return
   */
  private List<UserGroup> findAllGroupsWithReadGrant(Properties p) {
    UserGroupController ugc = new UserGroupController();
    List<UserGroup> l =
        new ArrayList<>(ugc.searchByGrantFor(p.getId().toString(), Imeji.adminUser));
    if (p instanceof Item) {
      l.addAll(ugc.searchByGrantFor(((Item) p).getCollection().toString(), Imeji.adminUser));
    }
    return l;
  }


  /**
   * Initialize the link to the share page
   * 
   * @param uri
   * @return
   */
  private String initLinkToSharePage(URI uri) {
    return getNavigation().getApplicationUrl()
        + ObjectHelper.getObjectType(uri).name().toLowerCase() + "/" + ObjectHelper.getId(uri) + "/"
        + Navigation.SHARE.getPath();
  }

  /**
   * Return a label for the status
   * 
   * @return
   */
  public String getStatusLabel() {
    if (status == Status.RELEASED) {
      return Imeji.RESOURCE_BUNDLE.getLabel("published", getLocale());
    } else if (status == Status.WITHDRAWN) {
      return Imeji.RESOURCE_BUNDLE.getLabel("withdrawn", getLocale());
    }
    return Imeji.RESOURCE_BUNDLE.getLabel("private", getLocale());
  }

  /**
   * @return the status
   */
  public Status getStatus() {
    return status;
  }

  public List<String> getUsers() {
    return users;
  }

  public List<String> getGroups() {
    return groups;
  }

  public String getOwner() {
    return owner;
  }

  public String getLinkToSharePage() {
    return linkToSharePage;
  }

  public boolean isShowManage() {
    return showManage;
  }

  public boolean isShow() {
    return show;
  }

  public boolean isHasMoreCollaborator() {
    return hasMoreCollaborator;
  }
}
