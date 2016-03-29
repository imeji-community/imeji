package de.mpg.imeji.testimpl.logic.businesscontroller;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.registration.RegistrationBusinessController;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.test.logic.controller.ControllerTest;
import de.mpg.j2j.helper.DateHelper;
import util.JenaUtil;

public class RegistratinBusinessControllerTest extends ControllerTest {

  private RegistrationBusinessController registrationBC = new RegistrationBusinessController();
  private static final Logger LOGGER = Logger.getLogger(RegistratinBusinessControllerTest.class);


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
    registrationBC.register(user);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    registrationBC.activate(user.getRegistrationToken());
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
    registrationBC.register(user);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    registrationBC.activate(user.getRegistrationToken());
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
    registrationBC.register(user);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    registrationBC.activate(user.getRegistrationToken());
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
    registrationBC.register(user);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    registrationBC.activate(user.getRegistrationToken());
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
    registrationBC.register(user);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    registrationBC.activate(user.getRegistrationToken());
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
    registrationBC.register(user);
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    registrationBC.activate(user.getRegistrationToken());
    user = new UserController(Imeji.adminUser).retrieve(user.getEmail());
    assertTrue(user.isActive());
    assertTrue(!AuthUtil.isAllowedToCreateCollection(user));
  }


  @Test
  public void createAndActivateInactiveUserTest() throws ImejiException {
    User user = new User();
    user.setEmail("inactive-activate@imeji.org");
    user.getPerson().setFamilyName(JenaUtil.TEST_USER_NAME);
    user.getPerson().setOrganizations(JenaUtil.testUser.getPerson().getOrganizations());
    UserController c = new UserController(user);
    try {
      // Create a new user with a new id but with the same email
      user = c.create(user, USER_TYPE.INACTIVE);
      assertTrue(!user.isActive());
    } catch (Exception e1) {
      Assert.fail("An error happened by cretaion an inactive User " + e1.getMessage());
    }

    try {
      user = registrationBC.activate(user.getRegistrationToken() + "RR");

    } catch (NotFoundException e1) {
      // Do Nothing this is OK
      LOGGER.info("OK, invalid registration token");
    } catch (Exception e2) {
      Assert.fail("An error happened by activating the user with false Registration Token"
          + e2.getMessage());
    }

    Calendar originalCreateDate = user.getCreated();
    try {
      Calendar now = DateHelper.getCurrentDate();
      // Tests assumes registration token is valid for not more than 20 days
      // ToDo: Change to read from Configuration Bean
      now.add(Calendar.DAY_OF_MONTH,
          Integer.parseInt(Imeji.CONFIG.getRegistrationTokenExpiry()) - 5);
      user.setCreated(now);
      user = c.update(user, c.getControllerUser());
      user = registrationBC.activate(user.getRegistrationToken());

    } catch (UnprocessableError e1) {
      LOGGER.info("OK, expired registration token");
    } catch (Exception e2) {
      Assert.fail("An error happened by activating the user with expired Registration Token"
          + e2.getMessage());
    }

    user.setCreated(originalCreateDate);
    user = c.update(user, c.getControllerUser());

    try {
      user = registrationBC.activate(user.getRegistrationToken());
      assertTrue(user.isActive());

    } catch (Exception e1) {
      Assert.fail("An error happened by activating the user " + e1.getMessage());
    }

    try {
      user = registrationBC.activate(user.getRegistrationToken());
      LOGGER.info("OK, double registration!");
      Assert.fail("An error happened by activating the user again!");
    } catch (Exception e) {
      // Do Nothing all is fine
    }
  }



  @Test
  public void createAndCleanInactiveUserTest() throws ImejiException {
    String email = "clean-inactive-user@imeji.org";
    UserController c = new UserController(Imeji.adminUser);
    Calendar now = DateHelper.getCurrentDate();
    for (int i = 1; i < 10; i++) {
      User user = new User();
      user.setEmail(email + i);
      user.getPerson().setFamilyName(JenaUtil.TEST_USER_NAME);
      user.getPerson().setOrganizations(JenaUtil.testUser.getPerson().getOrganizations());
      user = c.create(user, USER_TYPE.INACTIVE);
      assertTrue(!user.isActive());
      Calendar originalCreateDate = user.getCreated();
      if (i < 7) {
        now.add(Calendar.DAY_OF_MONTH,
            Integer.parseInt(Imeji.CONFIG.getRegistrationTokenExpiry()) - 5);
        user.setCreated(now);
        user = c.update(user, c.getControllerUser());
      }
    }

    int numCleaned = c.cleanInactiveUsers();
    assertTrue(numCleaned == 6);
  }

  @Test
  public void createInactiveUserTest() throws ImejiException {
    UserController c = new UserController(null);
    // Create a new user with a new id but with the same email
    User user = new User();
    LOGGER.info("User object has " + user.getId());
    user.setEmail("inactiveuser@imeji.org");
    user.getPerson().setFamilyName(JenaUtil.TEST_USER_NAME);
    user.getPerson().setOrganizations(JenaUtil.testUser.getPerson().getOrganizations());
    c.create(user, USER_TYPE.INACTIVE);
    assertTrue(!user.isActive());
  }

  @Test
  public void createActiveUserTestAsNoUser() {
    UserController c = new UserController(null);
    // Create a new user with a new id but with the same email
    User user = new User();
    user.setEmail("activeuser@imeji.org");
    user.getPerson().setFamilyName(JenaUtil.TEST_USER_NAME);
    user.getPerson().setOrganizations(JenaUtil.testUser.getPerson().getOrganizations());
    try {
      user = c.create(user, USER_TYPE.DEFAULT);
      Assert.fail("User should not be created in other state than inactive!");
    } catch (ImejiException e) {
      // Do Nothing this is fine
    }
  }
}
