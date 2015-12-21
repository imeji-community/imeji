package de.mpg.imeji.testimpl.rest.resources.item;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONFromObject;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildTOFromJSON;
import static de.mpg.imeji.test.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.test.rest.resources.test.integration.ImejiTestBase;

/**
 * Created by vlad on 09.12.14.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemUpdateBasicRaw extends ImejiTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemUpdateBasicRaw.class);

  private static String updateJSON;
  private static final String PATH_PREFIX = "/rest/items";
  private static final String UPDATED_FILE_NAME = "updated_filename.png";
  private final String UPDATE_ITEM_FILE_JSON = STATIC_CONTEXT_REST + "/updateItemFile.json";

  @BeforeClass
  public static void specificSetup() throws Exception {
    initCollection();
    initItem();
    updateJSON = getStringFromPath(STATIC_CONTEXT_REST + "/updateItemBasic.json");
  }


  @Test
  public void test_1_UpdateItem_1_Basic() throws IOException, BadRequestException {
    FormDataMultiPart multiPart = new FormDataMultiPart();

    // remove all metadata!!!
    final ItemWithFileTO item = (ItemWithFileTO) buildTOFromJSON(updateJSON, ItemWithFileTO.class);
    item.getMetadata().clear();

    multiPart
        .field("json", buildJSONFromObject(item).replace("___FILE_NAME___", UPDATED_FILE_NAME));

    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));
    ItemTO updatedItem = response.readEntity(ItemWithFileTO.class);

    assertEquals(OK.getStatusCode(), response.getStatus());
    assertThat("Filename has not been updated", updatedItem.getFilename(),
        equalTo(UPDATED_FILE_NAME));

  }


  @Test
  public void test_1_UpdateItem_2_NotAllowedUser() throws IOException {
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json", updateJSON);
    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser2)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals(FORBIDDEN.getStatusCode(), response.getStatus());
  }

  @Test
  public void test_1_UpdateItem_3_NotFoundItem() throws IOException {
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json", updateJSON);
    Response response =
        target(PATH_PREFIX).path("/" + itemId + "_not_exist_item")
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }


  @Test
  public void test_1_UpdateItem_4_Unauthorized() throws IOException {
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json", updateJSON);
    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

    Response response2 =
        target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUserFalse)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.UNAUTHORIZED.getStatusCode(), response2.getStatus());
}


  @Test
  public void test_2_UpdateItem_SyntaxInvalidJSONFile() throws Exception {

    FileDataBodyPart filePart =
        new FileDataBodyPart("file", new File("src/test/resources/storage/test.png"));
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    String wrongJSON = getStringFromPath("src/test/resources/rest/wrongSyntax.json");

    multiPart.field("json", wrongJSON

    .replace("___FILENAME___", "test.png"));

    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());

  }

  @Test
  public void test_2_UpdateItem_WrongID() throws Exception {
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json",
        getStringFromPath(UPDATE_ITEM_FILE_JSON).replace("___ITEM_ID___", "12345"));

    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
  }


}
