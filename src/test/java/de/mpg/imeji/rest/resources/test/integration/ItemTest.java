package de.mpg.imeji.rest.resources.test.integration;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

import static de.mpg.imeji.rest.resources.test.TestUtils.getStringFromPath;
import static javax.ws.rs.core.Response.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by vlad on 09.12.14.
 */
public class ItemTest extends ImejiRestTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemTest.class);

    private static String collectionId;
    private static String itemId;
    private static String updateJSON;
    private static String itemJSON;
    private static ItemTO itemTO;
    private static final String pathPrefix = "/items";
    private static final String updatedFileName = "updated_filename.png";

    @BeforeClass
    public static void initItem() throws Exception {
        CollectionImeji c = ImejiFactory.newCollection();
        CollectionController controller = new CollectionController();
        controller.create(c, null, adminUser);
        assertNotNull(c);
        collectionId = c.getIdString();

        //get full item
        ItemWithFileTO itemWithFileTO = (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(
                getStringFromPath("src/test/resources/rest/itemFull.json"), ItemWithFileTO.class);

        itemWithFileTO.setFile(new File("src/test/resources/storage/test.png"));
        itemWithFileTO.setCollectionId(collectionId);
        ItemService is = new ItemService();
        itemTO =  is.create(itemWithFileTO, adminUser);
        assertNotNull(itemTO);
        itemId = itemTO.getId();

        updateJSON = getStringFromPath("src/test/resources/rest/updateItem.json");

    }

    @Test
    public void test_1_UpdateItem_1_Basic() throws IOException {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", updateJSON
                .replace("___FILENAME___", updatedFileName));

        Response response = target(pathPrefix)
                .path("/" + itemId)
                .register(authAsAdmin)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), Status.OK.getStatusCode());
        ItemTO updatedItem = (ItemTO) response.readEntity(ItemWithFileTO.class);
        assertThat("Filename has not been updated", updatedItem.getFilename(), equalTo(updatedFileName));

    }

    @Test
    public void test_1_UpdateItem_2_NotAllowedUser() throws IOException {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", updateJSON);
        Response response = target(pathPrefix)
                .path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), Status.FORBIDDEN.getStatusCode());
    }

    @Ignore
    @Test
    public void test_1_UpdateItem_3_WithFile() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", new File("src/test/resources/storage/test2.png"));
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", getStringFromPath("src/test/resources/rest/updateItem_short.json"));

        Response response = target(pathPrefix)
                .path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        //ItemTO updatedItem = (ItemTO) response.readEntity(ItemWithFileTO.class);
        LOGGER.info(response.readEntity(String .class));


    }
}
