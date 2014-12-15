package de.mpg.imeji.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.j2j.exceptions.NotFoundException;

public class ItemServiceTest {

	private ItemWithFileTO itemWithFileTo;
	private ItemTO itemTo;
	private CollectionImeji c;
	private static final String TEST_IMAGE = "./src/test/resources/storage/test.png";
	private File file = null;

	@Before
	public void setup() throws Exception {
		JenaUtil.initJena();
		initItem();
	}

	@After
	public void tearDown() throws Exception {

		JenaUtil.closeJena();
	}

	public void initItem() throws Exception {
		file = new File(TEST_IMAGE);
		c = ImejiFactory.newCollection();
		CollectionController controller = new CollectionController();
		controller.create(c, null, JenaUtil.testUser);
		itemWithFileTo = new ItemWithFileTO();
		itemWithFileTo.setFilename("testname2");
		itemWithFileTo.setFile(file);
		itemWithFileTo.setCollectionId(c.getIdString());
	}

	@Test
	public void testItemCRUD() throws NotFoundException, NotAllowedError,
			Exception {

		ItemService crud = new ItemService();
		// create item
		try {
			crud.create(itemWithFileTo, null);
			fail("should not allowed to create item");
		} catch (NotAllowedError e) {
			// its everything fine
		}
		System.out.println(itemTo);
		itemTo = crud.create(itemWithFileTo, JenaUtil.testUser);
		// check the item be created and has new id
		assertNotNull(itemTo.getId());
		// check the default visibility of item = private
		assertTrue(itemTo.getVisibility().equals("PRIVATE"));
		// check the item mime type
		assertEquals(itemTo.getMimetype(),
			Files.probeContentType(Paths.get(file.getName())));
		// check the item file name
		assertEquals(itemTo.getFilename(),crud.read(itemTo.getId(), JenaUtil.testUser).getFilename());
		// check the item status
		assertTrue(itemTo.getStatus().equals("PENDING"));

		// read the item
		assertNotNull(crud.read(itemTo.getId(), JenaUtil.testUser).getId());
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getCollectionId(), (c.getIdString()));
		assertEquals(
				crud.read(itemTo.getId(), JenaUtil.testUser).getFilename(),
				(itemTo.getFilename()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getChecksumMd5(), (itemTo.getChecksumMd5()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getCreatedBy().getFullname(),
				(itemTo.getCreatedBy().getFullname()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getModifiedBy().getFullname(),
				(itemTo.getModifiedBy().getFullname()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getCreatedBy().getUserId(),
				(itemTo.getCreatedBy().getUserId()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getModifiedBy().getUserId(),
				(itemTo.getModifiedBy().getUserId()));
		assertEquals(
				crud.read(itemTo.getId(), JenaUtil.testUser).getMimetype(),
				(itemTo.getMimetype()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser).getStatus(),
				(itemTo.getStatus()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getVisibility(), (itemTo.getVisibility()));
		assertEquals(
				crud.read(itemTo.getId(), JenaUtil.testUser).getFilename(),
				(itemTo.getFilename()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getCreatedDate(), (itemTo.getCreatedDate()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getModifiedDate(), (itemTo.getModifiedDate()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getWebResolutionUrlUrl(), (itemTo.getWebResolutionUrlUrl()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser)
				.getThumbnailUrl(), (itemTo.getThumbnailUrl()));
		assertEquals(crud.read(itemTo.getId(), JenaUtil.testUser).getFileUrl(),
				(itemTo.getFileUrl()));

		// delete item
		Assert.assertTrue(crud.delete(itemTo.getId(), JenaUtil.testUser));
		// try to read the delete item
		try {
			// crud.read(itemTo.getId(), JenaUtil.testUser);
			// fail("should not found the item");
		} catch (Exception e) {

		}

	}

}
