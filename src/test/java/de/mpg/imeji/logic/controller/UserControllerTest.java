package de.mpg.imeji.logic.controller;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.exceptions.AlreadyExistsException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.ConfigurationBean;
import de.mpg.j2j.helper.DateHelper;

public class UserControllerTest extends ControllerTest{

	private static final Logger LOGGER = Logger
			.getLogger(UserControllerTest.class);
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
			Assert.assertSame("error_user_already_exists", e1.getMessage());
			
//			Assert.fail("An error happened by creating the user "
//					+ e1.getMessage());
		}
	}

	@Test
	public void updateUserWithEmailAlreadyUsedByAnotherUser() {
		try {
			UserController c = new UserController(Imeji.adminUser);
			// Set Email of user2 to user
			User user = JenaUtil.testUser;
			user.setEmail(JenaUtil.TEST_USER_EMAIL_2);
			user.getPerson().setFamilyName(JenaUtil.TEST_USER_NAME);
			c.update(user, Imeji.adminUser);
			//Assert.fail("User should not be updated, since the email is already used by another user");
		} catch (Exception e1) {
			Assert.assertSame("error_user_already_exists", e1.getMessage());
		}
	}
	
	@Test
	public void createInactiveUserTest() throws ImejiException {
			UserController c = new UserController(null);
			// Create a new user with a new id but with the same email
			User user = new User();
			LOGGER.info("User object has "+user.getId());
			user.setEmail("inactiveuser@imeji.org");
			user.getPerson().setFamilyName(JenaUtil.TEST_USER_NAME);
			c.create(user, USER_TYPE.INACTIVE);
			assertTrue(!user.isActive());
	}
	
	@Test
	public void createActiveUserTestAsNoUser()  {

			UserController c = new UserController(null);
			// Create a new user with a new id but with the same email
			User user = new User();
			user.setEmail("activeuser@imeji.org");
			user.getPerson().setFamilyName(JenaUtil.TEST_USER_NAME);
			try {
				user=c.create(user, USER_TYPE.DEFAULT);
				Assert.fail("User should not be created in other state than inactive!");
			} catch (ImejiException e) {
				//Do Nothing this is fine
			}
			
	}
	
	
	
	@Test
	public void createAndActivateInactiveUserTest() throws ImejiException {
		User user = new User(); 
		user.setEmail("inactive-activate@imeji.org");
		user.getPerson().setFamilyName(JenaUtil.TEST_USER_NAME);
		UserController c = new UserController(user);
		try {
			// Create a new user with a new id but with the same email
			user=c.create(user, USER_TYPE.INACTIVE);
			assertTrue(!user.isActive());
		} catch (Exception e1) {
			Assert.fail("An error happened by cretaion an inactive User "+ e1.getMessage());
		}
		
		try {
				user=c.activate(user.getRegistrationToken()+"RR");
			
			}
		catch (NotFoundException e1) {
			//Do Nothing this is OK
			LOGGER.info("OK, invalid registration token");
		}
		catch (Exception e2) {
			Assert.fail("An error happened by activating the user with false Registration Token"	+ e2.getMessage());
		}
		
		Calendar originalCreateDate = user.getCreated();
		try {
			Calendar now = DateHelper.getCurrentDate();
			//Tests assumes registration token is valid for not more than 20 days
			//ToDo: Change to read from Configuration Bean
			now.add(Calendar.DAY_OF_MONTH, ConfigurationBean.getRegistrationTokenExpiryStatic()-5);
			user.setCreated(now);
			user=c.update(user, c.getControllerUser());
			user=c.activate(user.getRegistrationToken());
		
		}
			catch (UnprocessableError e1) {
				LOGGER.info("OK, expired registration token");
			}
			catch (Exception e2) {
			Assert.fail("An error happened by activating the user with expired Registration Token"	+ e2.getMessage());
		}
	
		user.setCreated(originalCreateDate);
		user=c.update(user, c.getControllerUser());
		
		try {
			user= c.activate(user.getRegistrationToken());
			assertTrue(user.isActive());
		
		}
		catch (Exception e1) {
			Assert.fail("An error happened by activating the user "	+ e1.getMessage());
		}
		
		try {
			
			user=c.activate(user.getRegistrationToken());
			LOGGER.info("OK, double registration!");
			Assert.fail("An error happened by activating the user again!");
		}
		catch (Exception e) {
			//Do Nothing all is fine
		}
	}
	
	
	
	@Test
	public void createAndCleanInactiveUserTest() throws ImejiException {
		String email = "clean-inactive-user@imeji.org";
		UserController c = new UserController(Imeji.adminUser);
		Calendar now = DateHelper.getCurrentDate();
		for (int i=1;i<10;i++ ){
			User user = new User(); 
			user.setEmail(email+i);
			user.getPerson().setFamilyName(JenaUtil.TEST_USER_NAME);
			user=c.create(user, USER_TYPE.INACTIVE);
			assertTrue(!user.isActive());
			Calendar originalCreateDate = user.getCreated();
				if (i<7){ 
					now.add(Calendar.DAY_OF_MONTH, ConfigurationBean.getRegistrationTokenExpiryStatic()-5);
					user.setCreated(now);
					user=c.update(user, c.getControllerUser());
				}
			}
		
		int numCleaned = c.cleanInactiveUsers();
		assertTrue(numCleaned==6);
		}
}
