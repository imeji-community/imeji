package de.mpg.imeji.presentation.share;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.Imeji;
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

}
