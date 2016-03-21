package de.mpg.imeji.logic.collaboration.invitation;

import java.io.Serializable;
import java.util.List;

/**
 * An invitation sent by a user to another user for a Object with some roles. Invitation ids follow
 * the pattern: invitation:{inviteeEmail}:{objectUri}
 * 
 * @author bastiens
 *
 */
public class Invitation implements Serializable {
  private static final long serialVersionUID = 658949804870284864L;
  private final String id;
  private final String inviteeEmail;
  private final String objectUri;
  private final List<String> roles;
  private static final String INVITATION_PREFIX = "invitation";

  /**
   * Create a new Invitation
   * 
   * @param invitor
   * @param invitee
   * @param objectUri
   * @param roles
   */
  public Invitation(String inviteeEmail, String objectUri, List<String> roles) {
    this.inviteeEmail = inviteeEmail;
    this.objectUri = objectUri;
    this.roles = roles;
    id = INVITATION_PREFIX + ":" + inviteeEmail + ":" + objectUri;
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

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

}
