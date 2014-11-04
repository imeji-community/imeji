package de.mpg.imeji.rest.process;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.mpg.imeji.logic.vo.Item;

public class RestProcessUtils {
	
	public static Response buildJSONResponse(Item item){
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	String json = "";
		try {
			json = ow.writeValueAsString(item);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(json).type(MediaType.APPLICATION_JSON).build();

	}

}
