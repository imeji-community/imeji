package de.mpg.imeji.service.test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.j2j.exceptions.NotFoundException;

public class ItemServiceTest {

	private Item item;
	private ItemWithFileTO itemWithFileTo;
	private ItemTO itemTo;
	private User user;
	private CollectionImeji c;
	private static final String TEST_IMAGE = "./src/test/resources/storage/test.png";
	private String email = "testUser@imeji.org";
	private String name = "imeji tester";
	private String pwd = "test";

	@Before
	public void setup() throws Exception {
		JenaUtil.initJena();
		initUser();
		initItem();
	}

	@After
	public void tearDown() throws Exception {
		JenaUtil.closeJena();
	}

	public void initItem() throws Exception {
		File file = new File(TEST_IMAGE);
		c = ImejiFactory.newCollection();
		CollectionController controller = new CollectionController();
		controller.create(c, null, user);
		item = ImejiFactory.newItem(c);
		item.setFilename("test item");
		itemWithFileTo = new ItemWithFileTO();
		itemWithFileTo.setFilename("testname2");
		itemWithFileTo.setFile(file);
		itemWithFileTo.setCollectionId(c.getIdString());
	}

	public void initUser() {
		try {
			UserController c = new UserController(Imeji.adminUser);
			user = new User();
			user.setEmail(email);
			user.setName(name);
			user.setEncryptedPassword(StringHelper.convertToMD5(pwd));
			user = c.create(user, USER_TYPE.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testItemCRUD() throws NotFoundException, NotAllowedError,
			Exception {
		
		ItemService crud = new ItemService();
		// create item
		itemTo = crud.create(itemWithFileTo, user);
		// check the item be created and has new id
		assertNotNull(itemTo.getId());
		// read the item id
		assertNotNull(crud.read(itemTo.getId(), user)
				.getId());
		// check the default visibility of item = private
		assertTrue(itemTo.getVisibility().equals("PRIVATE"));
		// set new visibility = public
		itemTo.setVisibility("PUBLIC");
		// check the visibility= public unchanged
		assertTrue(itemTo.getVisibility().equals("PUBLIC"));
		// check the visibility changed
		assertFalse(itemTo.getVisibility().equals("PRIVATE"));
		// delete item
//		Assert.assertTrue(crud.delete(itemTo, user));
		// try to read the delete item
//		try {
//			crud.read(itemTo.getId(), user);
//			fail("should not found the item");
//		} catch (Exception e) {
//			// success
//			;
//		}
		
	}

}
