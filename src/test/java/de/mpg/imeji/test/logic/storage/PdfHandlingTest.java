package de.mpg.imeji.test.logic.storage;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.mpg.imeji.logic.storage.util.PdfUtils;

public class PdfHandlingTest {

  @Test
  public void createImageFromPdfRenderer2Test() throws IOException {
    File file = new File("src/test/resources/pdf/pdfWith4Pages.pdf");
    PdfUtils.pdfsToImageBytes(file);
  }
}
