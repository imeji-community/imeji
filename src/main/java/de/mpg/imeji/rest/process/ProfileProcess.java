package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Response.Status;

import de.mpg.imeji.logic.auth.exception.AuthenticationError;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import de.mpg.j2j.exceptions.NotFoundException;

public class ProfileProcess {

	public static JSONResponse readProfile(HttpServletRequest req, String id) {
		JSONResponse resp;
		
		User u = BasicAuthentication.auth(req);

		ProfileService pcrud = new ProfileService();
		try {
			resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), pcrud.read(id,u));
		} catch (Exception e) {
			resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());

		}
		return resp;
	}

	public static JSONResponse deleteProfile(HttpServletRequest req, String id) {
		JSONResponse resp; 
		
		User u = BasicAuthentication.auth(req);
		
		if(u == null){
			Exception e = new AuthenticationError(CommonUtils.USER_MUST_BE_LOGGED_IN);
			resp = RestProcessUtils.localExceptionHandler(e, CommonUtils.USER_MUST_BE_LOGGED_IN);
		}
		else{
			ProfileService pcrud = new ProfileService();			
			try{				
				resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), pcrud.delete(id, u));
			}catch(Exception e) {
				
				resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
			}
		}
		return resp;

	}
}
