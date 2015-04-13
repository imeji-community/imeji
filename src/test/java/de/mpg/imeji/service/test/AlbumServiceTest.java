package de.mpg.imeji.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.rest.api.AlbumService;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE;
import de.mpg.imeji.rest.to.AlbumTO;





public class AlbumServiceTest {

	private static Album vo = new Album();
	private static AlbumService service = new AlbumService();
	private static Logger logger = Logger.getLogger(AlbumServiceTest.class);

	@BeforeClass
	public static void setup() throws Exception {
		JenaUtil.initJena();
		initAlbum();
	}

	
	@AfterClass
	public static void tearDown() throws Exception {
		JenaUtil.closeJena();
	}
	
	public static void initAlbum(){
		try {
			Path jsonPath = Paths
					.get("src/test/resources/rest/createAlbum.json");
			String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");
			
			AlbumTO to = (AlbumTO) RestProcessUtils.buildTOFromJSON(jsonString, AlbumTO.class);
			to = service.create(to, JenaUtil.testUser);
		
			ReverseTransferObjectFactory.transferAlbum(to, vo, TRANSFER_MODE.CREATE, JenaUtil.testUser);
			vo.setId(URI.create(to.getId()));
			
			System.out.println("IdString= "+ vo.getIdString());
		} catch (Exception e) {
			logger.error("Cannot init Album", e);
		}
	}
	
	@Test
	public void test_readAlbum() throws Exception {

		AlbumTO to = null;
		try {
			to = service.read(vo.getIdString(),
					JenaUtil.testUser);
		} catch (Exception e) {
			fail("could not read album "+ vo.getIdString());
		}
		assertNotNull(to.getId());
	}
	
	@Test
	public void test_createAlbum() throws Exception{

		AlbumTO to = new AlbumTO();
		to.setTitle("Test");
		try {			
			to = service.createNoValidate(to, JenaUtil.testUser);
		} catch (Exception e) {
			fail();
			logger.error("test_createAlbum", e);
		}
		// check the album be created and has new id
		assertNotNull(to.getId());
		// check the album status
		assertTrue(to.getStatus().equals("PENDING"));
		//check the createdDate attribute
		assertNotNull(to.getCreatedDate());
		//check the createdBy attribute
		assertNotNull(to.getCreatedBy());
		
	}

}
