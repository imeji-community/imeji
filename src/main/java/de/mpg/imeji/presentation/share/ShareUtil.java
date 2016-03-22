package de.mpg.imeji.presentation.share;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController.ShareRoles;
import de.mpg.imeji.presentation.session.SessionBean;
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
      collectionRoleMenu.add(new SelectItem(ShareRoles.READ, sb.getLabel("collection_share_read")));
      collectionRoleMenu
          .add(new SelectItem(ShareRoles.CREATE, sb.getLabel("collection_share_image_upload")));
      collectionRoleMenu
          .add(new SelectItem(ShareRoles.EDIT_ITEM, sb.getLabel("collection_share_image_edit")));
      collectionRoleMenu.add(
          new SelectItem(ShareRoles.DELETE_ITEM, sb.getLabel("collection_share_image_delete")));
      collectionRoleMenu
          .add(new SelectItem(ShareRoles.EDIT, sb.getLabel("collection_share_collection_edit")));
      if (AuthUtil.staticAuth().administrate(sb.getUser(), profileUri)) {
        collectionRoleMenu.add(
            new SelectItem(ShareRoles.EDIT_PROFILE, sb.getLabel("collection_share_profile_edit")));
      }
      collectionRoleMenu
          .add(new SelectItem(ShareRoles.ADMIN, sb.getLabel("collection_share_admin")));
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
      SessionBean sb = getSession();
      itemRoleMenu =
          Arrays.asList(new SelectItem(ShareRoles.READ, sb.getLabel("collection_share_read")));
    }
    return itemRoleMenu;
  }

  /**
   * Menu for sharing Album
   * 
   * @return
   */
  public static List<SelectItem> getAlbumRoleMenu() {
    if (albumRoleMenu == null) {
      SessionBean sb = getSession();
      albumRoleMenu =
          Arrays.asList(new SelectItem(ShareRoles.READ, sb.getLabel("album_share_read")),
              new SelectItem(ShareRoles.CREATE, sb.getLabel("album_share_image_add")),
              new SelectItem(ShareRoles.EDIT, sb.getLabel("album_share_album_edit")),
              new SelectItem(ShareRoles.ADMIN, sb.getLabel("album_share_admin")));
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

}
