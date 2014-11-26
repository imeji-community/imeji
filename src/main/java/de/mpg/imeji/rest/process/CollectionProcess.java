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

		Authentication auth = AuthenticationFactory.factory(req);
		User u = auth.doLogin();

		CollectionImeji collection = null;
		//CollectionImeji collection2 = new CollectionImeji();
		
		CollectionService ccrud = new CollectionService();
		try { 
			collection = ccrud.read(id, u);
			CollectionTO to = new CollectionTO();
			TransferObjectFactory.transferCollection(collection, to);
			//ReverseTransferObjectFactory.transferCollection(to, collection2);
			
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

		Authentication auth = AuthenticationFactory.factory(req);
		User u = auth.doLogin();

		CollectionImeji collection = null;
		//CollectionImeji collection2 = new CollectionImeji();
		
		CollectionService ccrud = new CollectionService();
		try {
			CollectionTO to = new CollectionTO();
			RestProcessUtils.buildTOFromJSON(req, to);
			CollectionImeji vo = new CollectionImeji();
			ReverseTransferObjectFactory.transferCollection(to, vo);
			
			
			
			
			collection = ccrud.create(vo, u);

			TransferObjectFactory.transferCollection(collection, to);
			//ReverseTransferObjectFactory.transferCollection(to, collection2);
			
			resp.setObject(to);
			resp.setStatus(Status.OK);
		} catch (Exception e) {

		}
		return resp;

	}
	

}
