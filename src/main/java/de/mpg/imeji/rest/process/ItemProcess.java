package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.util.ObjectLoader;

public class ItemProcess{
	
	
	public static Response buildJSONResponse(HttpServletRequest req, String id){
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		Item item = ObjectLoader.loadItem(ObjectHelper.getURI(Item.class, id), null); 
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
