package de.mpg.imeji.rest.resources;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by vlad on 13.01.15.
 */
@Api(value = "rest/storage", description = "Storage configuration")
public class StorageResource {

        @Path("/storage")
        @GET
        @ApiOperation(value = "Get storage configuration")
        @Produces(MediaType.APPLICATION_JSON)
        public Response getStorageConfig()
        {
            return null;
        }

}
