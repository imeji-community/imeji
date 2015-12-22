package de.mpg.imeji.rest.process;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.rest.process.CommonUtils.USER_MUST_BE_LOGGED_IN;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONAndExceptionResponse;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildResponse;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildTOFromJSON;
import static de.mpg.imeji.rest.process.RestProcessUtils.localExceptionHandler;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.AlbumService;
import de.mpg.imeji.rest.to.AlbumTO;
import de.mpg.imeji.rest.to.JSONResponse;


public class AlbumProcess {

  public static JSONResponse readAlbum(HttpServletRequest req, String id) {
    JSONResponse resp;
    AlbumService ccrud = new AlbumService();
    try {
      User u = BasicAuthentication.auth(req);
      resp = buildResponse(Status.OK.getStatusCode(), ccrud.read(id, u));
    } catch (Exception e) {
      resp = localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;

  }

  public static JSONResponse readAllAlbums(HttpServletRequest req, String q, int offset, int size) {
    JSONResponse resp;
    AlbumService as = new AlbumService();
    try {
      User u = BasicAuthentication.auth(req);
      resp = RestProcessUtils.buildResponse(OK.getStatusCode(), as.readAll(u, q, offset, size));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse readAlbumItems(HttpServletRequest req, String id, String q,
      int offset, int size) {
    JSONResponse resp;

    AlbumService ccrud = new AlbumService();
    try {
      User u = BasicAuthentication.auth(req);
      resp =
          RestProcessUtils.buildResponse(OK.getStatusCode(),
              ccrud.readItems(id, u, q, offset, size));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }


  public static JSONResponse createAlbum(HttpServletRequest req) {
    JSONResponse resp;

    AlbumService service = new AlbumService();
      try {
        User u = BasicAuthentication.auth(req);
        AlbumTO to = (AlbumTO) buildTOFromJSON(req, AlbumTO.class);
        resp = buildResponse(Status.CREATED.getStatusCode(), service.create(to, u));
      } catch (ImejiException e) {
        resp = localExceptionHandler(e, e.getLocalizedMessage());
      }
    return resp;
  }

  public static JSONResponse updateAlbum(HttpServletRequest req, String id) {
    JSONResponse resp;

    AlbumService service = new AlbumService();
      try {
        User u = BasicAuthentication.auth(req);
        AlbumTO to = (AlbumTO) buildTOFromJSON(req, AlbumTO.class);
        if (!id.equals(to.getId())) {
          throw new BadRequestException("Album id is not equal in request URL and in json");
        }
        resp = buildResponse(OK.getStatusCode(), service.update(to, u));
      } catch (ImejiException e) {
        resp = localExceptionHandler(e, e.getLocalizedMessage());
      }
    return resp;
  }

  public static JSONResponse deleteAlbum(HttpServletRequest req, String id) {
    JSONResponse resp;

    AlbumService service = new AlbumService();
    try {
      User u = BasicAuthentication.auth(req);
      resp = buildResponse(Status.NO_CONTENT.getStatusCode(), service.delete(id, u));
    } catch (Exception e) {
      resp = localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse withdrawAlbum(HttpServletRequest req, String id, String discardComment) {
    JSONResponse resp;
    if (isNullOrEmpty(discardComment)) {
      return localExceptionHandler(new BadRequestException("Please give a comment"), null);
    }

    AlbumService service = new AlbumService();

    try {
      User u = BasicAuthentication.auth(req);
      resp = buildResponse(Status.OK.getStatusCode(), service.withdraw(id, u, discardComment));
    } catch (Exception e) {
      resp = localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse releaseAlbum(HttpServletRequest req, String id) {
    JSONResponse resp;

    AlbumService service = new AlbumService();

    try {
      User u = BasicAuthentication.auth(req);
      resp = buildResponse(Status.OK.getStatusCode(), service.release(id, u));
    } catch (Exception e) {
      resp = localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse addItems(HttpServletRequest req, String id) {
    JSONResponse resp;
    AlbumService service = new AlbumService();

    try {
      User u = BasicAuthentication.auth(req);
      List<String> itemIds = (List) buildTOFromJSON(req, List.class);
      resp = buildResponse(Status.OK.getStatusCode(), service.addItems(id, u, itemIds));
    } catch (Exception e) {
      resp = localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse removeItems(HttpServletRequest req, String id, boolean removeAllItems) {
    JSONResponse resp;
    AlbumService service = new AlbumService();
    List<String> itemIds = null;
    try {
      User u = BasicAuthentication.auth(req);

      if (!removeAllItems)
        itemIds = (List) buildTOFromJSON(req, List.class);
      resp =
          buildResponse(Status.NO_CONTENT.getStatusCode(),
              service.removeItems(id, u, itemIds, removeAllItems));
    } catch (Exception e) {
      resp = localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

}
