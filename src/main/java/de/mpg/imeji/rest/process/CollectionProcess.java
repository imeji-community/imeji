package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.java.dev.webdav.jaxrs.ResponseStatus;
import de.mpg.imeji.logic.auth.exception.AuthenticationError;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.auth.exception.UnprocessableError;
import de.mpg.imeji.logic.controller.exceptions.NotFoundError;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.j2j.exceptions.NotFoundException;

public class CollectionProcess {

	public static JSONResponse readCollection(HttpServletRequest req, String id) {
		JSONResponse resp;

		User u = BasicAuthentication.auth(req);

		CollectionTO to = null;
		CollectionService ccrud = new CollectionService();
		try {
			to = ccrud.read(id, u);
			resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), to);
		} catch (Exception e) {
			resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
		}
		return resp;

	}

	public static JSONResponse createCollection(HttpServletRequest req) {
		JSONResponse resp; 

		User u = BasicAuthentication.auth(req);
		
		if (u == null) {
			resp = RestProcessUtils.buildJSONAndExceptionResponse(Status.UNAUTHORIZED.getStatusCode(), CommonUtils.USER_MUST_BE_LOGGED_IN);
		} else {
			CollectionService service = new CollectionService();
			CollectionTO to = (CollectionTO) RestProcessUtils.buildTOFromJSON(
					req, CollectionTO.class);
			try {
				resp = RestProcessUtils.buildResponse(Status.CREATED.getStatusCode(), service.create(to, u));
			} catch (Exception e) {
				resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
			}

		}
		return resp;
	}
	
	public static JSONResponse releaseCollection(HttpServletRequest req,
			String id) throws Exception {
		JSONResponse resp;
		User u = BasicAuthentication.auth(req);
		CollectionService service = new CollectionService(); 
		
		try {
			resp= RestProcessUtils.buildResponse(Status.OK.getStatusCode(), service.release(id, u));
		} catch (Exception e) {
			resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
		}
		return resp;
	}

	public static JSONResponse withdrawCollection(HttpServletRequest req, String id, String discardComment) 
	throws 
	Exception {
		JSONResponse resp;

		User u = BasicAuthentication.auth(req);
		CollectionService service = new CollectionService();

		try {
				resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), service.withdraw(id, u, discardComment));
			} 
		catch (Exception e)	{
			resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
			}
		return resp;
	}

}
