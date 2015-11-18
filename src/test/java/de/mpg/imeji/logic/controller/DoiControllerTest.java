package de.mpg.imeji.logic.controller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.vo.Properties.Status;

public class DoiControllerTest extends ControllerTest{
  
  private static final Logger logger = Logger.getLogger(ItemControllerTest.class);

  @Before
  public void specificSetup() {
    try {
      createCollection();
    } catch (ImejiException e) {
      logger.error("Error initializing collection or item", e);
    }

  }
  
  @Ignore
  public void getNewDoiTest() throws ImejiException{
    DoiController dc = new DoiController();
    String doi = dc.getNewDoi(collection, "bastien", "test");
    assert(doi != null);
  }  
}
