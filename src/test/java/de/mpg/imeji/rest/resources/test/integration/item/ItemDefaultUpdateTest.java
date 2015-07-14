package de.mpg.imeji.rest.resources.test.integration.item;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONFromObject;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildTOFromJSON;
import static de.mpg.imeji.rest.process.RestProcessUtils.jsonToPOJO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.defaultTO.DefaultItemTO;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.MetadataProfileTO;


public class ItemDefaultUpdateTest extends ItemTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemDefaultUpdateTest.class);

  private static String createItemJSON;
  protected static String updateItemJSON;
  private static final String PATH_PREFIX = "/rest/items";
  public static final String METADATA_KEY = "metadata";

  DefaultItemTO defaultItemTO;


  @BeforeClass
  public static void specificSetup() throws Exception {

    initCollectionWithProfile(getDefaultBasicStatements());
    createItemJSON = getStringFromPath("src/test/resources/rest/defaultCreateItem.json");
    defaultCreatItemWithFullMetadata();
    updateItemJSON = getStringFromPath("src/test/resources/rest/defaultUpdateItem.json");
  }

  @Test
  public void test_1_updateItem_updateMetadata_emptySyntax() throws IOException,
      BadRequestException {
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json", updateItemJSON.replace("___COLLECTION_ID___", collectionId));


    Response response = getTargetAuth().put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Ignore
  @Test
  public void test_2_updateItem_deleteAllMetadata() throws IOException, BadRequestException {

    FormDataMultiPart multiPart = new FormDataMultiPart();
    String jsonNew =
        updateItemJSON.replace("___COLLECTION_ID___", collectionId).replaceAll(
            "\"metadata\"\\s*:\\s*\\{[\\d\\D]*\\}", "\"metadata\":{}}");

    multiPart.field("json", jsonNew);
    Response response = getTargetAuth().put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    defaultItemTO = response.readEntity(DefaultItemTO.class);
    assertThat(defaultItemTO.getMetadata().keySet(), hasSize(0));
  }

  @Test
  public void test_3_updateItem_rawSyntax_BAD_REQUEST() throws IOException, BadRequestException {

    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json", updateItemJSON.replace("___COLLECTION_ID___", collectionId));


    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

  }

  @Test
  public void test_4_updateItem_defaultSyntax_OK() throws IOException, BadRequestException {

    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json", updateItemJSON.replace("___COLLECTION_ID___", collectionId));


    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.DEFAULT.toString().toLowerCase())
            .register(authAsUser).register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

  }

  protected static void defaultCreatItemWithFullMetadata() throws Exception {
    createItemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FILENAME___", "test.png")
        .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
        .replaceAll("___REFERENCE_URL___", "");

    Map<String, Object> itemMap = jsonToPOJO(createItemJSON);
    HashMap<String, Object> metadata = (LinkedHashMap<String, Object>) itemMap.remove(METADATA_KEY);
    itemTO =
        (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(buildJSONFromObject(itemMap),
            ItemWithFileTO.class);
    itemTO.setCollectionId(collectionId);
    ((ItemWithFileTO) itemTO).setFile(new File("src/test/resources/storage/test.png"));

    DefaultItemTO defaultTO =
        (DefaultItemTO) buildTOFromJSON("{\"" + METADATA_KEY + "\":"
            + buildJSONFromObject(metadata) + "}", DefaultItemTO.class);

    CollectionTO col = new CollectionService().read(itemTO.getCollectionId(), JenaUtil.testUser);
    MetadataProfileTO mdProfileTO =
        new ProfileService().read(col.getProfile().getId(), JenaUtil.testUser);

    ReverseTransferObjectFactory.transferDefaultItemTOtoItemTO(mdProfileTO, defaultTO, itemTO);
    ItemService s = new ItemService();
    itemTO = s.create(itemTO, JenaUtil.testUser);
    System.out.println(itemTO.getMetadata().size());
    itemId = itemTO.getId();
  }

  private Invocation.Builder getTargetAuth() {
    return target(PATH_PREFIX).path("/" + itemId).register(authAsUser)
        .register(MultiPartFeature.class).register(JacksonFeature.class)
        .request(MediaType.APPLICATION_JSON_TYPE);
  }


}
