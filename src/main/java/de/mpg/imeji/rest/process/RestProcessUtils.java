package de.mpg.imeji.rest.process;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.java.dev.webdav.jaxrs.ResponseStatus;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import de.mpg.imeji.exceptions.AuthenticationError;
import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.NotAllowedError;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.rest.to.HTTPError;
import de.mpg.imeji.rest.to.JSONException;
import de.mpg.imeji.rest.to.JSONResponse;

public class RestProcessUtils {
	
	private static Logger logger = Logger.getLogger(RestProcessUtils.class);
	/**
	 * Parse a json file and construct a new Object of type T
	 * 
	 * @param json
	 * @param type
	 * @return 
	 */
	public static <T> Object buildTOFromJSON(String json, Class<T> type) throws JsonProcessingException, UnrecognizedPropertyException{
		ObjectReader reader = new ObjectMapper().reader().withType(type);
		try {
			return reader.readValue(json);
		} catch (IllegalArgumentException e) {
			return e.getMessage();		
		}catch(UnrecognizedPropertyException e2){
			return e2.getMessage();
		}catch(JsonProcessingException e3){
			return e3.getMessage();
		}catch(IOException e4){
			return e4.getMessage();
		}
	}

	public static <T> Object buildTOFromJSON(HttpServletRequest req, Class<T> type) {
		ObjectReader reader = new ObjectMapper().reader().withType(type);
		try {
			return reader.readValue(req.getInputStream());
		} catch (IOException e) {
			logger.error("Could not build TO from JSON", e);
		}
		return null;
	}

	public static String buildJSONFromObject(Object obj) {
		ObjectWriter ow = new ObjectMapper().writer()
				.with(SerializationFeature.INDENT_OUTPUT);
		try {
			return ow.writeValueAsString(obj);
		} catch (IOException e) {
			logger.error("Could not build JSON from Object", e);

		}
		return null;
	}


	public static Response buildJSONResponse(JSONResponse resp) {
		ObjectWriter ow = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		String json = "";
		try {
			json = ow.writeValueAsString(resp.getObject());
		} catch (JsonProcessingException e) {

			logger.error("Have a JSON Processing Exception during building JSON Response", e);

		}
		return Response.status(resp.getStatus()).entity(json)
				.type(MediaType.APPLICATION_JSON).build();
	}

	public static Object buildExceptionResponse(int errorCode, String e) {
		JSONException ex = new JSONException();
		HTTPError error = new HTTPError();
		String errorCodeLocal = "1"+ errorCode;
		error.setCode(errorCodeLocal);
		String errorTitleLocal = "";
		Status localStatus = Status.fromStatusCode(errorCode);
		if (localStatus != null) {
			errorTitleLocal = localStatus.getReasonPhrase();
		}
		else
		{
			if (errorCode == ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode()) {
				errorTitleLocal = ResponseStatus.UNPROCESSABLE_ENTITY.getReasonPhrase();
			}
			else
			{
				errorTitleLocal = Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
			}
		}
		
		error.setExceptionReport(e);
		error.setCode(errorCodeLocal);
		error.setTitle(errorTitleLocal);
		error.setMessage(errorCodeLocal+"-message");
		ex.setError(error);
		return ex;
	}
	

	/**
	 * This method builds exception response. Based on the error Code, local title and message (which can be localized through the Language Bundles) are built
	 *  
	 * @param errorCode
	 * @param e
	 * @return
	 */
	public static JSONResponse buildJSONAndExceptionResponse(int errorCode, String e) {
		JSONResponse resp = new JSONResponse();
		resp.setStatus(errorCode);
		resp.setObject(buildExceptionResponse(errorCode, e));
		return resp;
	}

	/**
	 * This method builds the response for successfully created, updated, deleted, released etc. object.
	 * It is a convenience method to save 3 lines of code every time the HTTP Response needs to be built after success 
	 * @param statusCode
	 * @param responseObject
	 * @return
	 */
	public static JSONResponse buildResponse(int statusCode, Object responseObject) {
		JSONResponse resp = new JSONResponse();
		resp.setStatus(statusCode);
		resp.setObject(responseObject);
		return resp;
	}
	
	/**
	 * This method checks the exception type and returns appropriate JSON Response with properly set-up HTTP Code.
	 * 
	 * 
	 * @param eX
	 * @param message
	 * @return
	 */
	public static JSONResponse localExceptionHandler(Exception eX, String message) {
		String localMessage = message;
		if (message=="" || message == null) {
			message = eX.getLocalizedMessage();
		}
				
		JSONResponse resp;
				
		if (eX instanceof AuthenticationError)  {
			resp = RestProcessUtils.buildJSONAndExceptionResponse(Status.UNAUTHORIZED.getStatusCode(), localMessage);
		}
		else if (eX instanceof NotAllowedError) {
			resp = RestProcessUtils.buildJSONAndExceptionResponse(Status.FORBIDDEN.getStatusCode(), localMessage);
			
		}
		else if (eX instanceof NotFoundException || eX instanceof NotFoundException) {
			resp = RestProcessUtils.buildJSONAndExceptionResponse(Status.NOT_FOUND.getStatusCode(),localMessage);

		}
		else if (eX instanceof UnprocessableError) {
			resp = RestProcessUtils.buildJSONAndExceptionResponse(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(),localMessage);
		}
		else if (eX instanceof InternalServerErrorException) {
			resp = RestProcessUtils.buildJSONAndExceptionResponse(Status.INTERNAL_SERVER_ERROR.getStatusCode(), localMessage);
		}
		else if (eX instanceof BadRequestException) {
			resp = RestProcessUtils.buildJSONAndExceptionResponse(Status.BAD_REQUEST.getStatusCode(), localMessage);
		}
		else if (eX instanceof ClassCastException) {
			resp = RestProcessUtils.buildJSONAndExceptionResponse(Status.BAD_REQUEST.getStatusCode(), localMessage);
		}
		else {
			resp = RestProcessUtils.buildJSONAndExceptionResponse(Status.INTERNAL_SERVER_ERROR.getStatusCode(), localMessage);
		}
		
		return resp;
		
	}
}
