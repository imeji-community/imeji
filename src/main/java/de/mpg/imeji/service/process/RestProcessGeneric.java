package de.mpg.imeji.service.process;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestProcessGeneric {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RestProcessGeneric.class);
	
	
	public static Response hallo() {
		
		return Response.status(Status.OK).entity("Hello World!!!").type(MediaType.TEXT_HTML).build();
		
	}

}
