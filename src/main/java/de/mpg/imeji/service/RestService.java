package de.mpg.imeji.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.spi.resource.Singleton;

import de.mpg.imeji.service.process.RestProcessGeneric;


@Singleton
@Path("/")
public class RestService implements Pathes {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestService.class);

	public RestService() {
		LOGGER.info("Initiating Servlet ImejiRestService");
		/*
		 * 
		 * */
		LOGGER.info("Initiating of Servlet ImejiRestService finished.");
	}
	
	@GET @Path(Pathes.PATH_HELLO_WORLD)
	@Produces(MediaType.TEXT_HTML)
	public Response getHelloWorld() {
		return RestProcessGeneric.hallo();
	}
	
//	@GET @Path(Pathes.PATH_COLLECTIONS)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response getCollections() {
//		return null;
//	}
//	
//	{ 
//	     {"uri":"uri1", "name":"name1"}, 
//	     {"uri":"uri2", "name":"name2"} 
//	}
//	
	
	 
		
	
	
}
