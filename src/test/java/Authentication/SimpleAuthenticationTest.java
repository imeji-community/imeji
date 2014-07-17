package Authentication;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.logic.auth.authentication.SimpleAuthentication;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.PropertyBean;


public class SimpleAuthenticationTest {
	private SimpleAuthentication simpAuth;
	private String emailTestUser ="test@imeji.org";
	private String nameTestUser = "test user";
	private String pwdTestUser = "test";
	

	private String emailTestUser2 ="abcd@imeji.org";
	private String nameTestUser2 = "test user abcd";
	private String pwdTestUser2 = "abcd";
	
	private User testUser;
	private User testUser2;

	@Before
	public void setup() {
		new PropertyBean();
		JenaUtil.initJena();
		JenaUtil.addUser(emailTestUser, nameTestUser, pwdTestUser);
		JenaUtil.addUser(emailTestUser2, nameTestUser2, pwdTestUser2);
	}

	@After
	public void tearDown() throws Exception {
	}



	@Test
	public void testDoLogin() {
		
		simpAuth = new SimpleAuthentication("test@imeji.org", "test"); // test user with right login and password
		testUser = simpAuth.doLogin();
		Assert.assertNotNull(testUser); // test user as imeji user should be returned
		
		simpAuth = new SimpleAuthentication("abcd@imeji.org", "abcd"); // test user 2 with right login and password
		testUser2 = simpAuth.doLogin();
		Assert.assertNotNull(testUser2); // test user 2 as imeji user should be returned
		
		simpAuth = new SimpleAuthentication("test@imeji.org", "abcd"); //  login of test user and password of test user 2
		testUser2 = simpAuth.doLogin();
		Assert.assertNull(testUser2); // value null should be returned
		
		simpAuth = new SimpleAuthentication("abcd@imeji.org", "test"); //  login of test user 2 and password of test user
		testUser2 = simpAuth.doLogin();
		Assert.assertNull(testUser2); // value null should be returned
		
		simpAuth = new SimpleAuthentication("test@imeji.org","blabla"); // test user with right login and wrong password
		testUser = simpAuth.doLogin();
		Assert.assertNull(testUser);	// value null should be returned
		
		simpAuth = new SimpleAuthentication("123@imeji.org", "test");  // test user with wrong login and right password
		testUser = simpAuth.doLogin();
		Assert.assertNull(testUser);	// value null should be returned
		
		simpAuth = new SimpleAuthentication("123456789", "123456789"); // test user with wrong login and password
		testUser = simpAuth.doLogin();
		Assert.assertNull(testUser);  // value null should be returned
		
		simpAuth = new SimpleAuthentication("!§$%&/().,-#+*", "!§$%&/().,-#+*"); // test user with special characters
		testUser = simpAuth.doLogin();
		Assert.assertNull(testUser);  // value null should be returned
	}


}
