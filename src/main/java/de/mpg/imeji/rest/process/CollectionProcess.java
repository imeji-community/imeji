package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.soap.AddressingFeature.Responses;

import net.java.dev.webdav.jaxrs.ResponseStatus;
import de.mpg.imeji.logic.auth.exception.AuthenticationError;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.auth.exception.UnprocessableError;
import de.mpg.imeji.logic.controller.exceptions.AuthorizationException;
import de.mpg.imeji.logic.vo.Properties;
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
			resp.setObject(RestProcessUtils.buildBadRequestResponse(e.getLocalizedMessage()));
			resp.setStatus(Status.BAD_REQUEST);

		} catch (AuthenticationError e) {
				resp.setObject(RestProcessUtils.buildUnauthorizedResponse(e.getLocalizedMessage()));
				resp.setStatus(Status.UNAUTHORIZED);
		}
		catch (NotAllowedError e) { 
				resp.setObject(RestProcessUtils.buildNotAllowedResponse(e.getLocalizedMessage()));
				resp.setStatus(Status.FORBIDDEN);
		} catch (Exception e) {

		}
		return resp;

	}  
	
	public static JSONResponse createCollection(HttpServletRequest req) {
		JSONResponse resp = new JSONResponse();
		
		User u = BasicAuthentication.auth(req);
		
		if(u == null)
		{
			resp.setObject(RestProcessUtils.buildUnauthorizedResponse("Not logged in not allowed to create collection"));
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
				resp.setObject(RestProcessUtils.buildBadRequestResponse(e.getLocalizedMessage()));
				resp.setStatus(Status.BAD_REQUEST);
			}

		}  
		return resp;

	}
	
	public static JSONResponse releaseCollection(HttpServletRequest req, String id) throws Exception{
		JSONResponse resp = new JSONResponse();
		resp.setStatus(Status.NO_CONTENT);
		User u = BasicAuthentication.auth(req);
		CollectionService service = new CollectionService(); 
		
		try {
				service.release(id, u);
			} catch (NotFoundException e) {
				resp.setObject(RestProcessUtils.buildBadRequestResponse(e.getLocalizedMessage()));
				resp.setStatus(Status.BAD_REQUEST);

			} catch (AuthenticationError e) {
					resp.setObject(RestProcessUtils.buildUnauthorizedResponse(e.getLocalizedMessage()));
					resp.setStatus(Status.UNAUTHORIZED);
			}
			catch (NotAllowedError e) {
					resp.setObject(RestProcessUtils.buildNotAllowedResponse(e.getLocalizedMessage()));
					resp.setStatus(Status.FORBIDDEN);
			} catch(RuntimeException e){
				resp.setObject(RestProcessUtils.buildExceptionResponse(e.getLocalizedMessage()));
				resp.setStatus(Status.FORBIDDEN);
			}
			catch (UnprocessableError e){
				resp.setObject(RestProcessUtils.buildUnprocessableErrorResponse(e.getLocalizedMessage()));
				resp.setStatus(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode());
			}
			catch (Exception e) {
				e.printStackTrace();
			}	
		return resp;
	}



}
