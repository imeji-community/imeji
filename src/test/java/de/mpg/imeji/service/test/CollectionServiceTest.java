package de.mpg.imeji.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.NotSupportedException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.exceptions.NotAllowedError;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;

public class CollectionServiceTest {


	private ItemTO itemTo;
	private static CollectionImeji collection = new CollectionImeji();
	private static CollectionService collService = new CollectionService();
	private static final String TEST_IMAGE = "./src/test/resources/storage/test.png";
	private static Logger logger = Logger.getLogger(CollectionServiceTest.class);

	@BeforeClass
	public static void setup() throws Exception {
		JenaUtil.initJena();
		initCollection();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		JenaUtil.closeJena();
	}

	public static void initCollection()  {
		
		try {
			Path jsonPath = Paths
					.get("src/test/resources/rest/createCollection.json");
			String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");
			
			CollectionTO collectionTO= (CollectionTO) RestProcessUtils.buildTOFromJSON(jsonString, CollectionTO.class);
			collectionTO = collService.create(collectionTO, JenaUtil.testUser);
		
			ReverseTransferObjectFactory.transferCollection(collectionTO, collection, TRANSFER_MODE.CREATE);
			collection.setId(URI.create(collectionTO.getId()));
			
			System.out.println("IdString= "+collection.getIdString());
		} catch (Exception e) {
			logger.error("Cannot init Collection", e);
		}
	}

	@Test
	public void test_readCollection() throws Exception {

		CollectionTO collectionTO = null;
		try {
			collectionTO = collService.read(collection.getIdString(),
					JenaUtil.testUser);
		} catch (Exception e) {
			fail("could not read collection "+collection.getIdString());
		}
		assertNotNull(collectionTO.getId());
	}

	@Test
	public void test_releaseCollection() throws Exception{
		
		try {
			collService.release(collection.getIdString(), JenaUtil.testUser);
			fail("should not be allowed to release collection");
		} catch (Exception e) {

		}
		File file = null;
		file = new File(TEST_IMAGE);
		ItemService crud = new ItemService();
		ItemWithFileTO itemWithFileTo;
		itemWithFileTo = new ItemWithFileTO();
		itemWithFileTo.setFilename("testname2");
		itemWithFileTo.setFile(file);
		itemWithFileTo.setCollectionId(collection.getIdString());
		try {
			itemTo = crud.create(itemWithFileTo, JenaUtil.testUser);
		} catch (Exception e1) {
			logger.error("test_releaseCollection, can not create item", e1);
		}

		try {
			collService.release(collection.getIdString(), JenaUtil.testUser);
		} catch (NotSupportedException | NotAllowedError | NotFoundException e) {
			logger.error("test_releaseCollection", e);

		}

	}
	
	@Test
	public void test_createCollection() throws Exception{

		CollectionTO to = new CollectionTO();
		try {
			
			to = collService.createNoValidate(to, JenaUtil.testUser);
		} catch (Exception e) {
			fail();
			logger.error("test_createCollectionCollection", e);
		}
		// check the collection be created and has new id
		assertNotNull(to.getId());
		// check the collection status
		assertTrue(to.getStatus().equals("PENDING"));
		//check the collection profile
		assertNotNull(to.getProfile());
		//check the createdDate attribute
		assertNotNull(to.getCreatedDate());
		//check the createdBy attribute
		assertNotNull(to.getCreatedBy());
		
	}
}
