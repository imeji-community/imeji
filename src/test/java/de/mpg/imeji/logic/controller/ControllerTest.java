package de.mpg.imeji.logic.controller;

import java.net.URI;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Metadata.Types;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import util.JenaUtil;

/**
 * Created by vlad on 15.04.15.
 */
public class ControllerTest {

	protected static CollectionImeji collection = null;
	protected static MetadataProfile profile = null;

	@BeforeClass
	public static void setup() {
		JenaUtil.initJena();
	}

	@AfterClass
	public static void tearDown() throws Exception {
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

}