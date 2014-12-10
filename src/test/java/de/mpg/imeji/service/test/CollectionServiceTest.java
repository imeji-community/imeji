package de.mpg.imeji.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.validation.constraints.AssertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.service.test.ItemServiceTest.*;
import util.JenaUtil;

public class CollectionServiceTest {

	private User user;
	private String email = "testUser@imeji.org";
	private String name = "imeji tester";
	private String pwd = "test";
	private CollectionImeji collection;
	
	@Before
	public void setup() throws Exception {
		JenaUtil.initJena();
		initUser();
		initCollection();
	}

	@After
	public void tearDown() throws Exception {
		JenaUtil.closeJena();
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
	
	public void initCollection() throws Exception{
		collection = ImejiFactory.newCollection();
		collection.getMetadata().setTitle("test collection");
		CollectionController controller = new CollectionController();
		controller.create(collection, null, user);
	}
	
	@Test
	public void testCollectionCRUD(){
		
		CollectionService collcrud = new CollectionService();
		CollectionTO collectionTO = null;
		try {
			collectionTO = collcrud.read(collection.getIdString(), user);
		}catch (Exception e) {
			fail("could not read collection");
		}
		assertNotNull(collectionTO.getId());
		assertEquals("test collection",collectionTO.getTitle());
	}
	
}
