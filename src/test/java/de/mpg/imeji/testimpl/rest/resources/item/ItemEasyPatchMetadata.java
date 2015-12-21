package de.mpg.imeji.testimpl.rest.resources.item;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.test.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.logic.util.Patch;
import de.mpg.imeji.test.rest.resources.test.integration.ItemTestBase;

/**
 * Test for easy Update
 * 
 * @author saquet
 *
 */
@Ignore
public class ItemEasyPatchMetadata extends ItemTestBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(ItemEasyPatchMetadata.class);
  private static String easyItemJSON;
  private static final String ITEM_SERVICE_ADDRESS = "http://localhost:9998/rest/items/";

  @BeforeClass
  public static void specificSetup() throws Exception {
    easyItemJSON = getStringFromPath(STATIC_CONTEXT_REST + "/easyUpdateItemBasic.json");
    initCollectionWithProfile(getDefaultBasicStatements());
    createItem();
  }

  @Test
  public void test_1_simpleEdit() throws Exception {
    Patch patch =
        new Patch(URI.create(ITEM_SERVICE_ADDRESS + itemId), JenaUtil.TEST_USER_EMAIL,
            JenaUtil.TEST_USER_PWD);
    try {
      String json = easyItemJSON.replace("___ITEM_ID___",itemId).replace("___COLLECTION_ID___", collectionId).replace("___FILE_NAME___", "patchedFileName");
      patch.executeJSON(json);
      CloseableHttpResponse response = patch.getResponse();
      Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_OK);
//    String responseJSON = EntityUtils.toString(response.getEntity());
    } finally {
      patch.close();
    }
  }


}
