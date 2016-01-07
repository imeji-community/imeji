package de.mpg.imeji.rest.process;

import javax.ws.rs.core.Response.Status;

import org.jose4j.lang.JoseException;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.auth.authentication.APIKeyAuthentication;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.imeji.rest.to.UserTO;

public class AdminProcess {

  /**
   * Login to imeji. If success, return the {@link UserTO}
   * 
   * @param req
   * @return
   */
  public static JSONResponse login(String authorizationHeader) {
    try {
      User userVO = BasicAuthentication.auth(authorizationHeader);
      if (userVO.getApiKey() == null) {
        updateUserKey(userVO, generateNewKey(userVO));
      }
      UserTO userTO = new UserTO();
      TransferObjectFactory.transferUser(userVO, userTO);
      return RestProcessUtils.buildResponse(Status.OK.getStatusCode(), userTO);
    } catch (ImejiException | JoseException e) {
      return RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
  }

  /**
   * Invalidate the current key
   * 
   * @param authorizationHeader
   * @return
   */
  public static JSONResponse logout(String authorizationHeader) {
    try {
      User userVO = BasicAuthentication.auth(authorizationHeader);
      updateUserKey(userVO, generateNewKey(userVO));
      return RestProcessUtils.buildResponse(Status.OK.getStatusCode(), null);
    } catch (ImejiException | JoseException e) {
      return RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
  }

  /**
   * Update the key of a user in the database
   * 
   * @param user
   * @param key
   * @throws ImejiException
   */
  private static void updateUserKey(User user, String key) throws ImejiException {
    user.setApiKey(key);
    new UserController(user).update(user, user);
  }

  /**
   * Generate a new Key for the {@link User}. Key is saved in the database
   * 
   * @param user
   * @return
   * @throws JoseException
   * @throws ImejiException
   */
  private static String generateNewKey(User user) throws JoseException, ImejiException {
    if (user != null) {
      return APIKeyAuthentication.generateKey(user.getId(), Integer.MAX_VALUE);
    }
    return null;
  }

}
