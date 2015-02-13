package de.mpg.imeji.rest.resources;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import de.mpg.imeji.rest.process.CollectionProcess;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.JSONResponse;

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
	public Response read(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		JSONResponse resp = CollectionProcess.readCollection(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	

	@PUT
	@Path("/{id}/release")
	@ApiOperation(value = "Release collection by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response release(@Context HttpServletRequest req,
			@PathParam("id") String id) throws Exception {
		JSONResponse resp = CollectionProcess.releaseCollection(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}

	@PUT
	@Path("/{id}/discard")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "Withraw a collection by id, with mandatory discard commenrt")
	@Produces(MediaType.APPLICATION_JSON)
	public Response withdraw(@Context HttpServletRequest req,
			@FormParam("id") String id, @FormParam("discardComment") String discardComment) throws Exception {
		JSONResponse resp = CollectionProcess.withdrawCollection(req, id, discardComment);
		return RestProcessUtils.buildJSONResponse(resp);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Create collection or new version of collection", notes = "The body parameter is the json of a collection. You can get an example by using the get collection method.")
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest req, InputStream json) {
		JSONResponse resp = CollectionProcess.createCollection(req);
		return RestProcessUtils.buildJSONResponse(resp);
	}

	@DELETE
	@Path("/{id}")
	@ApiOperation(value = "Delete collection by id", notes = "Deletes also the profile and items of this collection")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		JSONResponse resp = CollectionProcess.deleteCollection(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	

	@Override
	public Response create(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

}
