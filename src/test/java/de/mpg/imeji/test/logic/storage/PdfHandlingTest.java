package de.mpg.imeji.test.logic.storage;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import de.mpg.imeji.logic.storage.Storage.FileResolution;
import de.mpg.imeji.logic.storage.util.PdfUtils;

public class PdfHandlingTest {
  // @Test
  public void createImageFromPdfRendererTest() {
    RandomAccessFile raf;
    try {
      raf = new RandomAccessFile(new File("src/test/resources/pdf/pdfWith4Pages.pdf"), "r");
      FileChannel channel = raf.getChannel();
      ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
      PDFFile pdffile = new PDFFile(buf);
      // draw the first page to an image
      int num = pdffile.getNumPages();
      for (int i = 0; i < num; i++) {
        PDFPage page = pdffile.getPage(i);
        // get the width and height for the doc at the default zoom
        int width = (int) page.getWidth();
        int height = (int) page.getHeight();
        float widthPt = width;
        float heightPt = height;
        float scaling = 300 / (float) 72;
        int widthPx = Math.round(widthPt * scaling);
        int heightPx = Math.round(heightPt * scaling);
        Rectangle rect = new Rectangle(0, 0, (int) widthPt, (int) heightPt);
        int rotationAngle = page.getRotation();
        Rectangle rect1 = rect;
        if (rotationAngle == 90 || rotationAngle == 270)
          rect1 = new Rectangle(0, 0, rect.height, rect.width);
        BufferedImage retval = null;
        // normalize the rotation angle
        if (rotationAngle < 0) {
          rotationAngle += 360;
        } else if (rotationAngle >= 360) {
          rotationAngle -= 360;
        }
        // swap width and height
        if (rotationAngle == 90 || rotationAngle == 270) {
          retval = (BufferedImage) page.getImage(heightPx, widthPx, // width & height
              rect1, // clip rect
              null, // null for the ImageObserver
              true, // fill background with white
              true // block until drawing is done
              );
        } else {
          retval = (BufferedImage) page.getImage(widthPx, heightPx, // width & height
              rect1, // clip rect
              null, // null for the ImageObserver
              true, // fill background with white
              true // block until drawing is done
              );
        }
        Graphics2D graphics = (Graphics2D) retval.getGraphics();
        // graphics.setBackground( TRANSPARENT_WHITE );
        // graphics.clearRect( 0, 0, retval.getWidth(), retval.getHeight() );
        if (rotationAngle != 0) {
          int translateX = 0;
          int translateY = 0;
          switch (rotationAngle) {
            case 90:
              translateX = retval.getWidth();
              break;
            case 270:
              translateY = retval.getHeight();
              break;
            case 180:
              translateX = retval.getWidth();
              translateY = retval.getHeight();
              break;
            default:
              break;
          }
          graphics.translate(translateX, translateY);
          graphics.rotate((float) Math.toRadians(rotationAngle));
        }
        graphics.scale(scaling, scaling);
        // BufferedImage sImg = (BufferedImage)img.getScaledInstance(width*3, height*3,
        // BufferedImage.SCALE_FAST);
        //
        // BufferedImage buffered = new BufferedImage(width*3, height*3, BufferedImage.SCALE_FAST);
        // buffered.getGraphics().drawImage(img, 0, 0 , null);
        ImageIO.write(retval, "png", new File("src/test/resources/pdf/test.png"));
        break;
      }
    } catch (FileNotFoundException e1) {
      System.err.println(e1.getLocalizedMessage());
    } catch (IOException e) {
      System.err.println(e.getLocalizedMessage());
    }
  }

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
