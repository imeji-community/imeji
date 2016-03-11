package de.mpg.imeji.logic.collaboration.invitation;

import de.mpg.imeji.exceptions.AlreadyExistsException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.User.UserStatus;
import de.mpg.imeji.presentation.util.ImejiFactory;

/**
 * Business Controller for invitation of Users
 * 
 * @author bastiens
 *
 */
public class InvitationBusinessController {
  private final UserController userController = new UserController(Imeji.adminUser);
  private final ShareBusinessController shareBusinessController = new ShareBusinessController();

  /**
   * Invite a user to collaborate for an object
   * 
   * @param invitation
   * @throws ImejiException
   */
  public void invite(Invitation invitation) throws ImejiException {
    User invitee = retrieveInvitee(invitation.getInviteeEmail());
    if (invitee == null) {
      invitee = createInvitedUser(invitation.getInviteeEmail());
      shareBusinessController.shareToUser(invitation.getInvitor(), invitee,
          invitation.getObjectUri(), invitation.getRoles());
    } else {
      throw new AlreadyExistsException(invitation.getInviteeEmail() + "exists already");
    }

  }

  /**
   * Uninvite a User. (if the user is not still invited)
   * 
   * @param invitation
   * @throws ImejiException
   */
  public void uninvite(Invitation invitation) throws ImejiException {
    User invitee = retrieveInvitee(invitation.getInviteeEmail());
    if (invitee.getUserStatus() == UserStatus.INVITED) {
      userController.delete(invitee);
      return;
    }
    throw new ImejiException("User already reigisted");
  }

  /**
   * Create a Invited User
   * 
   * @param email
   * @return
   * @throws ImejiException
   */
  private User createInvitedUser(String email) throws ImejiException {
    User invitee = new User();
    invitee.setEmail(email);
    invitee.setPerson(ImejiFactory.newPerson(email, "", email));
    invitee.setUserStatus(UserStatus.INVITED);
    return userController.create(invitee, USER_TYPE.DEFAULT);
  }

  /**
   * 
   * @param user
   * @return
   */
  private User retrieveInvitee(String email) {
    try {
      return userController.retrieve(email);
    } catch (ImejiException e) {
      return null;
    }
  }

}
