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

import de.mpg.imeji.rest.process.AlbumProcess;
import de.mpg.imeji.rest.process.CollectionProcess;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.JSONResponse;


@Path("/albums")
@Api(value = "rest/albums", description = "Operations on albums")
public class AlbumResource implements ImejiResource{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CollectionResource.class);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Create album or new version of album", notes = "The body parameter is the json of an album. You can get an example by using the get album method.")
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest req, InputStream json) {
		JSONResponse resp = AlbumProcess.createAlbum(req);
		return RestProcessUtils.buildJSONResponse(resp);
	}

	@Override
	public Response readAll(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Get album by id")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response read(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		JSONResponse resp = AlbumProcess.readAlbum(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}

	@Override
	public Response create(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@DELETE
	@Path("/{id}")
	@ApiOperation(value = "Delete album by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		JSONResponse resp = AlbumProcess.deleteAlbum(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	
	@PUT
	@Path("/{id}/discard")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "Withraw a album by id, with mandatory discard commenrt")
	@Produces(MediaType.APPLICATION_JSON)
	public Response withdraw(@Context HttpServletRequest req,
			@FormParam("id") String id, @FormParam("discardComment") String discardComment) throws Exception {
		JSONResponse resp = AlbumProcess.withdrawAlbum(req, id, discardComment);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	
	@PUT
	@Path("/{id}/release")
	@ApiOperation(value = "Release album by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response release(@Context HttpServletRequest req, @PathParam("id") String id) throws Exception {
		JSONResponse resp = AlbumProcess.releaseAlbum(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	
	@PUT
	@Path("/{id}/add")
	@ApiOperation(value = "Release album by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(@Context HttpServletRequest req, InputStream json, @PathParam("id") String id) throws Exception {
		JSONResponse resp = AlbumProcess.addItem(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	
	
}
