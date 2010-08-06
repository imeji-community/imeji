package de.mpg.escidoc.faces.upload;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import de.mpg.escidoc.faces.deposit.DepositController;
import de.mpg.escidoc.faces.item.ImejiItemVO;


/**
 * 
 * @author yu
 *
 */

public class UploadServlet extends HttpServlet
{
	private static Logger logger = Logger.getLogger(UploadServlet.class);
	private static String title;
	private static String description;
	private static String mimetype;
	private static String format;
	private static String userHandle;
	private static String collection;
	private static String context;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
	}
	
	public  void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
	{

		ServletInputStream inputStream = req.getInputStream();
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		title = req.getParameter("name");
		StringTokenizer st = new StringTokenizer(title, ".");
		while(st.hasMoreTokens())
			format = st.nextToken();
		
		mimetype = "image/"+ format;
		userHandle = req.getParameter("userHandle");
		collection = req.getParameter("collection");
		context = req.getParameter("context");
	
		//TODO remove static image description 
		description = "";

		ImejiItemVO item = DepositController.createImejiItem(bufferedImage, title, description, mimetype, format, userHandle,collection, context );

		try 
		{
			String itemXml = DepositController.depositImejiItem(item, userHandle);
			System.out.println(itemXml);
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		} 

	}

	}


