package de.mpg.imeji.logic.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.AlreadyExistsException;

public class UserControllerTest {
	@Before
	public void setup() {
		JenaUtil.initJena();
	}

	@After
	public void tearDown() throws Exception {
		JenaUtil.closeJena();
	}

	@Test
	public void createAlreadyExistingUserTest() {
		try {
			UserController c = new UserController(Imeji.adminUser);
			// Create a new user with a new id but with the same email
			User user = JenaUtil.testUser.clone(JenaUtil.TEST_USER_EMAIL);
			c.create(user, USER_TYPE.DEFAULT);
			Assert.fail("User should not be created, since User exists already");
		} catch (AlreadyExistsException e) {
			// everything fine
		} catch (Exception e1) {
			Assert.fail("An error happened by creating the user "
					+ e1.getMessage());
		}
	}

	@Test
	public void updateUserWithEmailAlreadyUsedByAnotherUser() {
		try {
			UserController c = new UserController(Imeji.adminUser);
			// Set Email of user2 to user
			User user = JenaUtil.testUser;
			user.setEmail(JenaUtil.TEST_USER_EMAIL_2);
			c.update(user, Imeji.adminUser);
			Assert.fail("User should not be updated, since the email is already used by another user");
		} catch (AlreadyExistsException e) {
			// everything fine
		} catch (Exception e1) {
			Assert.fail("An error happened by updating the user "
					+ e1.getMessage());
		}
	}
}
