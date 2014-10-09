package de.mpg.imeji.presentation.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.client.HttpResponseException;
import org.apache.log4j.Logger;

import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * SErvlet to call Data viewer service
 * 
 * @author saquet
 *
 */
public class DataViewerServlet extends HttpServlet {

	private static final long serialVersionUID = -4602021617386831403L;
	private static Logger logger = Logger.getLogger(FileServlet.class);
    
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Data Viewer Servlet initialized");
	}
   
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			SessionBean sb = (SessionBean)req.getSession(false).getAttribute(SessionBean.class.getSimpleName());
			String id = req.getParameter("id");
			Item item = ObjectLoader.loadItem(ObjectHelper.getURI(Item.class, id),sb.getUser());
			StorageController controller = new StorageController();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			controller.read(item.getFullImageUrl().toString(), out, true);
			byte[] data = out.toByteArray();
			ByteArrayInputStream istream = new ByteArrayInputStream(data);
			out.close();
			String name = item.getFilename();
			File image = new File("");
			if(name.endsWith(".swc"))
				image = viewSWCFile(istream);
			else
				viewFitsFile(istream, resp);	 

	        String contentType = getServletContext().getMimeType(image.getName());
			
	        // Init servlet response.
	        resp.reset();
	        resp.setContentType(contentType);
	        resp.setHeader("Content-Length", String.valueOf(image.length()));

	        // Write image content to response.
	        Files.copy(image.toPath(), resp.getOutputStream());

//			resp.getWriter().append("id" + id);
		} catch (HttpResponseException he) {
			
			resp.sendError(he.getStatusCode(), he.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	private void viewFitsFile(InputStream istream, HttpServletResponse response) {
		String fitsServiceTargetURL = "http://servicehub.mpdl.mpg.de/fits/api/view";
		
	}

	private File viewSWCFile(InputStream istream) throws FileNotFoundException, IOException {
		String swcServiceTargetURL = "http://servicehub.mpdl.mpg.de/swc/api/view";
		PostMethod post = new PostMethod(swcServiceTargetURL);
		try {
			post.setParameter("swc", IOUtils.toString(istream));
		} catch (Exception e) {
			e.printStackTrace();
		}
		post.setParameter("portable", "true");		
		HttpClient client = new HttpClient();
		int status = client.executeMethod(post);
		File respFile = File.createTempFile("swc_3d", ".html");
		IOUtils.copy(post.getResponseBodyAsStream(), new FileOutputStream(respFile));
		post.releaseConnection();
		return respFile;
	}

}
