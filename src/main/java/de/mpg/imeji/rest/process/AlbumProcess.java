package de.mpg.imeji.rest.process;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.AlbumService;
import de.mpg.imeji.rest.to.AlbumTO;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.imeji.rest.to.PersonTO;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.rest.process.CommonUtils.USER_MUST_BE_LOGGED_IN;
import static de.mpg.imeji.rest.process.RestProcessUtils.*;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;


public class AlbumProcess {
	
	public static JSONResponse readAlbum(HttpServletRequest req, String id) {
		JSONResponse resp;

		User u = BasicAuthentication.auth(req);

		AlbumService ccrud = new AlbumService();
		try {
			resp = buildResponse(Status.OK.getStatusCode(), ccrud.read(id, u));
		} catch (Exception e) {
			resp = localExceptionHandler(e, e.getLocalizedMessage());
		}
		return resp;

	}

	public static JSONResponse createAlbum(HttpServletRequest req) {
		JSONResponse resp; 

		User u = BasicAuthentication.auth(req);
		
		if (u == null) {
			resp = buildJSONAndExceptionResponse(Status.UNAUTHORIZED.getStatusCode(), CommonUtils.USER_MUST_BE_LOGGED_IN);
		} else {
			AlbumService service = new AlbumService();
			try {
                AlbumTO to = (AlbumTO) buildTOFromJSON(req, AlbumTO.class);               
                resp = buildResponse(Status.CREATED.getStatusCode(), service.create(to, u));
			} catch (ImejiException e) {
				resp = localExceptionHandler(e, e.getLocalizedMessage());
			}

		}
		return resp;
	}
	
    public static JSONResponse updateAlbum(HttpServletRequest req, String id) {
		JSONResponse resp;

		User u = BasicAuthentication.auth(req);

		if (u == null) {
			resp = buildJSONAndExceptionResponse(UNAUTHORIZED.getStatusCode(), USER_MUST_BE_LOGGED_IN);
		} else {
			AlbumService service = new AlbumService();
            try {
                AlbumTO to = (AlbumTO) buildTOFromJSON(req, AlbumTO.class);
                if (!id.equals(to.getId())) {
                    throw new BadRequestException("Album id is not equal in request URL and in json");
                }       
                resp = buildResponse(OK.getStatusCode(), service.update(to, u));
			} catch (ImejiException e) {
				resp = localExceptionHandler(e, e.getLocalizedMessage());
			}
		}
		return resp;
	}
	
	public static JSONResponse deleteAlbum(HttpServletRequest req,
			String id) {
		JSONResponse resp;
		User u = BasicAuthentication.auth(req);
		AlbumService service = new AlbumService(); 
		
		try {
			resp= buildResponse(Status.NO_CONTENT.getStatusCode(), service.delete(id, u));
		} catch (Exception e) {
			resp = localExceptionHandler(e, e.getLocalizedMessage());
		}
		return resp;
	}
	
	public static JSONResponse withdrawAlbum(HttpServletRequest req, String id, String discardComment){
		JSONResponse resp;
        if ( isNullOrEmpty(discardComment) ) {
            return localExceptionHandler(new BadRequestException("Please give a comment"), null);
        }

		User u = BasicAuthentication.auth(req);
		AlbumService service = new AlbumService();

		try {
				resp = buildResponse(Status.OK.getStatusCode(), service.withdraw(id, u, discardComment));
			} 
		catch (Exception e)	{
			resp = localExceptionHandler(e, e.getLocalizedMessage());
			}
		return resp;
	}
	
	public static JSONResponse releaseAlbum(HttpServletRequest req,	String id)  {
		JSONResponse resp;
		User u = BasicAuthentication.auth(req);
		AlbumService service = new AlbumService(); 
		
		try {
			resp= buildResponse(Status.OK.getStatusCode(), service.release(id, u));
		} catch (Exception e) {
			resp = localExceptionHandler(e, e.getLocalizedMessage());
		}
		return resp;
	}
	
	public static JSONResponse addItem(HttpServletRequest req,	String id)	{
		JSONResponse resp;
		User u = BasicAuthentication.auth(req);
		AlbumService service = new AlbumService();

        try {
            List<String> itemIds = (List) buildTOFromJSON(req, List.class);
            resp= buildResponse(Status.OK.getStatusCode(), service.addItem(id, u, itemIds));
		} catch (Exception e) {
			resp = localExceptionHandler(e, e.getLocalizedMessage());
		}
		return resp;
	}
		


}
