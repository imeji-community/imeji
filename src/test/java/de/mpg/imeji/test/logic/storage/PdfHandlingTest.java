package de.mpg.imeji.test.logic.storage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import de.mpg.imeji.logic.storage.Storage.FileResolution;
import de.mpg.imeji.logic.storage.util.PdfUtils;

public class PdfHandlingTest {

  @Test
  public void createImageFromPdfRenderer2Test() throws IOException {
    File file = new File("src/test/resources/pdf/pdfWith4Pages.pdf");
    byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
    ByteArrayInputStream baos =
        new ByteArrayInputStream(PdfUtils.pdfsToImageBytes(bytes, FileResolution.THUMBNAIL));
    BufferedImage srcImage = ImageIO.read(baos);
    ImageIO.write(srcImage, "png", new File("src/test/resources/pdf/test.jpg"));
  }
}
