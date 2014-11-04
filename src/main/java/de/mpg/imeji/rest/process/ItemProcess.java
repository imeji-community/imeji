package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import de.mpg.imeji.logic.auth.AuthenticationFactory;
import de.mpg.imeji.logic.auth.authentication.SimpleAuthentication;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.crud.ItemCRUD;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.j2j.exceptions.NotFoundException;

public class ItemProcess{
	
	public static JSONResponse readItem(HttpServletRequest req, String id){
		JSONResponse resp = new JSONResponse();
		String login = req.getParameter("username");
		String pwd = req.getParameter("password");	
		      
		SimpleAuthentication auth = null;
		User u = null;
		if(login != null && pwd != null)
		auth =  (SimpleAuthentication) AuthenticationFactory.factory(login, pwd);
		if(auth != null)
			u = ((de.mpg.imeji.logic.auth.Authentication) auth).doLogin();   
		
		Item item = null;
		
		ItemCRUD icrud = new ItemCRUD();
		try {
			item = icrud.read(id, u);
			resp.setObject(item);
			resp.setStatus(Status.OK);
		} catch (NotFoundException e) {
			resp.setObject(RestProcessUtils.buildBadRequestResponse());
			resp.setStatus(Status.BAD_REQUEST);	
			
		}catch(NotAllowedError e){			
			if(auth == null)
			{
				resp.setObject(RestProcessUtils.buildUnauthorizedResponse());
				resp.setStatus(Status.UNAUTHORIZED);	
			}else{
				resp.setObject(RestProcessUtils.buildNotAllowedResponse());
				resp.setStatus(Status.FORBIDDEN);
				
			}
		}catch(Exception e){

		}
		return resp;


	}

	
	
	


}
