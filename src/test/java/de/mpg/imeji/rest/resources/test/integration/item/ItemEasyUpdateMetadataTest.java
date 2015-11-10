package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.logic.util.Patch;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import util.JenaUtil;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;

/**
 * Test for easy Update
 * 
 * @author saquet
 *
 */
@Ignore
public class ItemEasyUpdateMetadataTest extends ItemTestBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(ItemEasyUpdateMetadataTest.class);
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
      patch.executeJSON(easyItemJSON);
      CloseableHttpResponse response = patch.getResponse();
      Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpServletResponse.SC_OK);
      String responseJSON = EntityUtils.toString(response.getEntity());
      // System.out.println(responseJSON);
      // DefaultItemTO easyTO = (DefaultItemTO) buildTOFromJSON(
      // responseJSON, DefaultItemTO.class);
      // MetadataProfileTO profileTO = new MetadataProfileTO();
      // TransferObjectFactory.transferMetadataProfile(profile, profileTO);
      // ItemTO to = new ItemTO();
      // TransferObjectFactory.transferEasyItemTOItem(profileTO, easyTO,
      // itemTO);
      // String requestJSON = RestProcessUtils.buildJSONFromObject(itemTO);
      // System.out.println(requestJSON);
      // System.out.println(responseJSON);

    } finally {
      patch.close();
    }
  }


}
