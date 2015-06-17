package de.mpg.imeji.logic.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.util.ImejiFactory;

/**
 * Unit Tests for the {@link ItemController}
 * 
 * @author bastiens
 * 
 */
public class ItemControllerTest extends ControllerTest {
	private static Item item;
	private static final Logger logger = Logger
			.getLogger(ItemControllerTest.class);
	private static File originalFile = new File(
			"src/test/resources/storage/test.jpg");
	private static File thumbnailFile = new File(
			"src/test/resources/storage/test.png");

	@BeforeClass
	public static void specificSetup() {
		try {
			createCollection();
			createItem();
		} catch (ImejiException e) {
			logger.error("Error initializing collection or item", e);
		}

	}

	@Test
	public void replaceItemThumbnail() throws ImejiException, IOException {
		ItemController controller = new ItemController();
		try {
			item = controller.updateThumbnail(item, thumbnailFile,
					JenaUtil.testUser);
		} catch (ImejiException e) {
			Assert.fail("Thubmnail could not be replaced" + e.getMessage());
		}
		StorageController sController = new StorageController();
		File storedFile = File.createTempFile("testFile", null);
		FileOutputStream fos = new FileOutputStream(storedFile);
		sController.read(item.getFullImageUrl().toString(), fos, true);
		Assert.assertEquals(StorageUtils.calculateChecksum(originalFile),
				StorageUtils.calculateChecksum(storedFile));
		Assert.assertEquals(StorageUtils.calculateChecksum(originalFile),
				item.getChecksum());

	}

	@Test
	public void replaceItemFile() throws ImejiException, IOException {
		ItemController controller = new ItemController();
		try {
			item = controller.updateFile(item, thumbnailFile, "test.tmp",
					JenaUtil.testUser);
		} catch (ImejiException e) {
			Assert.fail("File could not be replaced" + e.getMessage());
		}
		StorageController sController = new StorageController();
		File storedFile = File.createTempFile("testFile", null);
		FileOutputStream fos = new FileOutputStream(storedFile);
		sController.read(item.getFullImageUrl().toString(), fos, true);
		Assert.assertNotEquals(StorageUtils.calculateChecksum(originalFile),
				StorageUtils.calculateChecksum(storedFile));
		Assert.assertEquals(StorageUtils.calculateChecksum(thumbnailFile),
				StorageUtils.calculateChecksum(storedFile));
		Assert.assertEquals(StorageUtils.calculateChecksum(thumbnailFile),
				item.getChecksum());

	}


	private static void createItem() throws ImejiException {
		ItemController controller = new ItemController();
		if(collection == null) {
			createCollection();
		}
		item = ImejiFactory.newItem(collection);
		item = controller.createWithFile(item, originalFile, "test.jpg",
				collection, JenaUtil.testUser);
	}

}
