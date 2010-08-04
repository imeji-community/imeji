package de.mpg.escidoc.faces.upload;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;


import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.mpg.escidoc.faces.album.beans.AlbumSessionOld;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.deposit.DepositController;
import de.mpg.escidoc.faces.item.FacesItemVO;
import de.mpg.escidoc.faces.item.ImejiItemVO;
import de.mpg.escidoc.faces.item.ItemVO;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;


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
	private String userHandle;
	
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
	
	
		//TODO remove static image description 
		description = "";

		ImejiItemVO item = DepositController.createImejiItem(bufferedImage, title, description, mimetype, format, userHandle );

		try 
		{
			String itemXml = DepositController.depositImejiItem(item, userHandle);
			System.out.println(itemXml);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	


	
	

	}


