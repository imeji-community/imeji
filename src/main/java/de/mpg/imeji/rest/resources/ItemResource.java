package de.mpg.imeji.rest.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import de.mpg.imeji.rest.process.ItemProcess;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.JSONResponse;

@Path("/items")
@Api(value = "rest/items", description = "Operations on items")
public class ItemResource implements ImejiResource{

	
    @GET  
    @Produces(MediaType.APPLICATION_JSON)  
    public Response readAll(@Context HttpServletRequest req){       
        return null;  
    } 
    
    @GET   
    @Path("/{id}")
    @ApiOperation(value = "Get item by id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readFromID(@Context HttpServletRequest req, @PathParam("id") String id){
    	JSONResponse resp = ItemProcess.readItem(req,id);
        return RestProcessUtils.buildJSONResponse(resp);


    }

    @POST 
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "(Not implemented) Create new item", notes = "When contains parameter \"versionOf\", new item is created as a copy of released item (statements only, not binaries)")
    @Produces(MediaType.APPLICATION_JSON) 
	public Response create(@Context HttpServletRequest req) {
		return null;
	}  

    @DELETE  
    @Path("/{id}")
    @ApiOperation(value = "(Not implemented) Delete item by id")
    @Produces(MediaType.APPLICATION_JSON) 
	public Response delete(@Context HttpServletRequest req, @PathParam("id") String id) {
		return null;
	}

    
    
    
    

}
