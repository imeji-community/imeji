package de.mpg.imeji.presentation.servlet;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.log4j.Logger;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.PropertyReader;


/**
 * SErvlet to call Data viewer service
 * 
 * @author saquet
 *
 */
public class DataViewerServlet extends HttpServlet {

	private static final long serialVersionUID = -4602021617386831403L;
	private static Logger logger = Logger.getLogger(DataViewerServlet.class);
    
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
			File image = new File("");
			String name = item.getFilename();
			String fileExtensionName = name.split("\\.")[name.split("\\.").length - 1];
			
			/*
			 * transfer file to dataViewer	
			
			StorageController controller = new StorageController();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			controller.read(item.getFullImageUrl().toString(), out, true);
			byte[] data = out.toByteArray();
			ByteArrayInputStream istream = new ByteArrayInputStream(data);
			out.close();
			image = viewGenericFile(istream, fileExtensionName);
			 */
			
			/*
			 * send the file URL to dataViewer	 
			*/
			URI fullImageUrl = item.getFullImageUrl();
			System.out.println(fullImageUrl.toString());
			image = viewFitsFile(fullImageUrl, false);
			
	        String contentType = getServletContext().getMimeType(image.getName());
			
	        // Init servlet response.
	        resp.reset();
	        resp.setContentType(contentType);
	        resp.setHeader("Content-Length", String.valueOf(image.length()));

	        // Write image content to response.
	        Files.copy(image.toPath(), resp.getOutputStream());

	        // resp.getWriter().append("id" + id);
		} catch (HttpResponseException he) {
			resp.sendError(he.getStatusCode(), he.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}
	

	private File viewFitsFile(URI url, boolean isLoad) throws FileNotFoundException, IOException {
 		String fitsServiceTargetURL = "http://localhost:8080/fits/api/view?url="+url+"&load="+String.valueOf(isLoad);
 		//String fitsServiceTargetURL = "http://servicehub.mpdl.mpg.de/fits/api/view?url="+url+"&load="+String.valueOf(isLoad);

 		GetMethod get = new GetMethod(fitsServiceTargetURL);
 		
 		HttpClient client = new HttpClient();
 		int status = client.executeMethod(get);
 		File respFile = File.createTempFile("fits", ".html");
 		IOUtils.copy(get.getResponseBodyAsStream(), new FileOutputStream(respFile));
 		get.releaseConnection();
		return respFile;
 	}
	
	
	private File viewGenericFile(InputStream istream, String fileType) throws FileNotFoundException, IOException, URISyntaxException {
		String dataViewerServiceTargetURL = PropertyReader.getProperty("dataViewer.service.targetURL");
		PostMethod post = new PostMethod(dataViewerServiceTargetURL);
		try {
			post.setParameter("mimetype", fileType);	
			File file = File.createTempFile("tmpInputstreamFile", ".tmp");
			OutputStream outputStream = new FileOutputStream(file);
 
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = istream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			
			Part[] parts = {new FilePart(file.getName(), file), new StringPart("mimetype",fileType)};

			post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));

			outputStream.flush();
			outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
		}
	
		HttpClient client = new HttpClient();
		int status = client.executeMethod(post);
		File respFile = File.createTempFile("result", ".html");
		IOUtils.copy(post.getResponseBodyAsStream(), new FileOutputStream(respFile));
		post.releaseConnection();
		return respFile;
	}
	
	/*
	public File viewGenericFileFromURL(URI url, String fileType) throws IOException, URISyntaxException{
		String dataViewerServiceTargetURL = PropertyReader.getProperty("dataViewer.service.targetURL");
		String connURL = String.format(dataViewerServiceTargetURL + "?url=%s&mimetype=%s", String.valueOf(url), fileType);
		GetMethod get = new GetMethod(connURL);
		HttpClient client = new HttpClient();
		client.executeMethod(get);		
		File respFile = File.createTempFile("result", ".html");
		IOUtils.copy(get.getResponseBodyAsStream(), new FileOutputStream(respFile));
		get.releaseConnection();
		return respFile;
	}
	*/
	
	

}
