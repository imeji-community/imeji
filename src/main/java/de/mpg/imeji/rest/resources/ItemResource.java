package de.mpg.imeji.rest.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.process.ItemProcess;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.JSONResponse;

@Path("/items")
@Api(value = "rest/items", description = "Operations on items")
public class ItemResource implements ImejiResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAll(@Context HttpServletRequest req) {
		return null;
	}

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Get item by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readFromID(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		JSONResponse resp = ItemProcess.readItem(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "(Not implemented) Create new item", notes = "Create an item with a file")
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest req,
			@FormDataParam("collectionId") String collectionId,
			@FormDataParam("file") InputStream file,
			@FormDataParam("json") String json) {
		System.out.println("create in " + collectionId);
		try {
			File tmp = File.createTempFile("createItem", null);
			IOUtils.copy(file, new FileOutputStream(tmp));
			System.out.println(tmp.getAbsolutePath());
			System.out.println("json " + json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Response create(HttpServletRequest req) {
		return null;
	}

	@DELETE
	@Path("/{id}")
	@ApiOperation(value = "(Not implemented) Delete item by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		return null;
	}

}
