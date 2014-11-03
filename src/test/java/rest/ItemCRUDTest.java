package rest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.PropertyBean;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.rest.crud.ItemCRUD;

public class ItemCRUDTest {

	private Item item;
	private User user;

	private String email = "testUser@imeji.org";
	private String name = "imeji tester";
	private String pwd = "test";

	@Before
	public void setup() {
		JenaUtil.initJena();
		initUser();
		initItem();

	}

	public void initItem() {
		CollectionImeji c = ImejiFactory.newCollection();
		MetadataProfile profile = ImejiFactory.newProfile();
		profile.setTitle("test profile");
		CollectionController controller = new CollectionController();
		ProfileController profController = new ProfileController();
		try {
			profController.create(profile, user);
			System.out.println(profile.getId());
			controller.create(c, profile.getId(), user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			c.create(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void itemCreate() {
		ItemCRUD crud = new ItemCRUD();
		crud.create(item, user);
		Assert.assertNotNull(item.getId());

	}

}
