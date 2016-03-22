package de.mpg.imeji.presentation.beans;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserGroupController;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;

/**
 * Abstract Bean for Status informations (Status + Shared)
 * 
 * @author bastiens
 *
 */
@ManagedBean(name = "StatusBean")
@RequestScoped
public class StatusBean implements Serializable {
  private static final long serialVersionUID = 3560140124183947655L;
  private Status status;
  private String owner;
  private List<String> users = new ArrayList<>();
  private List<String> groups = new ArrayList<>();
  private String linkToSharePage;
  @ManagedProperty(value = "#{Navigation}")
  private Navigation navigation;

  /**
   * Initialize the AbstractBean
   */
  public boolean init(Properties properties) {
    if (properties != null) {
      this.status = properties.getStatus();
      this.users = getUserSharedWith(properties);
      this.groups = getGroupSharedWith(properties);
      this.linkToSharePage = initLinkToSharePage(properties.getId());
      return true;
    }
    return false;
  }

  /**
   * Find all users the object is shared with
   * 
   * @param properties
   * @return
   */
  private List<String> getUserSharedWith(Properties properties) {
    UserController uc = new UserController(Imeji.adminUser);
    List<String> l = new ArrayList<>();
    for (User user : uc.searchByGrantFor(properties.getId().toString())) {
      if (!properties.getCreatedBy().toString().equals(user.getId().toString())) {
        l.add(user.getPerson().getCompleteName());
      } else {
        owner = user.getPerson().getCompleteName();
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
    UserGroupController ugc = new UserGroupController();
    Collection<UserGroup> groups =
        ugc.searchByGrantFor(properties.getId().toString(), Imeji.adminUser);
    for (UserGroup group : groups) {
      l.add(group.getName());
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
    return navigation.getApplicationUri() + uri.getPath() + "/" + Navigation.SHARE.getPath();
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

  public void setNavigation(Navigation navigation) {
    this.navigation = navigation;
  }
}
