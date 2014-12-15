package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import de.mpg.imeji.logic.auth.Authentication;
import de.mpg.imeji.logic.auth.AuthenticationFactory;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.j2j.exceptions.NotFoundException;

public class CollectionProcess {
    
	public static JSONResponse readCollection(HttpServletRequest req, String id) {
		JSONResponse resp = new JSONResponse();
		
		User u = BasicAuthentication.auth(req);

		CollectionTO to = null;
		CollectionService ccrud = new CollectionService();
		try {
			to = ccrud.read(id, u);
			resp.setObject(to);
			resp.setStatus(Status.OK);
		} catch (NotFoundException e) {
			resp.setObject(RestProcessUtils.buildBadRequestResponse());
			resp.setStatus(Status.BAD_REQUEST);

		} catch (NotAllowedError e) {
			if (u == null) {
				resp.setObject(RestProcessUtils.buildUnauthorizedResponse());
				resp.setStatus(Status.UNAUTHORIZED);
			} else {
				resp.setObject(RestProcessUtils.buildNotAllowedResponse());
				resp.setStatus(Status.FORBIDDEN);

			}
		} catch (Exception e) {

		}
		return resp;

	}  
	
	public static JSONResponse createCollection(HttpServletRequest req) {
		JSONResponse resp = new JSONResponse();
		
		User u = BasicAuthentication.auth(req);
		
		if(u == null)
		{
			resp.setObject(RestProcessUtils.buildUnauthorizedResponse());
			resp.setStatus(Status.UNAUTHORIZED);
		}
		else
		{
			CollectionService service = new CollectionService();
			CollectionTO to = (CollectionTO)RestProcessUtils.buildTOFromJSON(req, CollectionTO.class);
			try {
				resp.setObject(service.create(to, u));
				resp.setStatus(Status.CREATED);
			} catch (Exception e) {
				resp.setObject(RestProcessUtils.buildBadRequestResponse());
				resp.setStatus(Status.BAD_REQUEST);
			}

		}  
		return resp;

	}
	
	/*public static JSONResponse releaseCollection(HttpServletRequest req, String id){
		JSONResponse resp = new JSONResponse();
		resp.setStatus(Status.OK);
		User u = getUser(req);
		CollectionService service = new CollectionService();
		try {
				service.release(id, u);
			} catch (NotFoundException e) {
				resp.setObject(RestProcessUtils.buildBadRequestResponse());
				resp.setStatus(Status.BAD_REQUEST);

			} catch (NotAllowedError e) {
				if (u == null) {
					resp.setObject(RestProcessUtils.buildUnauthorizedResponse());
					resp.setStatus(Status.UNAUTHORIZED);
				} else {
					resp.setObject(RestProcessUtils.buildNotAllowedResponse());
					resp.setStatus(Status.FORBIDDEN);

				}
			} catch(RuntimeException e){
				resp.setObject(RestProcessUtils.buildExceptionResponse(e.getLocalizedMessage()));
				resp.setStatus(Status.FORBIDDEN);
			}
			catch (Exception e) {
				e.printStackTrace();
			}	
		return resp;
	}*/



}
