package de.mpg.imeji.rest.process;

import static de.mpg.imeji.rest.process.CommonUtils.USER_MUST_BE_LOGGED_IN;
import static de.mpg.imeji.rest.to.ItemTO.SYNTAX.guessType;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.JSONResponse;

public class CollectionProcess {

  public static JSONResponse readCollection(HttpServletRequest req, String id) {
    JSONResponse resp;

    User u = BasicAuthentication.auth(req);

    CollectionService ccrud = new CollectionService();
    try {
      resp = RestProcessUtils.buildResponse(OK.getStatusCode(), ccrud.read(id, u));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;

  }

  /**
   * Read the items of a {@link CollectionImeji} according to the search query
   * 
   * @param req
   * @param id
   * @param q
   * @return
   */
  public static JSONResponse readCollectionItems(HttpServletRequest req, String id, String q,
      int offset, int size) {
    JSONResponse resp = null;

    User u = BasicAuthentication.auth(req);

    CollectionService ccrud = new CollectionService();
    try {
      switch (guessType(req.getParameter("syntax"))) {
        case DEFAULT:
          resp =
              RestProcessUtils.buildResponse(Status.OK.getStatusCode(),
                  ccrud.readDefaultItems(id, u, q, offset, size));
          break;
        case RAW:
          resp =
              RestProcessUtils.buildResponse(OK.getStatusCode(),
                  ccrud.readItems(id, u, q, offset, size));
          break;
      }
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse createCollection(HttpServletRequest req) {
    JSONResponse resp;

    User u = BasicAuthentication.auth(req);

    if (u == null) {
      resp =
          RestProcessUtils.buildJSONAndExceptionResponse(UNAUTHORIZED.getStatusCode(),
              USER_MUST_BE_LOGGED_IN);
    } else {
      CollectionService service = new CollectionService();
      try {
        CollectionTO to = (CollectionTO) RestProcessUtils.buildTOFromJSON(req, CollectionTO.class);
        resp = RestProcessUtils.buildResponse(CREATED.getStatusCode(), service.create(to, u));
      } catch (ImejiException e) {
        resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
      }

    }
    return resp;
  }

  public static JSONResponse updateCollection(HttpServletRequest req, String id) {
    JSONResponse resp;

    User u = BasicAuthentication.auth(req);

    if (u == null) {
      resp =
          RestProcessUtils.buildJSONAndExceptionResponse(UNAUTHORIZED.getStatusCode(),
              USER_MUST_BE_LOGGED_IN);
    } else {
      CollectionService service = new CollectionService();
      try {
        CollectionTO to = (CollectionTO) RestProcessUtils.buildTOFromJSON(req, CollectionTO.class);
        if (!id.equals(to.getId())) {
          throw new BadRequestException("Collection id is not equal in request URL and in json");
        }
        resp = RestProcessUtils.buildResponse(OK.getStatusCode(), service.update(to, u));
      } catch (ImejiException e) {
        resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
      }

    }
    return resp;
  }

  public static JSONResponse releaseCollection(HttpServletRequest req, String id) {
    JSONResponse resp;
    User u = BasicAuthentication.auth(req);
    CollectionService service = new CollectionService();

    try {
      resp = RestProcessUtils.buildResponse(OK.getStatusCode(), service.release(id, u));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }


  public static JSONResponse withdrawCollection(HttpServletRequest req, String id,
      String discardComment) throws Exception {
    JSONResponse resp;

    User u = BasicAuthentication.auth(req);
    CollectionService service = new CollectionService();

    try {
      resp =
          RestProcessUtils.buildResponse(OK.getStatusCode(),
              service.withdraw(id, u, discardComment));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse deleteCollection(HttpServletRequest req, String id) {
    JSONResponse resp;
    User u = BasicAuthentication.auth(req);
    CollectionService service = new CollectionService();

    try {
      resp = RestProcessUtils.buildResponse(NO_CONTENT.getStatusCode(), service.delete(id, u));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse readAllCollections(HttpServletRequest req, String q, int offset,
      int size) {
    JSONResponse resp;
    User u = BasicAuthentication.auth(req);

    CollectionService ccrud = new CollectionService();
    try {
      resp = RestProcessUtils.buildResponse(OK.getStatusCode(), ccrud.readAll(u, q, offset, size));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

}
