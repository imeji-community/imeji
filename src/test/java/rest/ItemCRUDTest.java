package rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;


import junit.framework.Assert;


import org.junit.Before;
import org.junit.Test;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.crud.ItemCRUD;

public class ItemCRUDTest {

	private Item item;
	private User user;

	private String email = "testUser@imeji.org";
	private String name = "imeji tester";
	private String pwd = "test";

	@Before
	public void setup() throws Exception {
//		JenaUtil.initJena();
		initUser();
		initItem();
	}

	public void initItem() throws Exception {
		CollectionImeji c = ImejiFactory.newCollection();
		CollectionController controller = new CollectionController();
		controller.create(c, null, user);
		item = ImejiFactory.newItem(c);
		item.setFilename("test item");
		item.setCreated(Calendar.getInstance());
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
	public void testItemCRUND() {
		
		ItemCRUD crud = new ItemCRUD();
		// create item
		crud.create(item, user);
		// check the item be created and has new id
		Assert.assertNotNull(item.getId());
		// read the item id
		Assert.assertNotNull(crud.read(item, user).getId());
		// check the default visibility of item = private
		assertTrue(item.getVisibility().equals(Visibility.PRIVATE));
		//set new visibility = public
		item.setVisibility(Visibility.PUBLIC);
		// check the visibility= public unchanged
		assertTrue(item.getVisibility().equals(Visibility.PUBLIC));
		// check the visibility changed
		assertFalse(item.getVisibility().equals(Visibility.PRIVATE));
		// delete item
		Assert.assertTrue(crud.delete(item, user));
		// try to read the delete item
		try{
			crud.read(item, user);
			fail("should not found the item");
		}catch(RuntimeException e){
			assertTrue(e.getMessage().contains("Error reading item"));;
		}
		
	}

}
