package de.mpg.imeji.testimpl.logic.auth;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.imeji.exceptions.AuthenticationError;
import de.mpg.imeji.logic.auth.authentication.Authentication;
import de.mpg.imeji.logic.auth.authentication.impl.DefaultAuthentication;
import de.mpg.imeji.logic.vo.User;
import util.JenaUtil;

/**
 * Test the simple {@link Authentication}
 * 
 * @author saquet
 *
 */
public class SimpleAuthenticationTest {

  @BeforeClass
  public static void setup() {
    JenaUtil.initJena();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    JenaUtil.closeJena();
  }

  @Test
  public void testLoginWrongPassword() {
    Authentication simpAuth =
        new DefaultAuthentication(JenaUtil.TEST_USER_EMAIL, JenaUtil.TEST_USER_PWD + "a");
    User user = null;
    try {
      user = simpAuth.doLogin();
    } catch (AuthenticationError e) {
      user = null;
      
    }
    Assert.assertNull(user);
  }

  @Test
  public void testUserNotExist() {
    Authentication simpAuth =
        new DefaultAuthentication("abdc" + JenaUtil.TEST_USER_EMAIL, JenaUtil.TEST_USER_PWD);
    User user = null;
    try {
      user = simpAuth.doLogin();
    } catch (AuthenticationError e) {
      
      user = null;
    }
    Assert.assertNull(user);
  }

  @Test
  public void testDoLogin() {
    // test if login if working for test user
    Authentication simpAuth =
        new DefaultAuthentication(JenaUtil.TEST_USER_EMAIL, JenaUtil.TEST_USER_PWD);
    User user = null;
    try {
      user = simpAuth.doLogin();
    } catch (AuthenticationError e) {
      user = null;
    }
    Assert.assertNotNull(user);
  }
}
