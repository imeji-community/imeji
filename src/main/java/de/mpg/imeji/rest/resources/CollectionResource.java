package de.mpg.imeji.rest.resources;

import java.io.InputStream;

import javax.inject.Singleton;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import de.mpg.imeji.rest.process.CollectionProcess;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.JSONResponse;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Singleton
@Path("/collections")
@Api(value = "rest/collections", description = "Operations on collections")
public class CollectionResource implements ImejiResource {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CollectionResource.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAll(@Context HttpServletRequest req) {
		return null;
	}

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Get collection by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readFromID(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		JSONResponse resp = CollectionProcess.readCollection(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	
/*
	@PUT
	@Path("/{id}/release")
	@ApiOperation(value = "Release collection by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response releaseFromID(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		JSONResponse resp = CollectionProcess.releaseCollection(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}
*/

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Create collection or new version of collection", notes = "The body parameter is the json of a collection. You can get an example by using the get collection mehtod.")
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest req, InputStream json) {
		JSONResponse resp = CollectionProcess.createCollection(req);
		return RestProcessUtils.buildJSONResponse(resp);
	}

	@DELETE
	@Path("/{id}")
	@ApiOperation(value = "(Not implemented)  Delete collection by id", notes = "Deletes also the profile and items of this collection")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		return null;
	}

	@Override
	public Response create(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

}
