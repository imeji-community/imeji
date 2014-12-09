package de.mpg.imeji.rest.test;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by vlad on 09.12.14.
 */
public class ItemTest extends ImejiRestTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemTest.class);
    private static final String TEST_IMAGE = "src/test/resources/storage/test.png";


    @BeforeClass
    public static void initItem() throws Exception {
        File file = new File(TEST_IMAGE);
        CollectionImeji c = ImejiFactory.newCollection();
        CollectionController controller = new CollectionController();
        controller.create(c, null, adminUser);
        Item item = ImejiFactory.newItem(c);
        item.setFilename("test item");
        ItemWithFileTO itemWithFileTo = new ItemWithFileTO();
        itemWithFileTo.setFilename("testname2");
        itemWithFileTo.setFile(file);
        itemWithFileTo.setCollectionId(c.getIdString());
        ItemService is = new ItemService();
        is.create(itemWithFileTo, adminUser);
    }


}
