package de.mpg.imeji.testimpl.rest.resources.item;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.java.dev.webdav.jaxrs.ResponseStatus;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.test.rest.resources.test.integration.ItemTestBase;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemDeleteRaw extends ItemTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemDeleteRaw.class);
  private static String itemJSON;
  private static final String pathPrefix = "/rest/items";

  @BeforeClass
  public static void specificSetup() throws Exception {
    initCollection();
    createItem();
    itemJSON = getStringFromPath("src/test/resources/rest/createItem.json");
  }

  @Test
  public void test_1_deleteItem_WithNonAuth() throws Exception {
    ItemService s = new ItemService();
    assertEquals("PENDING", s.read(itemId, JenaUtil.testUser).getStatus());

    Form form = new Form();
    form.param("id", itemId);
    Response response =
        target(pathPrefix).path("/" + itemId)
        .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
        .request(MediaType.APPLICATION_JSON_TYPE).delete();
    assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

    Response response2 =
        target(pathPrefix).register(authAsUserFalse).path("/" + itemId)
        .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
        .request(MediaType.APPLICATION_JSON_TYPE).delete();
    assertEquals(Status.UNAUTHORIZED.getStatusCode(), response2.getStatus());

  }

  @Test
  public void test_2_deleteItem_NotAllowed() throws Exception {
    initCollection();
    createItem();
    ItemService s = new ItemService();
    assertEquals("PENDING", s.read(itemId, JenaUtil.testUser).getStatus());

    Form form = new Form();
    form.param("id", itemId);
    Response response = target(pathPrefix).register(authAsUser2).path("/" + itemId)
        .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
        .request(MediaType.APPLICATION_JSON_TYPE).delete();

    assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
  }

  @Test
  public void test_3_deleteItem_NotExist() throws Exception {

    createItem();
    ItemService s = new ItemService();
    assertEquals("PENDING", s.read(itemId, JenaUtil.testUser).getStatus());

    Form form = new Form();
    form.param("id", itemId + "i_do_not_exist");
    Response response = target(pathPrefix).register(authAsUser)
        .path("/" + itemId + "i_do_not_exist").queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).request(MediaType.APPLICATION_JSON_TYPE).delete();

    assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  public void test_2_deleteItem_Released() throws Exception {
    initCollection();
    createItem();
    ItemService s = new ItemService();
    assertEquals("PENDING", s.read(itemId, JenaUtil.testUser).getStatus());
    CollectionService cs = new CollectionService();
    cs.release(s.read(itemId, JenaUtil.testUser).getCollectionId(), JenaUtil.testUser);
    assertEquals("RELEASED", s.read(itemId, JenaUtil.testUser).getStatus());

    Form form = new Form();
    form.param("id", itemId);
    Response response = target(pathPrefix).register(authAsUser).path("/" + itemId)
        .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
        .request(MediaType.APPLICATION_JSON_TYPE).delete();

    assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
  }

  @Test
  public void test_2_deleteItem_Withdrawn() throws Exception {
    initCollection();
    createItem();
    ItemService s = new ItemService();
    assertEquals("PENDING", s.read(itemId, JenaUtil.testUser).getStatus());
    CollectionService cs = new CollectionService();
    cs.release(s.read(itemId, JenaUtil.testUser).getCollectionId(), JenaUtil.testUser);
    assertEquals("RELEASED", s.read(itemId, JenaUtil.testUser).getStatus());
    cs.withdraw(s.read(itemId, JenaUtil.testUser).getCollectionId(), JenaUtil.testUser,
        "ItemDeleteTest.test_2_deleteItemWithdrawn");
    assertEquals("WITHDRAWN", s.read(itemId, JenaUtil.testUser).getStatus());

    Form form = new Form();
    form.param("id", itemId);
    Response response = target(pathPrefix).register(authAsUser).path("/" + itemId)
        .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
        .request(MediaType.APPLICATION_JSON_TYPE).delete();

    assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
  }

  @Test
  public void test_3_deleteItem() throws Exception {
    initCollection();
    createItem();
    ItemService s = new ItemService();
    assertEquals("PENDING", s.read(itemId, JenaUtil.testUser).getStatus());

    Form form = new Form();
    form.param("id", itemId);
    Response response = target(pathPrefix).register(authAsUser).path("/" + itemId)
        .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
        .request(MediaType.APPLICATION_JSON_TYPE).delete();

    assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

  }

  @Test
  public void test_3_deleteItemTwice() throws Exception {
    initCollection();
    createItem();
    ItemService s = new ItemService();
    assertEquals("PENDING", s.read(itemId, JenaUtil.testUser).getStatus());

    Form form = new Form();
    form.param("id", itemId);
    Response response = target(pathPrefix).register(authAsUser).path("/" + itemId)
        .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
        .request(MediaType.APPLICATION_JSON_TYPE).delete();

    assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

    Response response2 = target(pathPrefix).register(authAsUser).path("/" + itemId)
        .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
        .request(MediaType.APPLICATION_JSON_TYPE).delete();
    assertEquals(Status.NOT_FOUND.getStatusCode(), response2.getStatus());
  }
}
