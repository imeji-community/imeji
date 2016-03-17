package de.mpg.imeji.presentation.share;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.PrettyContext;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.collaboration.email.EmailMessages;
import de.mpg.imeji.logic.collaboration.email.EmailService;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController.ShareRoles;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserGroupController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.history.HistoryUtil;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.UserGroupsBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

@ManagedBean(name = "ShareBean")
@ViewScoped
public class ShareBean implements Serializable {
  private static final long serialVersionUID = 8106762709528360926L;
  private static final Logger LOGGER = Logger.getLogger(ShareBean.class);
  private String id;
  private URI uri;
  // The object (collection, album or item) which is going to be shared
  private Object shareTo;
  // the user whom the shared object belongs
  private URI owner;
  private String title;
  private String profileUri;
  private boolean isAdmin;
  private boolean sendEmail = false;
  private UserGroup userGroup;
  private SharedObjectType type;
  // The url of the current share page (used for back link)
  private String pageUrl;
  @ManagedProperty("#{UserGroups}")
  private UserGroupsBean userGroupsBean;
  @ManagedProperty("#{SessionBean}")
  private SessionBean sb;
  private ShareInput input;
  private ShareList shareList;

  public enum SharedObjectType {
    COLLECTION, ALBUM, ITEM
  }

  /**
   * Init {@link ShareBean} for {@link CollectionImeji}
   * 
   * @throws ImejiException
   * 
   * @throws Exception
   */
  public void initShareCollection() throws ImejiException {
    this.shareTo = null;
    this.profileUri = null;
    this.type = SharedObjectType.COLLECTION;
    this.uri = ObjectHelper.getURI(CollectionImeji.class, getId());
    CollectionImeji collection = ObjectLoader.loadCollectionLazy(uri, sb.getUser());
    if (collection != null) {
      this.shareTo = collection;
      this.profileUri = collection.getProfile() != null ? collection.getProfile().toString() : null;
      this.title = collection.getMetadata().getTitle();
      this.owner = collection.getCreatedBy();
    }
    this.init();
  }

  /**
   * Init {@link ShareBean} for {@link Album}
   * 
   * @throws Exception
   */
  public void initShareAlbum() throws ImejiException {
    this.type = SharedObjectType.ALBUM;
    this.shareTo = null;
    this.profileUri = null;
    this.uri = ObjectHelper.getURI(Album.class, getId());
    Album album = ObjectLoader.loadAlbumLazy(uri, sb.getUser());
    if (album != null) {
      this.shareTo = album;
      this.title = album.getMetadata().getTitle();
      this.owner = album.getCreatedBy();
    }
    this.init();
  }

  /**
   * Loaded when the shre component is called from the item page
   * 
   * @return
   * @throws Exception
   */
  public String getInitShareItem() throws ImejiException {
    this.type = SharedObjectType.ITEM;
    this.profileUri = null;
    this.shareTo = null;
    this.uri =
        HistoryUtil.extractURI(PrettyContext.getCurrentInstance().getRequestURL().toString());
    Item item = new ItemController().retrieveLazy(uri, sb.getUser());
    if (item != null) {
      this.shareTo = item;
      this.title = item.getFilename();
      this.owner = item.getCreatedBy();
    }
    this.init();
    return "";
  }

  /**
   * Init method for {@link ShareBean}
   */
  public void init() {
    input = new ShareInput(uri.toString(), type, profileUri, title, sb);
    shareList = new ShareList(owner, uri.toString(), profileUri, type, sb.getUser());
    isAdmin = AuthUtil.staticAuth().administrate(sb.getUser(), shareTo);
    pageUrl = PrettyContext.getCurrentInstance().getRequestURL().toString()
        + PrettyContext.getCurrentInstance().getRequestQueryString();
    pageUrl = pageUrl.split("[&\\?]group=")[0];
    initShareWithGroup();
  }

  /**
   * Check in the url if a {@link UserGroup} should be shared with the currentContainer
   */
  private void initShareWithGroup() {
    this.userGroup = null;
    String groupToShareWithUri = UrlHelper.getParameterValue("group");
    if (groupToShareWithUri != null) {
      UserGroup group = retrieveGroup(groupToShareWithUri);
      if (group != null) {
        userGroup = group;
      }
    }
  }

  /**
   * Update the page accodring to new changes
   * 
   * @return
   */
  public void update() {
    for (ShareListItem item : shareList.getItems()) {
      boolean modified = item.update();
      if (sendEmail && modified) {
        sendEmailForShare(item, title);
      }
    }
    reloadPage();
  }

