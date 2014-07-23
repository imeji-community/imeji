package Authentication;



import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.logic.auth.Authentication;
import de.mpg.imeji.logic.auth.AuthenticationFactory;
import de.mpg.imeji.logic.auth.authentication.SimpleAuthentication;
import de.mpg.imeji.presentation.beans.PropertyBean;



public class AuthenticationFactoryTest {
	
	private String login = "test@imeji.org";
	private String pwd = "test";
	private SimpleAuthentication simp;
	@Before
	public void setUp() throws Exception {
		new PropertyBean();
		JenaUtil.initJena();
		JenaUtil.addUser("test@imeji.org", null, "test");
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void testFactory() {
//		Authentication ob = AuthenticationFactory.factory(login, pwd);
//		Assert.assertNotNull(ob.doLogin());
//		
//
//	}

}
