package de.mpg.imeji.rest.process;

import static javax.ws.rs.core.Response.Status.OK;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import de.mpg.imeji.exceptions.AuthenticationError;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.JSONResponse;

public class ProfileProcess {

  public static JSONResponse readProfile(HttpServletRequest req, String id) {
    JSONResponse resp;

    User u = BasicAuthentication.auth(req);

    ProfileService pcrud = new ProfileService();
    try {
      resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), pcrud.read(id, u));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());

    }
    return resp;
  }

  public static JSONResponse deleteProfile(HttpServletRequest req, String id) {
    JSONResponse resp;

    User u = BasicAuthentication.auth(req);

    if (u == null) {
      Exception e = new AuthenticationError(CommonUtils.USER_MUST_BE_LOGGED_IN);
      resp = RestProcessUtils.localExceptionHandler(e, CommonUtils.USER_MUST_BE_LOGGED_IN);
    } else {
      ProfileService pcrud = new ProfileService();
      try {
        resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), pcrud.delete(id, u));
      } catch (Exception e) {

        resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
      }
    }
    return resp;

  }

  public static JSONResponse readAll(HttpServletRequest req, String q) {
    JSONResponse resp;

    User u = BasicAuthentication.auth(req);

    ProfileService ccrud = new ProfileService();
    try {
      resp = RestProcessUtils.buildResponse(OK.getStatusCode(), ccrud.readAll(u, q));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse releaseProfile(HttpServletRequest req, String id) {
    JSONResponse resp;
    User u = BasicAuthentication.auth(req);
    ProfileService service = new ProfileService();

    try {
      resp = RestProcessUtils.buildResponse(OK.getStatusCode(), service.release(id, u));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse withdrawProfile(HttpServletRequest req, String id,
      String discardComment) {
    JSONResponse resp;

    User u = BasicAuthentication.auth(req);
    ProfileService service = new ProfileService();

    try {
      resp =
          RestProcessUtils.buildResponse(OK.getStatusCode(),
              service.withdraw(id, u, discardComment));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }
}
