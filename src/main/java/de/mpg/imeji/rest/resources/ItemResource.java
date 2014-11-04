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

import de.mpg.imeji.rest.process.ItemProcess;
import de.mpg.imeji.rest.process.RestProcessUtils;

@Path("/items")
public class ItemResource implements ImejiResource{

	
    @GET  
    @Produces(MediaType.APPLICATION_JSON)  
    public Response readAll(@Context HttpServletRequest req){       
        return null;  
    } 
    
    @GET   
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readFromID(@Context HttpServletRequest req, @PathParam("id") String id){
        return RestProcessUtils.buildJSONResponse(ItemProcess.readItem(req,id));


    }

    @POST 
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
	public Response create(@Context HttpServletRequest req) {
		return null;
	}  

    @DELETE  
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON) 
	public Response delete(@Context HttpServletRequest req, @PathParam("id") String id) {
		return null;
	}

    
    
    
    

}
