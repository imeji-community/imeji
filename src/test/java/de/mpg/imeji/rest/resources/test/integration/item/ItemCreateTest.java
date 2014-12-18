package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.rest.resources.test.TestUtils;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
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
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.io.IOException;

import static de.mpg.imeji.rest.resources.test.TestUtils.getStringFromPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Created by vlad on 09.12.14.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemCreateTest extends ImejiTestBase {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ItemCreateTest.class);

    private static String updateJSON;
    private static String itemJSON;
    private static final String pathPrefix = "/items";
    private static final String updatedFileName = "updated_filename.png";

    @BeforeClass
    public static void specificSetup() throws Exception {
        initCollection();
        initItem();
        updateJSON = getStringFromPath("src/test/resources/rest/updateItem.json");
    }

    @Test
    public void createItemWithEmtpyFilename() throws IOException {
        itemJSON = TestUtils
                .getStringFromPath("src/test/resources/rest/createItem.json");
        itemJSON = itemJSON.replace("___COLLECTION_ID___", collectionId);

        FileDataBodyPart filePart = new FileDataBodyPart("file", new File(
                "src/test/resources/storage/test.png"));
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON.replace("___FILENAME___", ""));

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void createItemWithoutFilename() throws IOException {
        itemJSON = TestUtils
                .getStringFromPath("src/test/resources/rest/createItem.json");
        itemJSON = itemJSON
                .replace("  \"filename\": \"___FILENAME___\",", "")
                .replace("___COLLECTION_ID___", collectionId);

        FileDataBodyPart filePart = new FileDataBodyPart("file", new File(
                "src/test/resources/storage/test.png"));
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON.replace("___FILENAME___", ""));

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void createItemWithFilename() throws IOException {
        itemJSON = TestUtils
                .getStringFromPath("src/test/resources/rest/createItem.json");
        itemJSON = itemJSON.replace("___COLLECTION_ID___", collectionId);

        FileDataBodyPart filePart = new FileDataBodyPart("file", new File(
                "src/test/resources/storage/test.png"));
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON.replace("___FILENAME___", "test.png"));

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
    }

}