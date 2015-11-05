package de.mpg.imeji.logic.auth.authentication;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.Authentication;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.User;

/**
 * {@link Authentication} with API Key
 * 
 * @author bastiens
 *
 */
public class APIKeyAuthentication implements Authentication {
  private static final Logger logger = Logger.getLogger(APIKeyAuthentication.class);
  private String key;

  public APIKeyAuthentication(String key) {
    this.key = key;
  }

  @Override
  public User doLogin() {
    try {
      UserController controller = new UserController(Imeji.adminUser);
      return controller.retrieveByApiKey(key);
    } catch (ImejiException e) {
      logger.error("Invalid Key authorization: ", e);
    }
    return null;
  }

  @Override
  public String getUserLogin() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getUserPassword() {
    // TODO Auto-generated method stub
    return null;
  }

}
