package de.mpg.imeji.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.process.StorageProcess;

/**
 * Created by vlad on 13.01.15.
 */
@Path("/storage")
@Api(value = "rest/storage", description = "Storage properties")
public class StorageResource {

        @GET
        @Path("/")
        @ApiOperation(value = "Get storage properties")
        @Produces(MediaType.APPLICATION_JSON)
        public Response getStorageProperties(){
                return RestProcessUtils.buildJSONResponse(StorageProcess.getStorageProperties());
        }

}
