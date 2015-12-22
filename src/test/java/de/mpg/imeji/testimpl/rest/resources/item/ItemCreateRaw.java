package de.mpg.imeji.testimpl.rest.resources.item;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.process.RestProcessUtils.jsonToPOJO;
import static de.mpg.imeji.test.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_PATH;
import static de.mpg.imeji.test.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_STORAGE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.java.dev.webdav.jaxrs.ResponseStatus;

import org.apache.commons.httpclient.HttpStatus;
import org.codehaus.jettison.json.JSONException;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.test.rest.resources.test.integration.ItemTestBase;

/**
 * Created by vlad on 09.12.14.
 */

public class ItemCreateRaw extends ItemTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemCreateRaw.class);
  public static final File TEST_PNG_FILE = new File("src/test/resources/storage/test.png");

  private static String itemJSON;
  private static final String pathPrefix = "/rest/items";
  private static final File ATTACHED_FILE = new File(STATIC_CONTEXT_STORAGE + "/test2.jpg");

  @BeforeClass
  public static void specificSetup() throws Exception {
    initCollection();
    initItem();
    //Replace in RAW format metadata{} with metadata[]
    itemJSON = getStringFromPath("src/test/resources/rest/createItemBasic.json").replace("\"metadata\": {}", "\"metadata\": []");
  }

  @Test
  public void createItemWithEmptyFilename() throws IOException {

    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json",
        itemJSON.replace("___COLLECTION_ID___", collectionId).replace("___FILENAME___", ""));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
  }

  // see bug https://github.com/imeji-community/imeji/issues/1023
  @Test
  public void createItemWithFile_NullAsExtensionInFileUrl_Bug1023() throws IOException {
    initCollection();
    FileDataBodyPart filePart =
        new FileDataBodyPart("file", new File("src/test/resources/storage/test"));
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replaceAll("\\s*\"filename\":\\s*\"___FILENAME___\"\\s*,", ""));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(CREATED.getStatusCode(), response.getStatus());
    ItemTO createdItem = (ItemTO) response.readEntity(ItemTO.class);

    assertThat(createdItem.getFileUrl().toString(),
        allOf(not(endsWith(".null")), endsWith(".png")));

  }

  @Test
  public void createItemWithoutFilename() throws IOException {

    initCollection();
    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replaceAll("\\s*\"filename\":\\s*\"___FILENAME___\"\\s*,", ""));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(CREATED.getStatusCode(), response.getStatus());
    ItemTO item = response.readEntity(ItemTO.class);
    assertThat("File name is wrong", item.getFilename(),
        equalTo(filePart.getFileEntity().getName()));

  }

  @Test
  public void createItemWithFilename() throws IOException {
    initCollection();
    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FILENAME___", "test.png"));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(CREATED.getStatusCode(), response.getStatus());
    Map<String, Object> itemData = jsonToPOJO(response);
    assertEquals(Long.toString(TEST_PNG_FILE.length()),
        Integer.toString((Integer) itemData.get("fileSize")));
  }

  @Test
  public void createItemWithOutCollection() throws IOException {

    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON);

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
  }

  @Test
  public void createItemWithoutFile() throws IOException {

    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json",
        itemJSON.replace("___COLLECTION_ID___", collectionId).replace("___FILENAME___", "test.png")
            .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
            .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", ""));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
  }

  @Test
  public void createItemInNotExistingCollection() throws IOException {
    initCollection();
    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId + "i_do_not_exist")
        .replace("___FILENAME___", "test.png"));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
  }

  @Test
  public void createItem_NotLoggedIn() throws IOException {
    initCollection();
    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FILENAME___", "test.png"));

    Response response = target(pathPrefix).register(MultiPartFeature.class).
         queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
        .register(JacksonFeature.class).request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

    Response response2 = target(pathPrefix).register(authAsUserFalse).register(MultiPartFeature.class)
        .register(JacksonFeature.class).request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.UNAUTHORIZED.getStatusCode(), response2.getStatus());

  }

  @Test
  public void createItem_InReleasedCollection() throws Exception {
    initCollection();
    initItem("test1");
    CollectionService sc = new CollectionService();
    sc.release(collectionId, JenaUtil.testUser);
    assertEquals("RELEASED", sc.read(collectionId, JenaUtil.testUser).getStatus());

    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FILENAME___", "test.png"));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.CREATED.getStatusCode(), response.getStatus());

    ItemService is = new ItemService();
    assertEquals("RELEASED", is.read(itemId, JenaUtil.testUser).getStatus());

  }

  @Test
  public void createItem_InWithdrawnCollection() throws Exception {
    initCollection();
    initItem();
    CollectionService sc = new CollectionService();
    sc.release(collectionId, JenaUtil.testUser);
    assertEquals("RELEASED", sc.read(collectionId, JenaUtil.testUser).getStatus());
    sc.withdraw(collectionId, JenaUtil.testUser, "ItemCreateTest_createItem_InWithdrawnCollection");

    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FILENAME___", "test.png"));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

  }

  @Test
  public void createItem_WithNotAllowedUser() throws Exception {
    initCollection();
    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FILENAME___", "test.png"));

    Response response = target(pathPrefix).queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser2)
        .register(MultiPartFeature.class).register(JacksonFeature.class)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());

  }

  @Test
  public void createItem_SyntaxInvalidJSONFile() throws Exception {
    initCollection();
    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    String wrongJSON = getStringFromPath("src/test/resources/rest/wrongSyntax.json");

    multiPart.field("json", wrongJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FILENAME___", "test.png"));

    //LOGGER.info(multiPart.getField("json").getValue());

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    //LOGGER.info(response.readEntity(String.class));
    assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());

  }

  @Test
  public void createItem_WithFile_Fetched() throws IOException {

    final String fileURL = target().getUri() + STATIC_CONTEXT_PATH.substring(1) + "/test2.jpg";

    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json",
        itemJSON.replace("___COLLECTION_ID___", collectionId).replace("___FETCH_URL___", fileURL)

    );

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.CREATED.getStatusCode(), response.getStatus());

  }

  @Test
  public void createItem_WithFile_Fetched_WithEmptyFileName() throws IOException {

    final String fileURL = target().getUri() + STATIC_CONTEXT_PATH.substring(1) + "/test2.jpg";

    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FETCH_URL___", fileURL).replace("___FILENAME___", ""));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.CREATED.getStatusCode(), response.getStatus());

  }

  @Test
  public void createItem_WithFile_Referenced() throws IOException {

    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json",
        itemJSON.replace("___COLLECTION_ID___", collectionId).replace("___FILENAME___", "test.png")
            .replace("___REFERENCE_URL___",
                "http://th03.deviantart.net/fs71/PRE/i/2012/242/1/f/png_moon_by_paradise234-d5czhdo.png")
        .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")

    );

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(Status.CREATED.getStatusCode(), response.getStatus());

  }

  @Test
  public void createItem_InvalidFetchURL() throws IOException {

    FormDataMultiPart multiPart = new FormDataMultiPart();

    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FETCH_URL___", "invalid url")

    );

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

  }

  @Test
  public void createItem_FetchURL_NoFile() throws IOException {

    FormDataMultiPart multiPart = new FormDataMultiPart();

    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FETCH_URL___", "www.google.de")

    );

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

  }

  @Test
  public void createItemChecksumTest() throws IOException, JSONException {
    initCollection();
    initItem();
    // init Item creates already one item with test.png file , thus checksum
    // is expected
    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FILENAME___", "test.png"));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    if (Imeji.isValidateChecksumInCollection()) {
      assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
    } else {
      assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
    }
  }

  @Test
  public void createItemExtensionsTest() throws IOException, JSONException {
    // NOTE: test assumes .exe file will never be allowed!!!!
    initCollection();
    initItem();
    // init Item creates already one item with test.png file , thus checksum
    // is expected
    FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.bodyPart(filePart);
    multiPart.field("json", itemJSON.replace("___COLLECTION_ID___", collectionId)
        .replace("___FILENAME___", "test.exe"));

    Response response = getAuthTarget().post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
  }

  // Default Authorized Target with imeji syntax
  private Invocation.Builder getAuthTarget() {
    return target(pathPrefix).register(authAsUser)
        .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
        .register(MultiPartFeature.class).register(JacksonFeature.class)
        .request(MediaType.APPLICATION_JSON_TYPE);
  }

  /**
   * @return the itemJSON
   */
  public static String getItemJSON() {
    return itemJSON;
  }

  /**
   * @param itemJSON the itemJSON to set
   */
  public static void setItemJSON(String itemJSON) {
    ItemCreateRaw.itemJSON = itemJSON;
  }

  /**
   * @return the logger
   */
  public static Logger getLogger() {
    return LOGGER;
  }

  /**
   * @return the testPngFile
   */
  public static File getTestPngFile() {
    return TEST_PNG_FILE;
  }

  /**
   * @return the pathprefix
   */
  public static String getPathprefix() {
    return pathPrefix;
  }

  /**
   * @return the attachedFile
   */
  public static File getAttachedFile() {
    return ATTACHED_FILE;
  }

}
