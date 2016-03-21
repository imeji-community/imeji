package de.mpg.imeji.logic.collaboration.invitation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.AlreadyExistsException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.message.KeyValueStoreBusinessController;
import de.mpg.imeji.logic.vo.User;

/**
 * Business Controller for invitation of Users.
 * 
 * @author bastiens
 *
 */
public class InvitationBusinessController {
  private final UserController userController = new UserController(Imeji.adminUser);
  private final ShareBusinessController shareBC = new ShareBusinessController();
  private final KeyValueStoreBusinessController keyValueStoreBC =
      new KeyValueStoreBusinessController();
  private static final Logger LOGGER = Logger.getLogger(InvitationBusinessController.class);

  /**
   * Invite a user to collaborate for an object
   * 
   * @param invitation
   * @throws ImejiException
   */
  public void invite(Invitation invitation) throws ImejiException {
    User invitee = retrieveInvitee(invitation.getInviteeEmail());
    if (invitee == null) {
      add(invitation);
    } else {
      throw new AlreadyExistsException(invitation.getInviteeEmail() + "exists already");
    }
  }

  /**
   * Check for pending invitations of this User, add grants to this users according to the
   * invitation, remove the invitations
   * 
   * @param user
   * @return
   * @throws ImejiException
   */
  public User consume(User user) throws ImejiException {
    for (Invitation invitation : retrieveInvitationOfUser(user.getEmail())) {
      shareBC.shareToUser(Imeji.adminUser, user, invitation.getObjectUri(), invitation.getRoles());
      cancel(invitation.getId());
    }
    return user;
  }

  /**
   * Cancel an Invitation. (if invitation has not been already consumed)
   * 
   * @param invitation
   * @throws ImejiException
   */
  public void cancel(String invitationId) throws ImejiException {
    Invitation invitation = retrieve(invitationId);
    remove(invitation);
  }

  /**
   * Retrieve an Invitation
   * 
   * @param id
   * @return
   * @throws ImejiException
   */
  public Invitation retrieve(String id) throws ImejiException {
    Object invitation = keyValueStoreBC.get(id);
    if (invitation instanceof Invitation) {
      return (Invitation) invitation;
    }
    throw new NotFoundException("No invitation found with id: " + id);
  }

  /**
   * Retrieve all the invitation for this object id
   * 
   * @param objectUri
   * @return
   * @throws ImejiException
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public List<Invitation> retrieveInvitationsOfObject(String objectUri) throws ImejiException {
    return keyValueStoreBC.getList("invitation:.*:" + objectUri, Invitation.class);
  }

  /**
   * Return all Invitationd for this user (according to his Email)
   * 
   * @param email
   * @return
   * @throws ImejiException
   */
  public List<Invitation> retrieveInvitationOfUser(String email) throws ImejiException {
    return keyValueStoreBC.getList("invitation:" + email + ":.*", Invitation.class);
  }

  /**
   * Get all pending invitations
   * 
   * @return
   * @throws ImejiException
   */
  public List<Invitation> retrieveAll() throws ImejiException {
    return keyValueStoreBC.getList("invitation:.*:.*", Invitation.class);
  }

  /**
   * remove all pending invitations
   * 
   * @throws ImejiException
   */
  public void clear() throws ImejiException {
    for (Invitation invitation : retrieveAll()) {
      remove(invitation);
    }
  }

  /**
   * Add an Invitation to the store with relations (email->Invitation and object->invitation)
   * 
   * @param invitation
   * @throws ImejiException
   */
  private void add(Invitation invitation) throws ImejiException {
    keyValueStoreBC.put(invitation.getId(), invitation);
  }

  /**
   * Remove an Invitation and its relation
   * 
   * @param invitation
   * @throws ImejiException
   */
  private void remove(Invitation invitation) throws ImejiException {
    keyValueStoreBC.delete(invitation.getId());
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

  /**
   * Retrieve all Invitations of an InvitationList
   * 
   * @param invitationList
   * @return
   */
  private List<Invitation> retrieveInvitations(InvitationList invitationList) {
    List<Invitation> invitations = new ArrayList<>();
    for (String invitationId : invitationList.getAllInvitationsId()) {
      try {
        invitations.add((Invitation) keyValueStoreBC.get(invitationId));
      } catch (ImejiException e) {
        LOGGER.error("Error retrieving invation: " + invitationId, e);
      }
    }
    return invitations;
  }

}
