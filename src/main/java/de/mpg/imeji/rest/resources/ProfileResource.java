package de.mpg.imeji.rest.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import de.mpg.imeji.rest.process.ProfileProcess;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.JSONResponse;


@Path("/profiles")
@Api(value = "rest/profiles", description = "Operations on profiles")
public class ProfileResource implements ImejiResource {

    @GET  
    @Produces(MediaType.APPLICATION_JSON)
	public Response readAll(@Context HttpServletRequest req) {
		return null;
	}
  
    @GET   
    @Path("/{id}")
    @ApiOperation(value = "Get profile by id")
    @Produces(MediaType.APPLICATION_JSON)
	public Response readFromID(@Context HttpServletRequest req, @PathParam("id") String id) {
    	JSONResponse resp = ProfileProcess.readProfile(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
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
		return RestProcessUtils.buildJSONResponse(resp);
	}

	
}
