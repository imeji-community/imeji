package de.mpg.imeji.rest.resources.test.integration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import util.JenaUtil;
import de.mpg.imeji.rest.MyApplication;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.MetadataProfileTO;

/**
 * Created by vlad on 09.12.14.
 */
public class ImejiTestBase extends JerseyTest {

	protected static HttpAuthenticationFeature authAsUser = HttpAuthenticationFeature
			.basic(JenaUtil.TEST_USER_EMAIL, JenaUtil.TEST_USER_PWD);
	protected static HttpAuthenticationFeature authAsUser2 = HttpAuthenticationFeature
			.basic(JenaUtil.TEST_USER_EMAIL_2, JenaUtil.TEST_USER_PWD);

	protected static String collectionId;
	protected static String profileId;
	protected static String itemId;
	protected static CollectionTO collectionTO;
	protected static ItemTO itemTO;

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

	/**
	 * Create a profile
	 */
	public static void initProfile() {
		try {
			ProfileService s = new ProfileService();
			profileId = s.create(new MetadataProfileTO(), JenaUtil.testUser)
					.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Create a new collection and set the collectionid
	 * 
	 * @throws Exception
	 */
	public static void initCollection() {
		CollectionService s = new CollectionService();
		try {
			collectionTO = s.create(new CollectionTO(), JenaUtil.testUser);
			collectionId = collectionTO.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create an item in the test collection (initCollection must be called
	 * before)
	 * 
	 * @throws Exception
	 */
	public static void initItem() {
		ItemService s = new ItemService();
		ItemWithFileTO to = new ItemWithFileTO();
		to.setCollectionId(collectionId);
		to.setFile(new File("src/test/resources/storage/test.png"));
		to.setStatus("PENDING");
		try {
			itemTO = s.create(to, JenaUtil.testUser);
			itemId = itemTO.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
