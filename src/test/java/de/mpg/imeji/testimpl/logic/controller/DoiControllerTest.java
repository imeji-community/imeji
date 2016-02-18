package de.mpg.imeji.testimpl.logic.controller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.DoiController;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.test.logic.controller.ControllerTest;

public class DoiControllerTest extends ControllerTest{
  
  private static final Logger LOGGER = Logger.getLogger(ItemControllerTestClass.class);

  @Before
  public void specificSetup() {
    try {
      createCollection();
    } catch (ImejiException e) {
      LOGGER.error("Error initializing collection or item", e);
    }

  }
  
  @Ignore
  public void getNewDoiTest() throws ImejiException{
    DoiController dc = new DoiController();
    String doi = dc.getNewDoi(collection, "https://test.doi.mpdl.mpg.de/doxi/rest/doi", "bastien", "test");
    assert(doi != null);
  }  
}
