package de.mpg.imeji.presentation.share;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController.ShareRoles;
import de.mpg.imeji.logic.resource.controller.AlbumController;
import de.mpg.imeji.logic.resource.controller.CollectionController;
import de.mpg.imeji.logic.resource.controller.ItemController;
import de.mpg.imeji.logic.resource.vo.Album;
import de.mpg.imeji.logic.resource.vo.CollectionImeji;
import de.mpg.imeji.logic.resource.vo.Grant;
import de.mpg.imeji.logic.resource.vo.Item;
import de.mpg.imeji.logic.resource.vo.User;
import de.mpg.imeji.logic.resource.vo.UserGroup;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.share.ShareBean.SharedObjectType;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Utility class for Share implementation
 * 
 * @author bastiens
 *
 */
public class ShareUtil {
  private static List<SelectItem> itemRoleMenu;
  private static List<SelectItem> collectionRoleMenu;
  private static List<SelectItem> albumRoleMenu;

  private ShareUtil() {
    // private constructor
  }

  /**
   * Menu for sharing collection
   * 
   * @return
   */
  public static List<SelectItem> getCollectionRoleMenu(String profileUri) {
    if (collectionRoleMenu == null) {
      SessionBean sb = getSession();
      collectionRoleMenu = new ArrayList<>();
      collectionRoleMenu.add(new SelectItem(ShareRoles.READ,
          Imeji.RESOURCE_BUNDLE.getLabel("collection_share_read", sb.getLocale())));
      collectionRoleMenu.add(new SelectItem(ShareRoles.CREATE,
          Imeji.RESOURCE_BUNDLE.getLabel("collection_share_image_upload", sb.getLocale())));
      collectionRoleMenu.add(new SelectItem(ShareRoles.EDIT_ITEM,
          Imeji.RESOURCE_BUNDLE.getLabel("collection_share_image_edit", sb.getLocale())));
      collectionRoleMenu.add(new SelectItem(ShareRoles.DELETE_ITEM,
          Imeji.RESOURCE_BUNDLE.getLabel("collection_share_image_delete", sb.getLocale())));
      collectionRoleMenu.add(new SelectItem(ShareRoles.EDIT,
          Imeji.RESOURCE_BUNDLE.getLabel("collection_share_collection_edit", sb.getLocale())));
      if (AuthUtil.staticAuth().administrate(sb.getUser(), profileUri)) {
        collectionRoleMenu.add(new SelectItem(ShareRoles.EDIT_PROFILE,
            Imeji.RESOURCE_BUNDLE.getLabel("collection_share_profile_edit", sb.getLocale())));
      }
      collectionRoleMenu.add(new SelectItem(ShareRoles.ADMIN,
          Imeji.RESOURCE_BUNDLE.getLabel("collection_share_admin", sb.getLocale())));
    }
    return collectionRoleMenu;
  }

  /**
   * Menu for sharing items
   * 
   * @return
   */
  public static List<SelectItem> getItemRoleMenu() {
    if (itemRoleMenu == null) {
      itemRoleMenu = Arrays.asList(new SelectItem(ShareRoles.READ,
          Imeji.RESOURCE_BUNDLE.getLabel("collection_share_read", BeanHelper.getLocale())));
    }
    return itemRoleMenu;
  }

  /**
   * Menu for sharing Album
   * 
   * @return
   */
  public static List<SelectItem> getAlbumRoleMenu() {
    SessionBean session = getSession();
    if (albumRoleMenu == null) {
      albumRoleMenu = Arrays.asList(
          new SelectItem(ShareRoles.READ,
              Imeji.RESOURCE_BUNDLE.getLabel("album_share_read", session.getLocale())),
          new SelectItem(ShareRoles.CREATE,
              Imeji.RESOURCE_BUNDLE.getLabel("album_share_image_add", session.getLocale())),
          new SelectItem(ShareRoles.EDIT,
              Imeji.RESOURCE_BUNDLE.getLabel("album_share_album_edit", session.getLocale())),
          new SelectItem(ShareRoles.ADMIN,
              Imeji.RESOURCE_BUNDLE.getLabel("album_share_admin", session.getLocale())));
    }
    return albumRoleMenu;
  }

  /**
   * Return the current session
   * 
   * @return
   */
  private static SessionBean getSession() {
    return (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
  }


  /**
   * Read the role of the {@link User}
   * 
   * @return
   * @throws Exception
   */
  public static List<ShareListItem> getAllRoles(User user, User sessionUser) throws Exception {
    List<String> shareToList = new ArrayList<String>();
    for (Grant g : user.getGrants()) {
      if (g.getGrantFor() != null) {
        if (!shareToList.contains(g.getGrantFor().toString())) {
          shareToList.add(g.getGrantFor().toString());
        }
      }
    }
    List<ShareListItem> roles = new ArrayList<ShareListItem>();
    for (String sharedWith : shareToList) {
      if (sharedWith.contains("/collection/")) {
        CollectionImeji c =
            new CollectionController().retrieveLazy(URI.create(sharedWith), sessionUser);
        if (c != null) {
          roles.add(new ShareListItem(user, SharedObjectType.COLLECTION, sharedWith,
              c.getProfile() != null ? c.getProfile().toString() : null, c.getMetadata().getTitle(),
              sessionUser));
        }
      } else if (sharedWith.contains("/album/")) {
        Album a = new AlbumController().retrieveLazy(URI.create(sharedWith), sessionUser);
        if (a != null) {
          roles.add(new ShareListItem(user, SharedObjectType.ALBUM, sharedWith, null,
              a.getMetadata().getTitle(), sessionUser));
        }
      } else if (sharedWith.contains("/item/")) {
        Item it = new ItemController().retrieveLazy(URI.create(sharedWith), sessionUser);
        if (it != null) {
          roles.add(new ShareListItem(user, SharedObjectType.ITEM, sharedWith, null,
              it.getFilename(), sessionUser));
        }
      }
    }
    return roles;
  }

  /**
   * Read the role of the {@link UserGroup}
   * 
   * @return
   * @throws Exception
   */
  public static List<ShareListItem> getAllRoles(UserGroup group, User sessionUser)
      throws Exception {
    List<String> shareToList = new ArrayList<String>();
    for (Grant g : group.getGrants()) {
      if (!shareToList.contains(g.getGrantFor().toString())) {
        shareToList.add(g.getGrantFor().toString());
      }
    }
    List<ShareListItem> roles = new ArrayList<ShareListItem>();
    for (String sharedWith : shareToList) {
      if (sharedWith.contains("/collection/")) {
        CollectionImeji c =
            new CollectionController().retrieveLazy(URI.create(sharedWith), sessionUser);
        roles.add(new ShareListItem(group, SharedObjectType.COLLECTION, sharedWith,
            c.getProfile().toString(), c.getMetadata().getTitle(), sessionUser));
      } else if (sharedWith.contains("/album/")) {
        Album a = new AlbumController().retrieveLazy(URI.create(sharedWith), sessionUser);
        roles.add(new ShareListItem(group, SharedObjectType.ALBUM, sharedWith, null,
            a.getMetadata().getTitle(), sessionUser));
      } else if (sharedWith.contains("/item/")) {
        ItemController c = new ItemController();
        Item it = c.retrieveLazy(URI.create(sharedWith), sessionUser);
        roles.add(new ShareListItem(group, SharedObjectType.ITEM, sharedWith, null,
            it.getFilename(), sessionUser));
      }
    }
    return roles;
  }
}
