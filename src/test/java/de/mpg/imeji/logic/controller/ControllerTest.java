package de.mpg.imeji.logic.controller;

import java.net.URI;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import util.JenaUtil;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata.Types;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.util.ImejiFactory;

/**
 * Created by vlad on 15.04.15.
 */
public class ControllerTest {

	protected static CollectionImeji collection = null;
	protected static MetadataProfile profile = null;
	protected static Item item = null;

	@BeforeClass
	public static void setup() {
		System.out.println("Initializing JENA");
		JenaUtil.initJena();
		System.out.println("Initializing JENA FINISHED");
	}

	@AfterClass
	public static void tearDown() throws Exception {
		System.out.println("ClOSING JENA");
		JenaUtil.closeJena();
	}

	protected static void createCollection() throws ImejiException {
		CollectionController controller = new CollectionController();
		collection = ImejiFactory.newCollection("test", "Planck", "Max", "MPG");
		URI uri = controller.create(collection, profile, JenaUtil.testUser,
				null);
		collection = controller.retrieve(uri, JenaUtil.testUser);
	}

	protected static void createProfile() throws ImejiException {
		ProfileController controller = new ProfileController();
		profile = new MetadataProfile();
		profile.setTitle("test");
		profile.getStatements().add(
				ImejiFactory.newStatement("md", "en", Types.TEXT));
		profile = controller.create(profile, JenaUtil.testUser);
	}

	protected static void createItem() throws ImejiException {
		ItemController controller = new ItemController();
		item = controller.create(ImejiFactory.newItem(collection), collection.getId(),
				JenaUtil.testUser);
	}

}