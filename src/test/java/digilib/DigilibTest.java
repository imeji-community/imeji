/**
 * 
 */
package digilib;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.junit.Test;

import de.mpg.imeji.logic.storage.util.ImageUtils;
import de.mpg.imeji.presentation.module.digilib.DigilibBean;

/**
 * @author hnguyen
 *
 */
public class DigilibTest {

	//@Test
	public void test() throws MalformedURLException, IOException {
		
		DigilibBean db = new DigilibBean();
		
//		String query = "wh=0.1712&ww=0.1282&wy=0.1681&wx=0.6895&dw=862&dh=904";
		String query = "dw=1750&rot=45";//"ww=0.3525&wh=0.2096&wx=0.2613&wy=0.2139";
		
		//String uri = "http://zuse.zib.de/imeji/coreserviceimage?imageUrl=http%3A//localhost%3A8080/ir/item/escidoc%3A9510/components/component/escidoc%3A9508/content";		
		String uri = "src/test/resources/digilib/cat.jpg";
//		String uri = "http://zuse2.zib.de/file/FukdvdNguPLnIsJh/93/cd/82/63-34b0-4bbd-b4b0-acc5fb16170f/0/original/514c183e2bdeb446f1e8b36c919a53b6.jpg";
		
		BufferedImage bim = ImageUtils.byteArrayToBufferedImage(db.getScaledImage("sessionID",uri, query));
		
		ImageIO.write(bim, "png", new File("src/test/resources/digilib/img.png"));
	}
	
	//@Test
	public void uriTest() throws URISyntaxException {
		
		String s = "http://zuse2.zib.de/file/FukdvdNguPLnIsJh/93/cd/82/63-34b0-4bbd-b4b0-acc5fb16170f/0/original/514c183e2bdeb446f1e8b36c919a53b6.jpg";
		
		final URI u = new URI(s);

		
		if(u.isAbsolute())
		{
		  System.out.println("Yes, i am absolute!");
		}
		else
		{
		  System.out.println("Ohh noes, it's a relative URI!");
		}
	}

}
