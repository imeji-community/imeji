package de.mpg.imeji.testimpl.logic.businesscontroller;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.collaboration.invitation.Invitation;
import de.mpg.imeji.logic.collaboration.invitation.InvitationBusinessController;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController;
import de.mpg.imeji.logic.collaboration.share.ShareBusinessController.ShareRoles;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.registration.Registration;
import de.mpg.imeji.logic.registration.RegistrationBusinessController;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.util.ImejiFactory;
import de.mpg.imeji.test.logic.controller.ControllerTest;

public class RegistrationBusinessControllerTest extends ControllerTest {

  private RegistrationBusinessController registrationBC = new RegistrationBusinessController();
  private static final Logger LOGGER = Logger.getLogger(RegistrationBusinessControllerTest.class);


  /**
   * user@domain.org allowed for domain.org
   * 
   * @throws Exception
   */
  @Test
  public void registerAllowedEmail() throws Exception {
    Imeji.CONFIG.setRegistrationWhiteList("domain.org");
    User user = new User();
    user.setEmail("user@domain.org");
    user.setPerson(ImejiFactory.newPerson("family", "given", "org"));
    Registration registration = registrationBC.register(user);
    registrationBC.activate(registration);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    assertTrue(user.isActive());
    assertTrue(AuthUtil.isAllowedToCreateCollection(user));
  }

  /**
   * user@subdomain.domain.org allowed for domain.org
   * 
   * @throws Exception
   */
  @Test
  public void registerAllowedEmail2() throws Exception {
    Imeji.CONFIG.setRegistrationWhiteList("domain.org");
    User user = new User();
    user.setEmail("user@subdomain.domain.org");
    user.setPerson(ImejiFactory.newPerson("family", "given", "org"));
    Registration registration = registrationBC.register(user);
    registrationBC.activate(registration);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    assertTrue(user.isActive());
    assertTrue(AuthUtil.isAllowedToCreateCollection(user));
  }

  /**
   * user@domain.com not allowed for subdomain.domain.org,example.org,domain.com
   * 
   * @throws Exception
   */
  @Test
  public void registerAllowedEmail3() throws Exception {
    Imeji.CONFIG.setRegistrationWhiteList("subdomain.domain.org,example.org,domain.com");
    User user = new User();
    user.setEmail("user@domain.com");
    user.setPerson(ImejiFactory.newPerson("family", "given", "org"));
    Registration registration = registrationBC.register(user);
    registrationBC.activate(registration);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    assertTrue(user.isActive());
    assertTrue(AuthUtil.isAllowedToCreateCollection(user));
  }

  /**
   * user@example.org not allowed for domain.org
   * 
   * @throws Exception
   */
  @Test
  public void registerNotAllowedEmail() throws Exception {
    Imeji.CONFIG.setRegistrationWhiteList("domain.org");
    User user = new User();
    user.setEmail("user@example.org");
    user.setPerson(ImejiFactory.newPerson("family", "given", "org"));
    Registration registration = registrationBC.register(user);
    registrationBC.activate(registration);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    assertTrue(user.isActive());
    assertTrue(!AuthUtil.isAllowedToCreateCollection(user));
  }

  /**
   * user@domain.org not allowed for subdomain.domain.org
   * 
   * @throws Exception
   */
  @Test
  public void registerNotAllowedEmail2() throws Exception {
    Imeji.CONFIG.setRegistrationWhiteList("subdomain.domain.org");
    User user = new User();
    user.setEmail("user2@domain.org");
    user.setPerson(ImejiFactory.newPerson("family", "given", "org"));
    Registration registration = registrationBC.register(user);
    registrationBC.activate(registration);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    assertTrue(user.isActive());
    assertTrue(!AuthUtil.isAllowedToCreateCollection(user));
  }

