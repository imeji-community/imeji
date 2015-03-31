package de.mpg.imeji.rest.resources;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import de.mpg.imeji.rest.process.ProfileProcess;
import de.mpg.imeji.rest.to.JSONResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static de.mpg.imeji.rest.process.CollectionProcess.readCollectionItems;
import static de.mpg.imeji.rest.process.CollectionProcess.releaseCollection;
import static de.mpg.imeji.rest.process.CollectionProcess.withdrawCollection;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONResponse;


@Path("/profiles")
@Api(value = "rest/profiles", description = "Operations on profiles")
public class ProfileResource implements ImejiResource {

    @GET  
    @ApiOperation(value = "Get all profiles user has access to.")
    @Produces(MediaType.APPLICATION_JSON)
	public Response readAll(@Context HttpServletRequest req,  @QueryParam("q") String q) {
    	JSONResponse resp = ProfileProcess.readAll(req, q);
        return buildJSONResponse(resp);
	}
  
    @GET   
    @Path("/{id}")
    @ApiOperation(value = "Get profile by id. If id is equal to \"default\", the default metadata profile will be delivered.")
    @Produces(MediaType.APPLICATION_JSON)
	public Response read(@Context HttpServletRequest req, @PathParam("id") String id) {
    	JSONResponse resp = ProfileProcess.readProfile(req, id);
		return buildJSONResponse(resp);
	}

	@Override
	public Response create(@Context HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

    @DELETE 
    @Path("/{id}")
    @ApiOperation(value = "Delete profile by id")
    @Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest req, @PathParam("id") String id) {
		JSONResponse resp = ProfileProcess.deleteProfile(req, id);
		return buildJSONResponse(resp);
	}
	
    @PUT
	@Path("/{id}/release")
	@ApiOperation(value = "Release profile by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response release(@Context HttpServletRequest req,
			@PathParam("id") String id) throws Exception {
		JSONResponse resp = ProfileProcess.releaseProfile(req,id);
		return buildJSONResponse(resp);
	}
    
    @PUT
	@Path("/{id}/discard")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "Discard a metadata profile by id, with mandatory discard comment")
	@Produces(MediaType.APPLICATION_JSON)
	public Response withdraw(@Context HttpServletRequest req,
			@FormParam("id") String id, @FormParam("discardComment") String discardComment) throws Exception {
		JSONResponse resp = ProfileProcess.withdrawProfile(req, id, discardComment);
		return buildJSONResponse(resp);
	}

}
