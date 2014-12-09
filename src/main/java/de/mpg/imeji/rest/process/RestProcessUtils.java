package de.mpg.imeji.rest.process;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.mpg.imeji.rest.to.HTTPError;
import de.mpg.imeji.rest.to.JSONException;
import de.mpg.imeji.rest.to.JSONResponse;

import java.net.URI;

public class RestProcessUtils {
	/**
	 * Parse a json file and construct a new Object of type T
	 * 
	 * @param req
	 * @param type
	 * @param json
	 * @return
	 */
	public static <T> Object buildTOFromJSON(HttpServletRequest req, Class<T> type, String json) {
		ObjectReader reader = new ObjectMapper().reader().withType(type);
		try {
			return reader.readValue(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> Object buildTOFromJSON(HttpServletRequest req, Class<T> type) {
		ObjectReader reader = new ObjectMapper().reader().withType(type);
		try {
			return reader.readValue(req.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
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

			e.printStackTrace();
		}
		return Response.status(resp.getStatus()).entity(json)
				.type(MediaType.APPLICATION_JSON).build();
	}

	public static JSONException buildBadRequestResponse() {
		JSONException ex = new JSONException();
		HTTPError error = new HTTPError();
		error.setCode("1400");
		error.setTitle("Validation failed");
		error.setMessage("validation-failed-message");
		ex.setError(error);
		return ex;

	}

	public static Object buildUnauthorizedResponse() {
		JSONException ex = new JSONException();
		HTTPError error = new HTTPError();
		error.setCode("1401");
		error.setTitle("Not authenticated");
		error.setMessage("invalid-account-message");
		ex.setError(error);
		return ex;
	}

	public static Object buildNotAllowedResponse() {
		JSONException ex = new JSONException();
		HTTPError error = new HTTPError();
		error.setCode("1403");
		error.setTitle("Forbidden");
		error.setMessage("authorization-failed-message");
		ex.setError(error);
		return ex;
		
	}
	
	public static Object buildExceptionResponse(String e) {
		JSONException ex = new JSONException();
		HTTPError error = new HTTPError();
		error.setCode("1403");
		error.setTitle("Forbidden");
		error.setMessage("authorization-failed-message");
		error.setExceptionReport(e);
		ex.setError(error);
		return ex;
		
	}
	



}
