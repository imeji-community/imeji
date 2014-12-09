package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Response.Status;

import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import de.mpg.j2j.exceptions.NotFoundException;

public class ProfileProcess {

	public static JSONResponse readProfile(HttpServletRequest req, String id) {
		JSONResponse resp = new JSONResponse();
		
		User u = BasicAuthentication.auth(req);

		ProfileService pcrud = new ProfileService();
		try {
			MetadataProfileTO to = new MetadataProfileTO();
			to = pcrud.read(id,u);
			
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

	public static JSONResponse deleteProfile(HttpServletRequest req, String id) {
		JSONResponse resp = new JSONResponse();
		
		User u = BasicAuthentication.auth(req);
		
		if(u == null){
			resp.setObject(RestProcessUtils.buildUnauthorizedResponse());
			resp.setStatus(Status.UNAUTHORIZED);			
		}
		else{
			ProfileService pcrud = new ProfileService();			
			try{				
				pcrud.delete(id, u);
				resp.setStatus(Status.OK);
			}catch(NotFoundException e) {
				resp.setObject(RestProcessUtils.buildBadRequestResponse());
				resp.setStatus(Status.BAD_REQUEST);
	
			} catch (NotAllowedError e) {	
					resp.setObject(RestProcessUtils.buildNotAllowedResponse());
					resp.setStatus(Status.FORBIDDEN);
			} catch (NotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		}
		return resp;

	}
}