  /**
   * Check the input and add all correct entry to the list of elements to be saved
   */
  public void share() {
    if (input.share()) {
      if (sendEmail) {
        for (ShareListItem item : input.getExistingUsersAsShareListItems()) {
          sendEmailForShare(item, title);
        }
      }
      reloadPage();
    }
  }

  /**
   * Invite the new users
   */
  public void invite() {
    input.shareAndSendInvitations();
    reloadPage();
  }

  /**
   * Unshare theContainer for one {@link User} (i.e, remove all {@link Grant} of this {@link User}
   * related to theContainer)
   * 
   * @param sh
   */
  public void unshare(ShareListItem item) {
    item.getRoles().clear();
    item.update();
    if (sendEmail) {
      sendEmailUnshare(item, title);
    }
    reloadPage();
  }

  /**
   * Called when user share with a group
   */
  public void shareWithGroup() {
    ShareListItem groupListItem =
        new ShareListItem(userGroup, type, uri.toString(), profileUri, null, sb.getUser());
    groupListItem.update();
    reloadPage();
  }


  /**
   * Remove an unknow Email from the list (no invitation will be sent to him)
   * 
   * @param pos
   */
  public void removeUnknowEmail(int pos) {
    input.getUnknownEmails().remove(pos);
  }


  /**
   * Reload the current page
   */
  private void reloadPage() {
    try {
      sb.reloadUser();
      Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
      if (AuthUtil.staticAuth().administrate(sb.getUser(), uri.toString())) {
        // user has still rights to read the collection
        FacesContext.getCurrentInstance().getExternalContext()
            .redirect(navigation.getApplicationUri() + pageUrl);
      } else if (AuthUtil.staticAuth().read(sb.getUser(), uri.toString())) {
        FacesContext.getCurrentInstance().getExternalContext()
            .redirect(navigation.getApplicationUri() + pageUrl.replace("share", ""));
      } else {
        // user has not right anymore to read the collection
        switch (type) {
          case COLLECTION:
            FacesContext.getCurrentInstance().getExternalContext()
                .redirect(navigation.getCollectionsUrl());
            break;
          case ALBUM:
            FacesContext.getCurrentInstance().getExternalContext()
                .redirect(navigation.getAlbumsUrl());
            break;
          case ITEM:
            FacesContext.getCurrentInstance().getExternalContext()
                .redirect(navigation.getBrowseUrl());
            break;
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Error reloading page " + pageUrl);
    }
  }

  /**
   * Send an Email...
   * 
   * @param email
   * @param subject
   * @param body
   */
  private void sendEmail(String email, String subject, String body) {
    try {
      new EmailService().sendMail(email, null,
          subject.replaceAll("XXX_INSTANCE_NAME_XXX", sb.getInstanceName()), body);
    } catch (Exception e) {
      LOGGER.error("Error sending email", e);
      BeanHelper.error(sb.getMessage("error") + ": Email not sent");
    }
  }

  /**
   * Send Email for each ShareListItem (User of Group) that object has been shared and with which
   * Grants
   * 
   * @param item
   * @param subject
   */
  private void sendEmailForShare(ShareListItem item, String subject) {
    for (User user : item.getUsers()) {
      ShareEmailMessage emailMessage = new ShareEmailMessage(user.getPerson().getCompleteName(),
          title, getLinkToSharedObject(), getShareToUri(), profileUri, item.getRoles(), type, sb);
      sendEmail(user.getEmail(), subject.replaceAll("XXX_INSTANCE_NAME_XXX", sb.getInstanceName()),
          emailMessage.getBody());
    }
  }


  /**
   * Send email to the user(s) for which the object has been unshared
   * 
   * @param dest
   * @param subject
   * @param grants
   */
  private void sendEmailUnshare(ShareListItem item, String subject) {
    subject = subject.replaceAll("XXX_INSTANCE_NAME_XXX", sb.getInstanceName());
    for (User user : item.getUsers()) {
      String body =
          new EmailMessages().getUnshareMessage(sb.getUser().getPerson().getCompleteName(),
              user.getPerson().getCompleteName(), title, getLinkToSharedObject());
      sendEmail(user.getEmail(), subject, body);
    }
  }


  /**
   * Search a {@link UserGroup} by name
   * 
   * @param uri
   * @return
   */
  private UserGroup retrieveGroup(String uri) {
    UserGroupController c = new UserGroupController();
    try {
      return c.read(uri, Imeji.adminUser);
    } catch (Exception e) {
      return null;
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public boolean isAdmin() {
    return isAdmin;
  }

  public void setAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public String getShareToUri() {
    if (shareTo instanceof Properties) {
      return ((Properties) shareTo).getId().toString();
    }
    return null;
  }

  private String getLinkToSharedObject() {
    Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
    switch (type) {
      case COLLECTION:
        return navigation.getCollectionUrl() + ((Properties) shareTo).getIdString();
      case ALBUM:
        return navigation.getAlbumUrl() + ((Properties) shareTo).getIdString();
      case ITEM:
        return navigation.getItemUrl() + ((Properties) shareTo).getIdString();
    }
    return null;
  }

  public Object getShareTo() {
    return shareTo;
  }

  public void setShareToUri(Object shareTo) {
    this.shareTo = shareTo;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the sendEmail
   */
  public boolean isSendEmail() {
    return sendEmail;
  }

  /**
   * @param sendEmail the sendEmail to set
   */
  public void setSendEmail(boolean sendEmail) {
    this.sendEmail = sendEmail;
  }

  /**
   * @return the userGroup
   */
  public UserGroup getUserGroup() {
    return userGroup;
  }

  /**
   * @param userGroup the userGroup to set
   */
  public void setUserGroup(UserGroup userGroup) {
    this.userGroup = userGroup;
  }

  public String getPageUrl() {
    return pageUrl;
  }

  public void setPageUrl(String pageUrl) {
    this.pageUrl = pageUrl;
  }

  public SharedObjectType getType() {
    return type;
  }

  public void setType(SharedObjectType type) {
    this.type = type;
  }

  public SessionBean getSb() {
    return sb;
  }

  public void setSb(SessionBean sb) {
    this.sb = sb;
  }

  /**
   * Menu for sharing collection
   * 
   * @return
   */
  public List<SelectItem> getShareCollectionGrantItems() {
    List<SelectItem> itemList = new ArrayList<SelectItem>();
    itemList.add(new SelectItem(ShareRoles.READ, sb.getLabel("collection_share_read")));
    itemList.add(new SelectItem(ShareRoles.CREATE, sb.getLabel("collection_share_image_upload")));
    itemList.add(new SelectItem(ShareRoles.EDIT_ITEM, sb.getLabel("collection_share_image_edit")));
    itemList
        .add(new SelectItem(ShareRoles.DELETE_ITEM, sb.getLabel("collection_share_image_delete")));
    itemList.add(new SelectItem(ShareRoles.EDIT, sb.getLabel("collection_share_collection_edit")));
    if (AuthUtil.staticAuth().administrate(sb.getUser(), profileUri)) {
      itemList.add(
          new SelectItem(ShareRoles.EDIT_PROFILE, sb.getLabel("collection_share_profile_edit")));
    }
    itemList.add(new SelectItem(ShareRoles.ADMIN, sb.getLabel("collection_share_admin")));
    return itemList;
  }

  /**
   * Menu for sharing items
   * 
   * @return
   */
  public List<SelectItem> getShareItemGrantItems() {
    List<SelectItem> itemList = new ArrayList<SelectItem>();
    itemList.add(new SelectItem(ShareRoles.READ, sb.getLabel("collection_share_read")));
    return itemList;
  }

  /**
   * Menu for sharing Album
   * 
   * @return
   */
  public List<SelectItem> getShareAlbumGrantItems() {
    List<SelectItem> itemList = new ArrayList<SelectItem>();
    itemList.add(new SelectItem(ShareRoles.READ, sb.getLabel("album_share_read")));
    itemList.add(new SelectItem(ShareRoles.CREATE, sb.getLabel("album_share_image_add")));
    itemList.add(new SelectItem(ShareRoles.EDIT, sb.getLabel("album_share_album_edit")));
    itemList.add(new SelectItem(ShareRoles.ADMIN, sb.getLabel("album_share_admin")));
    return itemList;
  }


  public UserGroupsBean getUserGroupsBean() {
    return userGroupsBean;
  }

  public void setUserGroupsBean(UserGroupsBean ugroupsBean) {
    this.userGroupsBean = ugroupsBean;
  }

  /**
   * @return the input
   */
  public ShareInput getInput() {
    return input;
  }

  /**
   * @param input the input to set
   */
  public void setInput(ShareInput input) {
    this.input = input;
  }

  /**
   * @return the shareList
   */
  public ShareList getShareList() {
    return shareList;
  }

  /**
   * @param shareList the shareList to set
   */
  public void setShareList(ShareList shareList) {
    this.shareList = shareList;
  }
}