  /**
   * user@domain.org not allowed for subdomain.domain.org,example.org,domain.com
   * 
   * @throws Exception
   */
  @Test
  public void registerNotAllowedEmail3() throws Exception {
    Imeji.CONFIG.setRegistrationWhiteList("subdomain.domain.org,example.org,domain.com");
    User user = new User();
    user.setEmail("user3@domain.org");
    user.setPerson(ImejiFactory.newPerson("family", "given", "org"));
    Registration registration = registrationBC.register(user);
    registrationBC.activate(registration);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    assertTrue(user.isActive());
    assertTrue(!AuthUtil.isAllowedToCreateCollection(user));
  }


  @Test
  public void registerAfterInvitation() throws Exception {
    User user = new User();
    user.setEmail("invited-user@example.org");
    user.setPerson(ImejiFactory.newPerson("test", "user", "orga"));
    // allow all users to register
    Imeji.CONFIG.setRegistrationWhiteList("");
    // create a collection
    createCollection();
    // invite the user to
    InvitationBusinessController invitationBusinessController = new InvitationBusinessController();
    invitationBusinessController.invite(new Invitation(user.getEmail(),
        collection.getId().toString(), ShareBusinessController.rolesAsList(ShareRoles.READ)));
    // Register
    Registration registration = registrationBC.register(user);
    Assert.assertNotNull(registrationBC.retrieveByToken(registration.getToken()));
    registrationBC.activate(registration);
    // check if the user exists
    Assert.assertNotNull(new UserController(Imeji.adminUser).retrieve(user.getEmail()));
  }

  @Test
  public void register() throws Exception {
    User user = new User();
    user.setEmail("register-user@example.org");
    user.setPerson(ImejiFactory.newPerson("test", "user", "orga"));
    // allow all users to register
    Imeji.CONFIG.setRegistrationWhiteList("");
    // Register
    Registration registration = registrationBC.register(user);
    Assert.assertNotNull(registrationBC.retrieveByToken(registration.getToken()));
    registrationBC.activate(registration);
    // check if the user exists
    Assert.assertNotNull(new UserController(Imeji.adminUser).retrieve(user.getEmail()));
  }

  @Test
  public void clearExpiredRegistration() throws Exception {
    try {
      User user = new User();
      user.setEmail("clear-expired-registration-user@example.org");
      user.setPerson(ImejiFactory.newPerson("test", "user", "orga"));
      // allow all users to register
      Imeji.CONFIG.setRegistrationWhiteList("");
      Imeji.CONFIG.setRegistrationTokenExpiry("0");
      // Register
      Registration registration = registrationBC.register(user);
      Assert.assertNotNull(registrationBC.retrieveByToken(registration.getToken()));
      registrationBC.deleteExpiredRegistration();
      try {
        registrationBC.retrieveByToken(registration.getToken());
        Assert.fail("The registration should be removed");
      } catch (NotFoundException e) {
        // OK
      }
    } finally {
      Imeji.CONFIG.setRegistrationTokenExpiry("1");
    }
  }

  @Test
  public void retrieveAllRegistrations() throws Exception {
    registrationBC.removeAll();
    User user1 = new User();
    user1.setEmail("retrieve-all-1@example.org");
    user1.setPerson(ImejiFactory.newPerson("test", "user", "orga"));
    User user2 = new User();
    user2.setEmail("retrieve-all-2@example.org");
    user2.setPerson(ImejiFactory.newPerson("test", "user", "orga"));
    registrationBC.register(user1);
    registrationBC.register(user2);
    Assert.assertEquals(registrationBC.retrieveAll().size(), 2);
  }

  /**
   * Test when the admin activate a user manually from the users interface
   * 
   * @throws Exception
   */
  @Test
  public void activateByEmail() throws Exception {
    User user = new User();
    user.setEmail("activate-inactive-user@example.org");
    user.setPerson(ImejiFactory.newPerson("test", "user", "orga"));
    // allow all users to register
    Imeji.CONFIG.setRegistrationWhiteList("");
    // Register
    registrationBC.register(user);
    registrationBC.activate(registrationBC.retrieveByEmail(user.getEmail()));
  }
}
