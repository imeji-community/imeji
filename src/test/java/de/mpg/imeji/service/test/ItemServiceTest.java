package de.mpg.imeji.service.test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
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
	private File file = null;

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
		file = new File(TEST_IMAGE);
		c = ImejiFactory.newCollection();
		CollectionController controller = new CollectionController();
		controller.create(c, null, user);
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
		try{
			itemTo = crud.create(itemWithFileTo, null);
			fail("should not allowed to create item");
		}catch(NotAllowedError e){
			
		}
		itemTo = crud.create(itemWithFileTo, user);
		// check the item be created and has new id
		assertNotNull(itemTo.getId());
		// check the default visibility of item = private
		assertTrue(itemTo.getVisibility().equals("PRIVATE"));
		// check the item mime type
		assertTrue(itemTo.getMimetype().equals(Files.probeContentType(Paths.get(file.getName()))));
		// check the item file name
		assertTrue(itemTo.getFilename().equals(itemWithFileTo.getFilename()));
		// check the item status
		assertTrue(itemTo.getStatus().equals("PENDING"));
		
		// read the item
		assertNotNull(crud.read(itemTo.getId(), user).getId());
		assertTrue(crud.read(itemTo.getId(), user).getCollectionId().equals(c.getIdString()));
		assertTrue(crud.read(itemTo.getId(), user).getFilename().equals(itemTo.getFilename()));
		assertTrue(crud.read(itemTo.getId(), user).getChecksumMd5().equals(itemTo.getChecksumMd5()));
		assertTrue(crud.read(itemTo.getId(), user).getCreatedBy().getUserId().equals(itemTo.getCreatedBy().getUserId()));
		assertTrue(crud.read(itemTo.getId(), user).getModifiedBy().getUserId().equals(itemTo.getModifiedBy().getUserId()));	
		assertTrue(crud.read(itemTo.getId(), user).getMimetype().equals(itemTo.getMimetype()));
		assertTrue(crud.read(itemTo.getId(), user).getStatus().equals(itemTo.getStatus()));
		assertTrue(crud.read(itemTo.getId(), user).getVisibility().equals(itemTo.getVisibility()));
		assertTrue(crud.read(itemTo.getId(), user).getFilename().equals(itemTo.getFilename()));
		assertTrue(crud.read(itemTo.getId(), user).getCreatedDate().equals(itemTo.getCreatedDate()));
		assertTrue(crud.read(itemTo.getId(), user).getModifiedDate().equals(itemTo.getModifiedDate()));
		assertTrue(crud.read(itemTo.getId(), user).getWebResolutionUrlUrl().equals(itemTo.getWebResolutionUrlUrl()));
		assertTrue(crud.read(itemTo.getId(), user).getThumbnailUrl().equals(itemTo.getThumbnailUrl()));
		assertTrue(crud.read(itemTo.getId(), user).getFileUrl().equals(itemTo.getFileUrl()));
		
		// delete item
		Assert.assertTrue(crud.delete(itemTo.getId(), user));
		// try to read the delete item
		try {	
			crud.read(itemTo.getId(), user);
			fail("should not found the item");
		} catch (Exception e) {
			
		}
		
		
	}
	
}
