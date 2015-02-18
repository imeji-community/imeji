package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import de.mpg.imeji.exceptions.ImejiException;
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
			resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), ccrud.read(id, u));
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
			} catch (ImejiException e) {
				resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
			}

		}
		return resp;
	}
	
	public static JSONResponse releaseCollection(HttpServletRequest req,
			String id)  {
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

	public static JSONResponse deleteCollection(HttpServletRequest req,
			String id) {
		JSONResponse resp;
		User u = BasicAuthentication.auth(req);
		CollectionService service = new CollectionService(); 
		
		try {
			resp= RestProcessUtils.buildResponse(Status.NO_CONTENT.getStatusCode(), service.delete(id, u));
		} catch (Exception e) {
			resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
		}
		return resp;
	}

}
