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

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import de.mpg.imeji.rest.process.CollectionProcess;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.JSONResponse;

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
	

	@PUT
	@Path("/{id}/release")
	@ApiOperation(value = "Release collection by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response releaseFromID(@Context HttpServletRequest req,
			@PathParam("id") String id) throws Exception {
		JSONResponse resp = CollectionProcess.releaseCollection(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}

	@PUT
	@Path("/{id}/withdraw")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "Withraw a collection by id, with mandatory discard commenrt")
	@Produces(MediaType.APPLICATION_JSON)
	
	/*
	 * 
	 * 
	 * 
	 * @Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Update an item", notes = "Update an already existed item. Both the item metadata and the item file can be updated. File can be defined either as (by order of priority):"
			+ "<br/> 1) form parameter (multipart/form-data)<br/> 2) json parameter: \"fetchUrl\" : \"http://example.org/myFile.png\" (myFile.png will be uploaded in imeji) "
			+ "<br/> 3) json parameter \"referenceUrl\" : \"http://example.org/myFile.png\" (myFile.png will be only referenced in imeji, i.e. not uploaded)"
			+ "<br/><br/>"
			+ "Json example:"
			+ "<div class=\"json_example\">"
			+ "{"
			+ "<br/>\"id\" : \"abc123\","
			+ "<br/>\"collectionId\" : \"def123\","
			+ "<br/>\"fetchUrl\" : \"http://example.org/myFile.png\","
			+ "<br/>\"referenceUrl\" : \"http://example.org/myFile.png\","
			+ "<br/>\"metadata\" : []"
			+ "<br/>}"
			+"</div>"
			+ "<br/><br/>"
			+ "The metadata parameter allows to define the metadata of item during the creation of the item. To get an example of how to do it, please try the get item method")
	@Produces(MediaType.APPLICATION_JSON)
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	public Response withdrawFromID(@Context HttpServletRequest req,
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
