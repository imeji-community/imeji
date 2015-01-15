package de.mpg.imeji.rest.resources.test.integration.storage;

import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.StorageTO;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;

/**
 * Created by vlad on 13.01.15.
 */
public class TestStorage extends ImejiTestBase{
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TestStorage.class);

    private final String PATH_PREFIX = "/rest/storage";


    @Test
    public void test_1_uploadFormats() throws IOException {


        Response response = target(PATH_PREFIX)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        String s = response.readEntity(String.class);
        LOGGER.info(s);
        assertEquals(response.getStatus(), OK.getStatusCode());

    }


}
