package de.mpg.imeji.presentation.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.collaboration.email.EmailService;
import de.mpg.imeji.logic.collaboration.invitation.Invitation;
import de.mpg.imeji.logic.collaboration.invitation.InvitationBusinessController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.share.ShareBean.SharedObjectType;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * The Input for Share Page
 * 
 * @author bastiens
 *
 */
public class ShareInput implements Serializable {
  private static final long serialVersionUID = 3979846119253696328L;
  private static final Logger LOGGER = Logger.getLogger(ShareInput.class);

  private String input = "";
  private ShareListItem menu;
  private List<String> validEmails = new ArrayList<>();
  private List<String> invalidEntries = new ArrayList<>();
  private List<String> unknownEmails = new ArrayList<>();
  private final String objectUri;
  private final String profileUri;
  private final SharedObjectType type;
  private final SessionBean sb;
  private final String title;

  /**
   * Constructor
   * 
   * @param objectUri
   */
  public ShareInput(String objectUri, SharedObjectType type, String profileUri, String title,
      SessionBean sb) {
    this.objectUri = objectUri;
    this.type = type;
    this.profileUri = profileUri;
    this.title = title;
    this.sb = sb;
    this.menu = new ShareListItem(type, objectUri, profileUri, sb.getUser());
  }

  /**
   * Share Button: If invalid
   */
  public boolean share() {
    parseInput();
    if (invalidEntries.isEmpty() && unknownEmails.isEmpty()) {
      shareWithValidEmails();
      return true;
    }
    return false;
  }

  /**
   * Send Invitations to unknown Emails and share to everybody (valid Emails and unknown Emails)
   */
  public void shareAndSendInvitations() {
    shareWithValidEmails();
    InvitationBusinessController invitationBC = new InvitationBusinessController();
    EmailService emailService = new EmailService();
    for (String invitee : unknownEmails) {
      try {
        invitationBC.invite(new Invitation(invitee, objectUri, menu.getRoles()));
        emailService.sendMail(invitee, null, sb.getMessage("invitation_share_subject"),
            getInvitationMessage());
      } catch (ImejiException e) {
        BeanHelper.error(sb.getMessage("error_send_invitation"));
        LOGGER.error("Error sending invitation:", e);
      }
    }
  }

  /**
   * @return the invitation message
   */
  private String getInvitationMessage() {
    return sb.getMessage("invitation_share_body").replace("XXX_OBJECT_NAME_XXX", title);
  }

  /**
   * Return the existing users as list of {@link ShareListItem}
   * 
   * @return
   */
  public List<ShareListItem> getExistingUsersAsShareListItems() {
    return toShareListItem(validEmails);
  }

  /**
   * Remove an unknow Email from the list (no invitation will be sent to him)
   * 
   * @param pos
   */
  public String removeUnknownEmail(int pos) {
    unknownEmails.remove(pos);
    return unknownEmails.isEmpty() ? "pretty:" : "";
  }

  /**
   * Share with existing users
   */
  private void shareWithValidEmails() {
    for (ShareListItem shareListItem : toShareListItem(validEmails)) {
      shareListItem.update();
    }
  }


  /**
   * Create a ShareListItem for an Email according to the current selected roles
   * 
   * @param emails
   * @return
   */
  private List<ShareListItem> toShareListItem(List<String> emails) {
    List<ShareListItem> listItems = new ArrayList<ShareListItem>();
    for (String email : emails) {
      ShareListItem item =
          new ShareListItem(retrieveUser(email), type, objectUri, profileUri, null, sb.getUser());
      item.setRoles(menu.getRoles());
      listItems.add(item);
    }
    return listItems;
  }


  /**
   * Parse the Input to a list of Emails. Add Unknown emails to externaluser list and invalid Emails
   * to invalideEntries
   * 
   * @return
   */
  private void parseInput() {
    validEmails.clear();
    unknownEmails.clear();
    invalidEntries.clear();
    for (String value : input.split("\\s*[|,;\\n]\\s*")) {
      if (EmailService.isValidEmail(value) && !value.equalsIgnoreCase(sb.getUser().getEmail())) {
        boolean exists = retrieveUser(value) != null;
        if (exists) {
          validEmails.add(value);
        } else {
          unknownEmails.add(value);
        }
      } else {
        invalidEntries
            .add(sb.getMessage("error_share_invalid_email").replace("XXX_VALUE_XXX", value));
      }
    }
  }

  /**
   * Retrieve the user. If not existing, return null
   * 
   * @param email
   * @return
   */
  private User retrieveUser(String email) {
    UserController controller = new UserController(Imeji.adminUser);
    try {
      return controller.retrieve(email);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * @return the input
   */
  public String getInput() {
    return input;
  }

  /**
   * @param input the input to set
   */
  public void setInput(String input) {
    this.input = input;
  }

  /**
   * @return the invalidEntries
   */
  public List<String> getInvalidEntries() {
    return invalidEntries;
  }

  /**
   * @param invalidEntries the invalidEntries to set
   */
  public void setInvalidEntries(List<String> invalidEntries) {
    this.invalidEntries = invalidEntries;
  }


  /**
   * @return the validEmails
   */
  public List<String> getValidEmails() {
    return validEmails;
  }

  /**
   * @param validEmails the validEmails to set
   */
  public void setValidEmails(List<String> validEmails) {
    this.validEmails = validEmails;
  }

  /**
   * @return the unknownEmails
   */
  public List<String> getUnknownEmails() {
    return unknownEmails;
  }

  /**
   * @param unknownEmails the unknownEmails to set
   */
  public void setUnknownEmails(List<String> unknownEmails) {
    this.unknownEmails = unknownEmails;
  }

  public ShareListItem getMenu() {
    return menu;
  }

  public void setMenu(ShareListItem menu) {
    this.menu = menu;
  }
}
