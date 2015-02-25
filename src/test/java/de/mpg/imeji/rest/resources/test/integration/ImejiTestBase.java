package de.mpg.imeji.rest.resources.test.integration;

import de.mpg.imeji.rest.MyApplication;
import de.mpg.imeji.rest.api.AlbumService;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.AlbumTO;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import util.JenaUtil;

import javax.ws.rs.core.Application;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;

/**
 * Created by vlad on 09.12.14.
 */
public class ImejiTestBase extends JerseyTest {

	protected static HttpAuthenticationFeature authAsUser = HttpAuthenticationFeature
			.basic(JenaUtil.TEST_USER_EMAIL, JenaUtil.TEST_USER_PWD);
	protected static HttpAuthenticationFeature authAsUser2 = HttpAuthenticationFeature
			.basic(JenaUtil.TEST_USER_EMAIL_2, JenaUtil.TEST_USER_PWD);

	protected static String collectionId;
	protected static String albumId;
	protected static String profileId;
	protected static String itemId;
	protected static CollectionTO collectionTO;
	protected static AlbumTO albumTO;
	protected static ItemTO itemTO;
	
	private static Logger logger = Logger.getLogger(ImejiTestBase.class);

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
			logger.error("Cannot init profile", e);
		}
	}


	/**
	 * Create a new collection and set the collectionid
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * 
	 * @throws Exception
	 */
	public static void initCollection()  {
		CollectionService s = new CollectionService();
		try {
            collectionTO= (CollectionTO) RestProcessUtils.buildTOFromJSON(
                    getStringFromPath(STATIC_CONTEXT_REST + "/createCollection.json"), CollectionTO.class);
			collectionTO = s.create(collectionTO, JenaUtil.testUser);
			collectionId = collectionTO.getId();
		} catch (Exception e) {
			logger.error("Cannot init Collection", e);
		}
	}

	/**
	 * Create a new album and set the albumid
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * 
	 * @throws Exception
	 */
	public static void initAlbum(){
		AlbumService s = new AlbumService();
		try{
            albumTO = (AlbumTO) RestProcessUtils.buildTOFromJSON(
                    getStringFromPath(STATIC_CONTEXT_REST + "/createAlbum.json"), AlbumTO.class);
			albumTO = s.create(albumTO, JenaUtil.testUser);
			albumId = albumTO.getId();
			
		}catch (Exception e) {
			logger.error("Cannot init Album", e);
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
			logger.error("Cannot init Item", e);

		}
	}
	

}
