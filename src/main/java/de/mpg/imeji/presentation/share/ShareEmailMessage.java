package de.mpg.imeji.presentation.share;

import java.util.List;

import de.mpg.imeji.logic.collaboration.email.EmailMessages;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.share.ShareBean.SharedObjectType;

/**
 * Email Message when sharing an object
 * 
 * @author bastiens
 *
 */
public class ShareEmailMessage {
  private String body = "";
  private String subject = "";
  private SessionBean session;

  /**
   * Constructor
   * 
   * @param shareToUri
   * @param profileUri
   * @param grants
   * @param type
   * @param session
   */
  public ShareEmailMessage(String addresseeName, String sharedObjectName, String sharedObjectLink,
      String shareToUri, String profileUri, List<String> roles, SharedObjectType type,
      SessionBean session) {
    this.session = session;
    body = initBody(addresseeName, sharedObjectName, sharedObjectLink, type);
    String messageRoles = "";
    switch (type) {
      case ALBUM:
        messageRoles = getMessageForShareAlbum(roles);
        break;
      case COLLECTION:
        messageRoles = getMessageForShareCollection(roles, profileUri);
        break;
      case ITEM:
        messageRoles = getMessageForShareItem(roles);
        break;
    }
    body = body.replaceAll("XXX_RIGHTS_XXX", messageRoles.trim());
  }

  private String initBody(String addresseeName, String sharedObjectName, String sharedObjectLink,
      SharedObjectType type) {
    EmailMessages emailMessages = new EmailMessages();
    String from = session.getUser().getPerson().getCompleteName();
    switch (type) {
      case ALBUM:
        return emailMessages.getSharedAlbumMessage(from, addresseeName, sharedObjectName,
            sharedObjectLink);
      case COLLECTION:
        return emailMessages.getSharedCollectionMessage(from, addresseeName, sharedObjectName,
            sharedObjectLink);
      case ITEM:
        return emailMessages.getSharedItemMessage(from, addresseeName, sharedObjectName,
            sharedObjectLink);
    }
    return null;
  }

  private List<String> toRoleList(String shareToUri, String profileUri, List<Grant> grants) {
    List<String> roles = ShareBusinessController.transformGrantsToRoles(grants, shareToUri);
    if (profileUri != null) {
      for (String profileRole : ShareBusinessController.transformGrantsToRoles(grants,
          profileUri)) {
        if (profileRole.equals("EDIT")) {
          roles.add("EDIT_PROFILE");
        }
      }
    }
    return roles;
  }

  private String getMessageForShareItem(List<String> roles) {
    for (String role : roles) {
      if ("READ".equals(role)) {
        return "- " + session.getLabel("collection_share_read");
      }
    }
    return "";
  }

  private String getMessageForShareCollection(List<String> roles, String profileUri) {
    String message = "";
    for (String role : roles) {
      switch (role) {
        case "READ":
          message += "- " + session.getLabel("collection_share_read") + "\n";
          break;
        case "CREATE":
          message += "- " + session.getLabel("collection_share_image_upload") + "\n";
          break;
        case "EDIT_ITEM":
          message += "- " + session.getLabel("collection_share_image_edit") + "\n";
          break;
        case "DELETE_ITEM":
          message += "- " + session.getLabel("collection_share_image_delete") + "\n";
          break;
        case "EDIT":
          message += "- " + session.getLabel("collection_share_collection_edit") + "\n";
          break;
        case "EDIT_PROFILE":
          message +=
              "- " + session.getLabel("collection_share_profile_edit") + ":  " + profileUri + "\n";
          break;
        case "ADMIN":
          message += "- " + session.getLabel("collection_share_admin") + "\n";
          break;
      }
    }
    return message;
  }

  private String getMessageForShareAlbum(List<String> roles) {
    String message = "";
    for (String role : roles) {
      switch (role) {
        case "READ":
          message += "- " + session.getLabel("album_share_read") + "\n";
          break;
        case "CREATE":
          message += "- " + session.getLabel("album_share_image_add") + "\n";
          break;
        case "EDIT":
          message += "- " + session.getLabel("album_share_album_edit") + "\n";
          break;
        case "ADMIN":
          message += "- " + session.getLabel("album_share_admin") + "\n";
          break;
      }
    }
    return message;
  }

  public String getBody() {
    return body;
  }

  public String getSubject() {
    return subject;
  }

}
