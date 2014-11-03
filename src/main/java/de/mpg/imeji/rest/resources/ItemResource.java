package de.mpg.imeji.rest.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.rest.process.ItemProcess;

@Path("/items")
public class ItemResource {

	
    @GET  
    @Produces(MediaType.APPLICATION_XML)  
    public List<Item> getAllItems(){       

        return null;  
    } 
    
    @GET   
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON) 
    public Response getItemFromID(@Context HttpServletRequest request, @PathParam("id") String id){
        return ItemProcess.buildJSONResponse(request, id);  

    }
    
    
    
    

}
