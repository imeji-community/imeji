package de.mpg.imeji.testimpl.rest.resources.item;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.test.rest.resources.test.integration.ItemTestBase;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemUpdateRaw extends ItemTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemUpdateRaw.class);

  protected static String updateItemJSON;

  private static final String PATH_PREFIX = "/rest/items";
  public static final String METADATA_KEY = "metadata";
  public static final String referenceUrl = "http://imeji.org";

  ItemTO defaultItemTO;


  @BeforeClass
  public static void specificSetup() throws Exception {

    updateItemJSON = getStringFromPath("src/test/resources/rest/updateItemBasic.json");
    initCollection();
    initItem();
  }
  


  @Test
  public void test_1_updateItem_updateMetadata_empty() throws IOException,
      BadRequestException {
    FormDataMultiPart multiPart = new FormDataMultiPart();
    Response response = getTargetAuth().get();
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    String json = response.readEntity(String.class);
    
    LOGGER.info(json);
    
    multiPart.field("json", json.
        replace("collectionId.*$", collectionId+"\" }") );

    response = getTargetAuth().put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    
  }


  @Test
  public void test_3_updateItem_rawSyntax_BAD_REQUEST() throws IOException, BadRequestException {

    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field("json", updateItemJSON.replace("___COLLECTION_ID___", "\"" +collectionId));

    Response response =
        getTargetAuth().put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

  }

  
 

  private Invocation.Builder getTargetAuth() {
    return target(PATH_PREFIX).path("/"+itemId).queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase()).register(authAsUser).register(MultiPartFeature.class)
        .register(JacksonFeature.class).request(MediaType.APPLICATION_JSON_TYPE);
  }
  

}
