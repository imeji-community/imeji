package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.ItemTO;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

import static de.mpg.imeji.rest.resources.test.TestUtils.getStringFromPath;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Created by vlad on 09.12.14.
 */

public class ItemCreateTest extends ImejiTestBase {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ItemCreateTest.class);

    private static String itemJSON;
    private static final String pathPrefix = "/items";

    @BeforeClass
    public static void specificSetup() throws Exception {
        initCollection();
        initItem();
        itemJSON = getStringFromPath("src/test/resources/rest/createItem.json");
    }

    @Test
    public void createItemWithEmtpyFilename() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", new File(
                "src/test/resources/storage/test.png"));
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                        .replace("___COLLECTION_ID___", collectionId)
                        .replace("___FILENAME___", "")
        );

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void createItemWithoutFilename() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", new File(
                "src/test/resources/storage/test.png"));
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replaceAll("\\s*\"filename\":\\s*\"___FILENAME___\"\\s*,", "")
        );

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(CREATED.getStatusCode(), response.getStatus());
        ItemTO item = response.readEntity(ItemTO.class);
        assertThat("File name is wrong", item.getFilename(), equalTo(filePart.getFileEntity().getName()));

    }

    @Test
    public void createItemWithFilename() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", new File(
                "src/test/resources/storage/test.png"));
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replace("___FILENAME___", "test.png"));

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(CREATED.getStatusCode(), response.getStatus());
    }

}