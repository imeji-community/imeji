package de.mpg.imeji.presentation.share;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserGroupController;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.share.ShareBean.SharedObjectType;

/**
 * A list a share Entries
 * 
 * @author bastiens
 *
 */
public class ShareList {
  private List<ShareListItem> items = new ArrayList<ShareListItem>();


  public ShareList(URI ownerUri, String sharedObjectUri, String profileUri, SharedObjectType type,
      User currentUser) {
    UserController uc = new UserController(Imeji.adminUser);
    Collection<User> allUser = uc.searchByGrantFor(sharedObjectUri);
    items = new ArrayList<>();
    for (User u : allUser) {
      // Do not display the creator of this collection here
      if (!u.getId().toString().equals(ownerUri.toString())) {
        items
            .add(new ShareListItem(u, type, sharedObjectUri, profileUri, null, currentUser));
      }
    }
    UserGroupController ugc = new UserGroupController();
    Collection<UserGroup> groups = ugc.searchByGrantFor(sharedObjectUri, Imeji.adminUser);
    for (UserGroup group : groups) {
      items
          .add(new ShareListItem(group, type, sharedObjectUri, profileUri, null, currentUser));
    }
  }

  public List<ShareListItem> getItems() {
    return items;
  }

}
