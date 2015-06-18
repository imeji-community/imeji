package de.mpg.imeji.logic.controller;


import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_STORAGE;
import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;

public class StatisticsControllerTest extends ImejiTestBase{
	
    @Test
    public void test_3_ReleaseCollection_1_WithAuth() throws ImejiException {
    	long totalFileSize =0; 
        ItemService itemStatus = new ItemService();
		initCollection();
		initItem("test");	//+1
		totalFileSize += itemStatus.read(itemId, JenaUtil.testUser).getFileSize();
        initItem("test2"); //+2
		totalFileSize += itemStatus.read(itemId, JenaUtil.testUser).getFileSize();
        initItem("test3"); //+3
        long lastAddedItemSize = itemStatus.read(itemId, JenaUtil.testUser).getFileSize();
		totalFileSize += itemStatus.read(itemId, JenaUtil.testUser).getFileSize();
        
		//deleteItem 
        Form form= new Form();
		form.param("id", itemId);
		target("/rest/items").register(authAsUser)
				.path("/" + itemId)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.delete(); //+2
		
        totalFileSize -= lastAddedItemSize;
		//init Collection with testUser2
        CollectionService cs = new CollectionService();
		try {
            collectionTO= (CollectionTO) RestProcessUtils.buildTOFromJSON(
                    getStringFromPath(STATIC_CONTEXT_REST + "/createCollection.json"), CollectionTO.class);
			collectionTO = cs.create(collectionTO, JenaUtil.testUser2);
			collectionId = collectionTO.getId();
		} catch (Exception e) {
		}
        
		//init Item with testUser2
		ItemService s = new ItemService();
		ItemWithFileTO to = new ItemWithFileTO();
		to.setCollectionId(collectionId);
		to.setFile(new File(STATIC_CONTEXT_STORAGE + "/test4.jpg"));
		to.setStatus("PENDING");
		try {
			itemTO = s.create(to, JenaUtil.testUser2); //+3
			itemId = itemTO.getId();
		} catch (Exception e) {
			System.out.println("Fail");
		}
        
        StatisticsController controller = new StatisticsController();
        long result = controller.getUsedStorageSizeForInstitute("imeji.org");

        assertEquals(itemStatus.read(itemId, JenaUtil.testUser2).getFileSize() +totalFileSize, result);

    }

}
