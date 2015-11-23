package de.mpg.imeji.test.logic.controller;

import java.net.URI;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.controller.CollectionController.MetadataProfileCreationMethod;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata.Types;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.util.ImejiFactory;
import util.JenaUtil;

/**
 * Created by vlad on 15.04.15.
 */
public class ControllerTest {

  protected static CollectionImeji collection = null;
  protected static MetadataProfile profile = null;
  protected static Item item = null;

  @BeforeClass
  public static void setup() {
    JenaUtil.initJena();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    JenaUtil.closeJena();
  }

  protected static CollectionImeji createCollection() throws ImejiException {
    CollectionController controller = new CollectionController();
    collection = ImejiFactory.newCollection("test", "Planck", "Max", "MPG");
    URI uri = controller.create(collection, profile, JenaUtil.testUser,
        MetadataProfileCreationMethod.COPY, null);
    collection = controller.retrieve(uri, JenaUtil.testUser);
    return collection;
  }

  protected static MetadataProfile createProfile() throws ImejiException {
    ProfileController controller = new ProfileController();
    profile = new MetadataProfile();
    profile.setTitle("test");
    profile.getStatements().add(ImejiFactory.newStatement("md", "en", Types.TEXT));
    profile = controller.create(profile, JenaUtil.testUser);
    return profile;
  }

  protected static Item createItem() throws ImejiException {
    ItemController controller = new ItemController();
    item =
        controller.create(ImejiFactory.newItem(collection), collection.getId(), JenaUtil.testUser);
    return item;
  }

}
