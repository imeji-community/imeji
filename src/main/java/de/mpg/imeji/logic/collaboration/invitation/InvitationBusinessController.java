package de.mpg.imeji.logic.collaboration.invitation;

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
 * Business Controller for invitation of Users
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
   */
  public List<Invitation> retrieveRelatedInvitations(String objectUri) throws ImejiException {
    InvitationList invitationList =
        (InvitationList) keyValueStoreBC.get(new InvitationList(objectUri).getId());
    return retrieveInvitations(invitationList);
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
    InvitationList invitationList =
        (InvitationList) keyValueStoreBC.get(new InvitationList(user.getEmail()).getId());
    for (Invitation invitation : retrieveInvitations(invitationList)) {
      shareBC.shareToUser(Imeji.adminUser, user, invitation.getObjectUri(), invitation.getRoles());
      cancel(invitation.getId());
    }
    return user;
  }

  /**
   * Get all pending invitations TODO
   * 
   * @return
   */
  public List<Invitation> retrieveAll() {
    return null;
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
    addObjectToInvitationRelation(invitation);
    addEmailToInvitationRelation(invitation);
  }

  /**
   * Remove an Invitation and its relation
   * 
   * @param invitation
   * @throws ImejiException
   */
  private void remove(Invitation invitation) throws ImejiException {
    removeEmailToInvitationRelation(invitation);
    removeObjectToInvitationRelation(invitation);
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

  /**
   * Add the relation Object -> Invitation to the store.
   * 
   * @param invitation
   * @throws ImejiException
   */
  private void addObjectToInvitationRelation(Invitation invitation) throws ImejiException {
    addRelationToInvitation(invitation, invitation.getObjectUri());
  }

  /**
   * Add the relation email -> Invitation to the store
   * 
   * @param invitation
   * @throws ImejiException
   */
  private void addEmailToInvitationRelation(Invitation invitation) throws ImejiException {
    addRelationToInvitation(invitation, invitation.getInviteeEmail());
  }

  /**
   * Add relation key -> Invitation. If the relation exists, add the invitation to this relation
   * 
   * @param invitation
   * @param relatedId
   * @throws ImejiException
   */
  private void addRelationToInvitation(Invitation invitation, String key) throws ImejiException {
    InvitationList invitationList = new InvitationList(key);
    try {
      invitationList = (InvitationList) keyValueStoreBC.get(invitationList.getId());
      invitationList.add(invitation);
    } catch (NotFoundException e) {
      // Doesn't exist, ok, will be then created
    }
    keyValueStoreBC.put(invitationList.getId(), invitationList);
  }

  /**
   * Remove the relation Object -> Invitation to the store.
   * 
   * @param invitation
   * @throws ImejiException
   */
  private void removeObjectToInvitationRelation(Invitation invitation) throws ImejiException {
    removeRelationToInvitation(invitation, invitation.getObjectUri());
  }

  /**
   * Remove the relation email -> Invitation to the store
   * 
   * @param invitation
   * @throws ImejiException
   */
  private void removeEmailToInvitationRelation(Invitation invitation) throws ImejiException {
    removeRelationToInvitation(invitation, invitation.getInviteeEmail());
  }

  /**
   * Remove the relation to an invitation
   * 
   * @param invitation
   * @param key
   * @throws ImejiException
   */
  private void removeRelationToInvitation(Invitation invitation, String key) throws ImejiException {
    InvitationList invitationList = new InvitationList(key);
    invitationList = (InvitationList) keyValueStoreBC.get(invitationList.getId());
    invitationList.remove(invitation);
    keyValueStoreBC.put(invitationList.getId(), invitationList);
  }

}
