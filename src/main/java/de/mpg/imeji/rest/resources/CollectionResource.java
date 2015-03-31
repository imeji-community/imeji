package de.mpg.imeji.rest.resources;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import de.mpg.imeji.rest.to.JSONResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static de.mpg.imeji.rest.process.CollectionProcess.*;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONResponse;

//@Singleton
@Path("/collections")
@Api(value = "rest/collections", description = "Operations on collections")
public class CollectionResource implements ImejiResource {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CollectionResource.class);

    @GET
    @Path("/{id}/items")
    @ApiOperation(value = "Get all items of collection by id.", notes = "The result set can be filtered by query (optional)")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readItemsWithQuery(@Context HttpServletRequest req,
                                 @PathParam("id") String id,
                                 @QueryParam("q") String q
    ) {
        JSONResponse resp = readCollectionItems(req, id, q);
        return buildJSONResponse(resp);
	}

    @GET
    @ApiOperation(value = "Get all collections user has access to.")
	@Produces(MediaType.APPLICATION_JSON)
    public Response readAll(@Context HttpServletRequest req ) {
    	        JSONResponse resp = readAllCollections(req, null);
    	        return buildJSONResponse(resp);
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Get collection by id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@Context HttpServletRequest req,
                         @PathParam("id") String id) {
        JSONResponse resp = readCollection(req, id);
        return buildJSONResponse(resp);
    }


	@PUT
	@Path("/{id}")
	@ApiOperation(value = "Update collection by id")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(
            @Context HttpServletRequest req,
            @PathParam("id") String id,
            @FormParam("json") String json) throws Exception {
		JSONResponse resp = updateCollection(req, id, json);
		return buildJSONResponse(resp);
	}

	@PUT
	@Path("/{id}/release")
	@ApiOperation(value = "Release collection by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response release(@Context HttpServletRequest req,
			@PathParam("id") String id) throws Exception {
		JSONResponse resp = releaseCollection(req, id);
		return buildJSONResponse(resp);
	}

	@PUT
	@Path("/{id}/discard")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "Discard a collection by id, with mandatory discard comment")
	@Produces(MediaType.APPLICATION_JSON)
	public Response withdraw(@Context HttpServletRequest req,
			@FormParam("id") String id, @FormParam("discardComment") String discardComment) throws Exception {
		JSONResponse resp = withdrawCollection(req, id, discardComment);
		return buildJSONResponse(resp);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Create collection or new version of collection", notes = "The body parameter is the json of a collection. You can get an example by using the get collection method.")
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest req, InputStream json) {
		JSONResponse resp = createCollection(req);
		return buildJSONResponse(resp);
	}

	@DELETE
	@Path("/{id}")
	@ApiOperation(value = "Delete collection by id", notes = "Deletes also the profile and items of this collection")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		JSONResponse resp = deleteCollection(req, id);
		return buildJSONResponse(resp);
	}
	

	@Override
	public Response create(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@GET
	@Path("/search")
	@ApiOperation(value = "Get all collections user has access to." , notes = "The result set can be filtered by query (optional)")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search (@Context HttpServletRequest req,  @QueryParam("q") String q) {
	    	JSONResponse resp = readAllCollections(req, q);
	    	return buildJSONResponse(resp);
	}
}
