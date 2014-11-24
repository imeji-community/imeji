package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import de.mpg.imeji.logic.auth.Authentication;
import de.mpg.imeji.logic.auth.AuthenticationFactory;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.j2j.exceptions.NotFoundException;

public class ProfileProcess {

	public static JSONResponse readProfile(HttpServletRequest req, String id) {
		JSONResponse resp = new JSONResponse();
  
		Authentication auth = AuthenticationFactory.factory(req);
		User u = auth.doLogin();

		MetadataProfile vo = null;
		//CollectionImeji collection2 = new CollectionImeji();
		
		ProfileService pcrud = new ProfileService();
		try {
			vo = pcrud.read(id, u);
//			ProfileTO to = new ProfileTO();
//			TransferObjectFactory.transferCollection(vo, to);
			
			resp.setObject(vo);
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

}
