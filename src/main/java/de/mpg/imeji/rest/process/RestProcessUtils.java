package de.mpg.imeji.rest.process;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import de.mpg.imeji.rest.to.HTTPError;
import de.mpg.imeji.rest.to.JSONException;
import de.mpg.imeji.rest.to.JSONResponse;

public class RestProcessUtils {
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
			e.printStackTrace();
		}
		return null;
	}

	public static String buildJSONFromObject(Object obj) {
		ObjectWriter ow = new ObjectMapper().writer()
				.with(SerializationFeature.INDENT_OUTPUT)
				//.without(SerializationFeature.WRITE_NULL_MAP_VALUES)
				;
		try {
			return ow.writeValueAsString(obj);
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

	public static JSONException buildBadRequestResponse(String e) {
		JSONException ex = new JSONException();
		HTTPError error = new HTTPError();
		error.setCode("1400");
		error.setTitle("Validation failed");
		error.setMessage("validation-failed-message");
		error.setExceptionReport(e);
		ex.setError(error);
		return ex;

	}

	public static Object buildUnauthorizedResponse(String e) {
		JSONException ex = new JSONException();
		HTTPError error = new HTTPError();
		error.setCode("1401");
		error.setTitle("Not authenticated");
		error.setMessage("invalid-account-message");
		error.setExceptionReport(e);
		ex.setError(error);
		return ex;
	}

	public static Object buildNotAllowedResponse(String e) {
		JSONException ex = new JSONException();
		HTTPError error = new HTTPError();
		error.setCode("1403");
		error.setTitle("Forbidden");
		error.setMessage("authorization-failed-message");
		error.setExceptionReport(e);
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
