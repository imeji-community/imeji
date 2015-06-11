package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.exceptions.BadRequestException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;

public class ItemEasyUpdateTest extends ItemTestBase{
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemEasyUpdateTest.class);
	
	private static String updateJSON;
    private static final String PATH_PREFIX = "/rest/items";
	private static String EASY_UPDATE_ITEM_JSON = STATIC_CONTEXT_REST + "/easyUpdateItem.json";
	
	@BeforeClass
	public static void specificSetup() throws Exception
	{
		initCollection();
		initItem();
		updateJSON = getStringFromPath(EASY_UPDATE_ITEM_JSON);
	}
	
    @Test
    public void test_1_EasyUpdateItem() throws IOException, BadRequestException {
//    	Response response = target(PATH_PREFIX).path("/" + itemId)
//              .register(authAsUser)
//              .register(MultiPartFeature.class)
//              .request(MediaType.APPLICATION_JSON_TYPE)
//              .put(Entity.entity(updateJSON, MediaType.APPLICATION_JSON_TYPE));
//
////    	ItemTO updatedItem = response.readEntity(ItemWithFileTO.class);
//
//        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

}
