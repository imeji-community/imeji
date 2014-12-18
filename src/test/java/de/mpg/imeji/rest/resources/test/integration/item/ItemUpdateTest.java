package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

import static de.mpg.imeji.rest.resources.test.TestUtils.getStringFromPath;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Created by vlad on 09.12.14.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemUpdateTest extends ImejiTestBase {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ItemUpdateTest.class);

    private static String updateJSON;
    private static final String pathPrefix = "/items";
    private static final String updatedFileName = "updated_filename.png";

    @BeforeClass
    public static void specificSetup() throws Exception {
        initCollection();
        initItem();
        updateJSON = getStringFromPath("src/test/resources/rest/updateItem.json");
    }

    @Test
    public void test_1_UpdateItem_1_Basic() throws IOException {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json",
                updateJSON.replace("___FILENAME___", updatedFileName));

        Response response = target(pathPrefix).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), OK.getStatusCode());
        ItemTO updatedItem = (ItemTO) response.readEntity(ItemWithFileTO.class);
        assertThat("Filename has not been updated", updatedItem.getFilename(),
                equalTo(updatedFileName));

    }

    @Test
    public void test_1_UpdateItem_2_NotAllowedUser() throws IOException {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", updateJSON);
        Response response = target(pathPrefix).path("/" + itemId)
                .register(authAsUser2)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), FORBIDDEN.getStatusCode());
    }

    @Test
    public void test_1_UpdateItem_3_WithFile() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", new File(
                "src/test/resources/storage/test2.png"));
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json",
                getStringFromPath("src/test/resources/rest/updateItem_short.json"));

        Response response = target(pathPrefix).path("/" + itemId)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(response.getStatus(), OK.getStatusCode());
        // ItemTO updatedItem = (ItemTO)
        // response.readEntity(ItemWithFileTO.class);
        LOGGER.info(response.readEntity(String.class));

    }
}