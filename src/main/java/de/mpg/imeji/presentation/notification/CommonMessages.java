package de.mpg.imeji.presentation.notification;

import de.mpg.imeji.presentation.session.SessionBean;

/**
 * Helper class for common messages support
 *
 * Created by vlad on 26.03.15.
 */
public class CommonMessages {

  public static String getSuccessCollectionDeleteMessage(String collectionName, SessionBean sb) {
    return sb.getMessage("success_collection_delete").replace("XXX_collectionName_XXX",
        collectionName);
  }

}
