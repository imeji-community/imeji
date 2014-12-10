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
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import de.mpg.j2j.exceptions.NotFoundException;

public class ProfileServiceTest {

	private User user;
	private CollectionImeji c;
	private MetadataProfile p;
	private String email = "testUser@imeji.org";
	private String name = "imeji tester";
	private String pwd = "test";

	@Before
	public void setup() throws Exception {
		JenaUtil.initJena();
		initUser();
		initProfile();
	}

	@After
	public void tearDown() throws Exception {
		JenaUtil.closeJena();
	}

	public void initProfile() throws Exception {
		CollectionController controller = new CollectionController();
		ProfileController pController = new ProfileController();
		
		c = ImejiFactory.newCollection();
		p = ImejiFactory.newProfile();
		
		c.getMetadata().setTitle("test collection");
		c.setProfile(p.getId());
		pController.create(p, user);
		controller.create(c, p, user);
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
	public void testProfileCRUD() throws NotFoundException, NotAllowedError,
			Exception {
		CollectionService collcrud = new CollectionService();
		ProfileService pCrud = new ProfileService();
		MetadataProfileTO profile = new MetadataProfileTO();
		try {
			System.out.println("collection.getProfile: "+c.getProfile());
			
			profile = pCrud.read(p.getIdString(), user);
			System.out.println("profile.getID: "+profile.getId());
			
			
		}catch (Exception e) {
			fail("could not read Profile");
		}
		assertNotNull(profile.getId());
		assertEquals(c.getProfile().toString().split("/")[5], profile.getId());
		
		
	}

}
