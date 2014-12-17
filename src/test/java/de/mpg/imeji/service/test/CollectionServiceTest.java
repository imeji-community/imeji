package de.mpg.imeji.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import javax.validation.constraints.AssertTrue;
import javax.ws.rs.NotSupportedException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.to.CollectionProfileTO;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.service.test.ItemServiceTest.*;
import de.mpg.j2j.exceptions.NotFoundException;
import util.JenaUtil;

public class CollectionServiceTest {

	private User user;
	private UserController c = new UserController(Imeji.adminUser);
	private String email = "testUser@imeji.org";
	private String name = "imeji tester";
	private String pwd = "test";
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
	public void test_createCollection() throws InterruptedException{
		CollectionTO collectionTO = null;
		try {
			collectionTO = collService.read(collection.getIdString(),
					JenaUtil.testUser);
			CollectionProfileTO profile = new CollectionProfileTO();
			profile.setMethod("copy");
			collectionTO.setProfile(profile);
		} catch (Exception e) {
			fail("could not read collection");
		}
		JenaUtil.closeJena();
		JenaUtil.initJena();
		try {
			collService.create(collectionTO, JenaUtil.testUser);
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
		
	}
}
