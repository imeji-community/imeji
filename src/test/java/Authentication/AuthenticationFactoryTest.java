package Authentication;



import org.junit.After;
import org.junit.Before;

import util.JenaUtil;
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
		// TODO Write test
		//JenaUtil.addUser("test@imeji.org", null, "test");
	}

	@After
	public void tearDown() throws Exception {
		JenaUtil.closeJena();
	}

//	@Test
//	public void testFactory() {
//		Authentication ob = AuthenticationFactory.factory(login, pwd);
//		Assert.assertNotNull(ob.doLogin());
//		
//
//	}

}
