package de.mpg.imeji.logic.collaboration.invitation;

import java.util.List;

import de.mpg.imeji.logic.vo.User;

/**
 * An invitation sent by a user to another user for a Object with some roles
 * 
 * @author bastiens
 *
 */
public class Invitation {
  private final User invitor;
  private final String inviteeEmail;
  private final String objectUri;
  private final List<String> roles;

  /**
   * Create a new Invitation
   * 
   * @param invitor
   * @param invitee
   * @param objectUri
   * @param roles
   */
  public Invitation(User invitor, String inviteeEmail, String objectUri, List<String> roles) {
    this.inviteeEmail = inviteeEmail;
    this.invitor = invitor;
    this.objectUri = objectUri;
    this.roles = roles;
  }

  /**
   * @return the invitor
   */
  public User getInvitor() {
    return invitor;
  }

  /**
   * @return the invitee
   */
  public String getInviteeEmail() {
    return inviteeEmail;
  }

  /**
   * @return the objectUri
   */
  public String getObjectUri() {
    return objectUri;
  }

  /**
   * @return the roles
   */
  public List<String> getRoles() {
    return roles;
  }

}
