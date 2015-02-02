package de.mpg.imeji.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import javax.ws.rs.NotSupportedException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.exceptions.NotAllowedError;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;

public class CollectionServiceTest {


	private ItemTO itemTo;
	private static CollectionImeji collection;
	private CollectionService collService = new CollectionService();
	private static final String TEST_IMAGE = "./src/test/resources/storage/test.png";

	@BeforeClass
	public static void setup() throws Exception {
		JenaUtil.initJena();
		initCollection();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		JenaUtil.closeJena();
	}

	public static void initCollection() throws Exception {
		collection = ImejiFactory.newCollection();
		collection.getMetadata().setTitle("test collection");
		CollectionController controller = new CollectionController();
		controller.create(collection, null, JenaUtil.testUser);
	}

	@Test
	public void test_readCollection() throws Exception {

		CollectionTO collectionTO = null;
		try {
			collectionTO = collService.read(collection.getIdString(),
					JenaUtil.testUser);
		} catch (Exception e) {
			fail("could not read collection");
		}
		assertNotNull(collectionTO.getId());
		assertEquals("test collection", collectionTO.getTitle());
		
	}

	@Test
	public void test_releaseCollection() throws Exception{
		
		try {
			collService.release(collection.getIdString(), JenaUtil.testUser);
			fail("should not be allowed to release collection");
		} catch (Exception e) {

		}
		File file = null;
		file = new File(TEST_IMAGE);
		ItemService crud = new ItemService();
		ItemWithFileTO itemWithFileTo;
		itemWithFileTo = new ItemWithFileTO();
		itemWithFileTo.setFilename("testname2");
		itemWithFileTo.setFile(file);
		itemWithFileTo.setCollectionId(collection.getIdString());
		try {
			itemTo = crud.create(itemWithFileTo, JenaUtil.testUser);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			collService.release(collection.getIdString(), JenaUtil.testUser);
		} catch (NotSupportedException | NotAllowedError | NotFoundException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void test_createCollection() throws Exception{

		CollectionTO to = new CollectionTO();
		try {
			
			to = collService.create(to, JenaUtil.testUser);
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
		// check the collection be created and has new id
		assertNotNull(to.getId());
		// check the collection status
		assertTrue(to.getStatus().equals("PENDING"));
		//check the collection profile
		assertNotNull(to.getProfile());
		//check the createdDate attribute
		assertNotNull(to.getCreatedDate());
		//check the createdBy attribute
		assertNotNull(to.getCreatedBy());
		
	}
}
