package de.mpg.imeji.rest.resources.test.integration;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static de.mpg.imeji.rest.resources.test.TestUtils.*;

/**
 * Created by vlad on 09.12.14.
 */
public class ItemTest extends ImejiRestTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemTest.class);

    private static String collectionId;
    private static String itemId;
    private static String itemJSON;
    private static ItemTO itemTO;

    @BeforeClass
    public static void initItem() throws Exception {
        CollectionImeji c = ImejiFactory.newCollection();
        CollectionController controller = new CollectionController();
        controller.create(c, null, adminUser);
        collectionId = c.getIdString();

        //get item
        String jsonString = getStringFromPath("src/test/resources/rest/itemFull.json");

        ItemWithFileTO itemWithFileTO = (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(jsonString, ItemWithFileTO.class);

        itemWithFileTO.setFile(new File("src/test/resources/storage/test.png"));
        itemWithFileTO.setCollectionId(collectionId);
        ItemService is = new ItemService();
        itemTO =  is.create(itemWithFileTO, adminUser);
        itemJSON = RestProcessUtils.buildJSONFromTO(itemTO);
        LOGGER.info(itemJSON);
        itemId = itemTO.getId();
    }

    @Test
    public void test_1_EditItem_1_Basic() throws IOException {
/*
        Path jsonPath = Paths.get("src/test/resources/rest/editItem.json");
        String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");
        ItemWithFileTO itemWithFileTO = (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(jsonString, ItemWithFileTO.class);
*/


/*
        String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");

        Response response = target(pathPrefix)
                .register(authAsAdmin)
                .register(MultiPartFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
        Map<String, Object> collData = jsonToPOJO(response);
        assertNotNull("Created collection is null", collData);
        collId = (String)collData.get("id");
        assertThat("Empty collection id", collId, not(isEmptyOrNullString()));
*/
    }
}
