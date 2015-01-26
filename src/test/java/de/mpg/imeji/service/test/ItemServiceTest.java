package de.mpg.imeji.service.test;

import de.mpg.imeji.logic.auth.exception.AuthenticationError;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.to.*;
import de.mpg.imeji.rest.to.predefinedMetadataTO.TextTO;
import de.mpg.j2j.exceptions.NotFoundException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import util.JenaUtil;

import java.io.File;
import java.net.URI;


import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ItemServiceTest {

	private static final String TEST_IMAGE_FILE_PATH = "src/test/resources/storage/test.png";
	private static File file = null;
	private static CollectionTO collectionTO;
	private static String collectionId;
	private static ItemWithFileTO itemTO;

	@BeforeClass
	public static void setup() throws Exception {
		JenaUtil.initJena();
		initCollection();
		initItem();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		JenaUtil.closeJena();
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

	public static void initItem() throws Exception {

		itemTO = new ItemWithFileTO();
		itemTO.setFilename("testname2");
		itemTO.setCollectionId(collectionId);

		file = new File(TEST_IMAGE_FILE_PATH);

		itemTO.setFile(file);

		TextTO text = new TextTO();
		text.setText("kuku moj mal4ik");

		LabelTO label = new LabelTO("en", "text label");

		MetadataSetTO mds = new MetadataSetTO();
		mds.setValue(text);
		mds.setTypeUri(URI.create("http://imeji.org/terms/metadata#text"));
		mds.getLabels().add(label);

		itemTO.getMetadata().add(mds);

	}

	@Test
	public void testItemCRUD() throws NotFoundException, NotAllowedError,
			AuthenticationError, Exception {

		ItemService crud = new ItemService();
		// create item
		try {
			crud.create(itemTO, null);
			fail("You have to be authenticated");
		} catch (Exception e) {
			// its everything fine
		}

		ItemTO createdItem = crud.create(itemTO, JenaUtil.testUser);
		// check the item be created and has new id
		assertNotNull(createdItem.getId());

		assertEquals(createdItem.getCollectionId(), collectionId);
		// check the default visibility of item = private
		assertTrue(createdItem.getVisibility().equals("PRIVATE"));
		// check the item mime type
		assertEquals(createdItem.getMimetype(), StorageUtils.getMimeType(file));
		// check the item file name
		assertEquals(createdItem.getFilename(), itemTO.getFilename());
		// check the item status
		assertTrue(createdItem.getStatus().equals("PENDING"));


		// read the item
		ItemTO readItem = crud.read(createdItem.getId(), JenaUtil.testUser);

		assertNotNull(readItem.getId());
		assertEquals(readItem.getCollectionId(), collectionId);

		assertEquals(readItem.getFilename(), createdItem.getFilename());
		assertEquals(readItem.getChecksumMd5(), (createdItem.getChecksumMd5()));
		assertEquals(readItem.getCreatedBy().getFullname(), createdItem.getCreatedBy().getFullname());
		assertEquals(readItem.getModifiedBy().getFullname(), createdItem.getModifiedBy().getFullname());
		assertEquals(readItem.getCreatedBy().getUserId(), createdItem.getCreatedBy().getUserId());
		assertEquals(readItem.getModifiedBy().getUserId(), createdItem.getModifiedBy().getUserId());
		assertEquals(readItem.getMimetype(), createdItem.getMimetype());
		assertEquals(readItem.getStatus(), createdItem.getStatus());
		assertEquals(readItem.getVisibility(), createdItem.getVisibility());
		assertEquals(readItem.getFilename(), createdItem.getFilename());
		assertEquals(readItem.getCreatedDate(), (createdItem.getCreatedDate()));
		assertEquals(readItem.getModifiedDate(), (createdItem.getModifiedDate()));
		assertEquals(readItem.getWebResolutionUrlUrl(), (createdItem.getWebResolutionUrlUrl()));
		assertEquals(readItem.getThumbnailUrl(), (createdItem.getThumbnailUrl()));
		assertEquals(readItem.getFileUrl(), createdItem.getFileUrl());

		// delete item
		Assert.assertTrue(crud.delete(readItem.getId(), JenaUtil.testUser));

		// try to read the delete item
		try {
			readItem = crud.read(createdItem.getId(), JenaUtil.testUser);
			fail("should not found the item");
		} catch (Exception e) {
			//is OK
		}

		}

	@Test
	public void updateItemTest() throws Exception {

		ItemService is = new ItemService();
		ItemTO itemTo = is.create(itemTO, JenaUtil.testUser);
		
		
		// update item with new file
		String uploadFilePath = "src/test/resources/storage/test.jpg";
		File uploadFile = new File(uploadFilePath);

		itemTO.setId(itemTo.getId());
		itemTO.setFile(uploadFile);
		itemTo = is.update(itemTO, JenaUtil.testUser);
		
	
	
		assertEquals(is.read(itemTo.getId(), JenaUtil.testUser).getChecksumMd5(),
				itemTo.getChecksumMd5());
		assertEquals(StorageUtils.calculateChecksum(uploadFile),
				itemTo.getChecksumMd5());
		
		
		
		//update item with new fetch url
		String url = "http://imeji.org/wp-content/uploads/2014/11/imeji_opening_can.png";
		itemTO.setFetchUrl(url);
		itemTO.setFile(null);
		itemTo = is.update(itemTO, JenaUtil.testUser);
		assertThat(StorageUtils.calculateChecksum(uploadFile), is(not(itemTo.getChecksumMd5())));
		assertThat(StorageUtils.getMimeType(uploadFile),is(not(itemTo.getMimetype())));
		
		//update item with new referenced url
		itemTO.setFetchUrl(null);
		itemTO.setFile(null);
		itemTO.setReferenceUrl(url);
		itemTo = is.update(itemTO, JenaUtil.testUser);
		assertEquals(url, itemTo.getFileUrl().toString());
		assertThat(itemTo.getThumbnailUrl().toString(), endsWith(ItemController.NO_THUMBNAIL_FILE_NAME));
		assertThat(itemTo.getWebResolutionUrlUrl().toString(), endsWith(ItemController.NO_THUMBNAIL_FILE_NAME));

		//update item with new file and new fetch url, the new file should be  updated, not though the fetch url
		itemTO.setFile(uploadFile);
		itemTO.setFetchUrl(url);
		itemTo = is.update(itemTO, JenaUtil.testUser);

		assertEquals(StorageUtils.calculateChecksum(uploadFile),
				itemTo.getChecksumMd5());
		assertEquals(is.read(itemTo.getId(), JenaUtil.testUser).getChecksumMd5(),
				StorageUtils.calculateChecksum(uploadFile));
//		assertThat("the thumbnail url does not updated", itemTo.getThumbnailUrl().toString(), is(not("NO_THUMBNAIL_URL")));
//		assertThat("the web image url does not updated", itemTo.getThumbnailUrl().toString(), is(not("NO_WEBIMAGE_URL")));
		
		//update item with new file and new referenced url, the new file should be  updated, not though the referenced url 
		itemTO.setFile(uploadFile);
		itemTO.setReferenceUrl(url);
		itemTo = is.update(itemTO, JenaUtil.testUser);
		assertEquals(is.read(itemTo.getId(), JenaUtil.testUser).getChecksumMd5(),
				StorageUtils.calculateChecksum(uploadFile));
		

		// change the file name
		String updateFileName = "updateFileName.png";
		itemTo.setFilename(updateFileName);
		is.update(itemTo, JenaUtil.testUser);
		assertEquals(is.read(itemTo.getId(), JenaUtil.testUser).getFilename(),
				itemTo.getFilename());

	}

}
