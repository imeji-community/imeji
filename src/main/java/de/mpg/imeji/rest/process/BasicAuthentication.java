package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;

import de.mpg.imeji.exceptions.AuthenticationError;
import de.mpg.imeji.logic.auth.Authentication;
import de.mpg.imeji.logic.auth.AuthenticationFactory;
import de.mpg.imeji.logic.vo.User;

public class BasicAuthentication {

  public static User auth(HttpServletRequest req) throws AuthenticationError {
    Authentication auth = AuthenticationFactory.factory(req);
    User u = auth.doLogin();
    return u;
  }
}
