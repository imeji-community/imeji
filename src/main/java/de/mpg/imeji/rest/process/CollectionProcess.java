package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import de.mpg.imeji.logic.auth.Authentication;
import de.mpg.imeji.logic.auth.AuthenticationFactory;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.j2j.exceptions.NotFoundException;

import java.io.IOException;
import java.io.InputStream;

public class CollectionProcess {
    
	public static JSONResponse readCollection(HttpServletRequest req, String id) {
		JSONResponse resp = new JSONResponse();

		User u = getUser(req);

		CollectionImeji collection = null;
		CollectionService ccrud = new CollectionService();
		try {
			collection = ccrud.read(id, u);
			CollectionTO to = new CollectionTO();
			TransferObjectFactory.transferCollection(collection, to);

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
	
	public JSONResponse createCollection(HttpServletRequest req) {
		JSONResponse resp = new JSONResponse();

		User u = getUser(req);

		CollectionImeji collection = null;
		//CollectionImeji collection2 = new CollectionImeji();
		
		CollectionService ccrud = new CollectionService();
		try {
			CollectionTO to = buildTOFromJSON(req);
			CollectionTO to = new CollectionTO();
			RestProcessUtils.buildTOFromJSON(req, CollectionTO.class,
					"TODO, write json as string here");
			CollectionImeji vo = new CollectionImeji();
			ReverseTransferObjectFactory.transferCollection(to, vo);
			collection = ccrud.create(vo, u);

			TransferObjectFactory.transferCollection(collection, to);

			resp.setObject(to);
			resp.setStatus(Status.CREATED);
		} catch (NotAllowedError e) {
			//cannot authorize user
 			if (u == null) {
				resp.setObject(RestProcessUtils.buildUnauthorizedResponse());
				resp.setStatus(Status.UNAUTHORIZED);
			} else {
				resp.setObject(RestProcessUtils.buildNotAllowedResponse());
				resp.setStatus(Status.FORBIDDEN);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;

	}

	public User getUser(HttpServletRequest req) {

//		Authentication auth = AuthenticationFactory.factory("admin@imeji.org", "admin");
//		return auth.doLogin();
		Authentication auth = AuthenticationFactory.factory(req);
		return auth.doLogin();
	}

	public static CollectionTO buildTOFromJSON(HttpServletRequest req) {
		try {
			return buildTOFromJSON(req.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static CollectionTO buildTOFromJSON(InputStream is) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(ByteStreams.toByteArray(is), CollectionTO.class);


	}


}
