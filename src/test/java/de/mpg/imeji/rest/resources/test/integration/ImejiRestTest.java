package de.mpg.imeji.rest.resources.test.integration;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.MyApplication;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import util.JenaUtil;

import javax.ws.rs.core.Application;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by vlad on 09.12.14.
 */
public class ImejiRestTest extends JerseyTest {

	protected static HttpAuthenticationFeature authAsUser = HttpAuthenticationFeature
			.basic(JenaUtil.TEST_USER_EMAIL, JenaUtil.TEST_USER_PWD);

	@Override
	protected Application configure() {
		return new MyApplication();
	}

	@Override
	protected TestContainerFactory getTestContainerFactory()
			throws TestContainerException {
		return new MyTestContainerFactory();
	}

	@BeforeClass
	public static void setup() throws IOException, URISyntaxException {
		JenaUtil.initJena();
	}

	@AfterClass
	public static void shutdown() throws IOException, URISyntaxException,
			InterruptedException {
		JenaUtil.closeJena();
	}

	public static String initProfile() {
		try {
			ProfileController c = new ProfileController();
			MetadataProfile p = c.create(ImejiFactory.newProfile(),
					JenaUtil.testUser);
			return p.getId().toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
