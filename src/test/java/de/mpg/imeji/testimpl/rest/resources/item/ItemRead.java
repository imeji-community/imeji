package de.mpg.imeji.testimpl.rest.resources.item;

import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.test.rest.resources.test.integration.ImejiTestBase;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JenaUtil;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static de.mpg.imeji.rest.process.RestProcessUtils.jsonToPOJO;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ItemRead extends ImejiTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemRead.class);

  private static final String PATH_PREFIX = "/rest/items";

  @Before
  public void specificSetup() {
    initCollection();
    initItem();
  }

  @Test
  public void test_1_ReadItem_Default() throws Exception {

    Response response =
        (target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser)
            .register(MultiPartFeature.class).request(MediaType.APPLICATION_JSON_TYPE)).get();
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    Map<String, Object> itemData = jsonToPOJO(response);
    assertEquals(itemId, (String) itemData.get("id"));
  }

  @Test
  public void test_2_ReadItem_Unauthorized() throws IOException {
    Response response =
        (target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
            .register(MultiPartFeature.class).request(MediaType.APPLICATION_JSON_TYPE)).get();
    assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    Response response2 =
        (target(PATH_PREFIX).path("/" + itemId).register(authAsUser2)
            .register(MultiPartFeature.class).request(MediaType.APPLICATION_JSON_TYPE)).get();
    assertEquals(Status.FORBIDDEN.getStatusCode(), response2.getStatus());

  }


  @Test
  public void test_3_ReadItem_Forbidden() throws IOException {

    Response response2 =
        (target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser2)
            .register(MultiPartFeature.class).request(MediaType.APPLICATION_JSON_TYPE)).get();
    assertEquals(Status.FORBIDDEN.getStatusCode(), response2.getStatus());

  }


  @Test
  public void test_4_ReadItem_InReleaseCollection() throws Exception {
    CollectionService s = new CollectionService();
    s.release(collectionId, JenaUtil.testUser);
    assertEquals("RELEASED", s.read(collectionId, JenaUtil.testUser).getStatus());

    Response response =
        (target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
            .register(MultiPartFeature.class).request(MediaType.APPLICATION_JSON_TYPE)).get();
    assertEquals(Status.OK.getStatusCode(), response.getStatus());

    Response response2 =
        (target(PATH_PREFIX).path("/" + itemId).register(authAsUser2)
            .register(MultiPartFeature.class).request(MediaType.APPLICATION_JSON_TYPE)).get();
    assertEquals(Status.OK.getStatusCode(), response2.getStatus());
  }

  @Test
  public void test_5_ReadItem_InWithDrawnCollection() throws Exception {
    CollectionService s = new CollectionService();
    s.release(collectionId, JenaUtil.testUser);
    assertEquals("RELEASED", s.read(collectionId, JenaUtil.testUser).getStatus());
    s.withdraw(collectionId, JenaUtil.testUser,
        "test_5_ReadItem_InWithDrawnCollection_" + System.currentTimeMillis());

    assertEquals("WITHDRAWN", s.read(collectionId, JenaUtil.testUser).getStatus());

    Response response =
        (target(PATH_PREFIX).path("/" + itemId)
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser)
            .register(MultiPartFeature.class).request(MediaType.APPLICATION_JSON_TYPE)).get();

    assertEquals(Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  public void test_6_ReadItem_NotFound() throws Exception {

    Response response =
        (target(PATH_PREFIX).path("/" + itemId + "_not_exist_item")
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser)
            .register(MultiPartFeature.class).request(MediaType.APPLICATION_JSON_TYPE)).get();

    assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  public void test_7_ReadItemsWithQuery() throws Exception {
    Response response =
        (target(PATH_PREFIX).queryParam("q", itemTO.getFilename())
            .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser)
            .register(MultiPartFeature.class).request(MediaType.APPLICATION_JSON_TYPE)).get();

    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    List<ItemTO> itemList =
        RestProcessUtils.buildTOListFromJSON(response.readEntity(String.class), ItemTO.class);
    assertThat(itemList, not(empty()));
    assertThat(itemList.get(0).getFilename(), equalTo(itemTO.getFilename()));
  }


}
