package de.mpg.imeji.presentation.servlet;

import java.io.ByteArrayInputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.client.HttpResponseException;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.beans.ConfigurationBean;
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
			ConfigurationBean config = new ConfigurationBean();
			String id = req.getParameter("id");
			Item item = ObjectLoader.loadItem(ObjectHelper.getURI(Item.class, id),sb.getUser());
			File image = new File("");
			String name = item.getFilename();
			String fileExtensionName = name.split("\\.")[name.split("\\.").length - 1];
			
			String dataViewerUrl = "";
			if(config.getDataViewerUrl().endsWith("/")){
				dataViewerUrl = config.getDataViewerUrl()+"api/view";
			}else{
				dataViewerUrl = config.getDataViewerUrl()+"/api/view";
			}
			
			if (fileExtensionName.equalsIgnoreCase("fits")){
				/*
				 * send the file URL to dataViewer if file is in .fits format 
				*/
				URI fullImageUrl = item.getFullImageUrl();
				System.out.println("fitsURL = " +fullImageUrl.toString());
				image = viewFileByURL(fullImageUrl, false, fileExtensionName, dataViewerUrl);
			}else{
				/*
				 * transfer file to dataViewer	
				 */
				StorageController controller = new StorageController();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				controller.read(item.getFullImageUrl().toString(), out, true);
				byte[] data = out.toByteArray();
				ByteArrayInputStream istream = new ByteArrayInputStream(data);
				out.flush();
				out.close();
				image = viewGenericFile(istream, fileExtensionName, dataViewerUrl);
			}
			
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
	

	private File viewFileByURL(URI url, boolean isLoad, String fileType, String dataViewerServiceTargetURL) throws FileNotFoundException, IOException, URISyntaxException {
    	String serviceTargetURL = dataViewerServiceTargetURL+"?url="+url+"&load="+String.valueOf(isLoad)+"&mimetype="+fileType;
  
 		GetMethod get = new GetMethod(serviceTargetURL);
 		
 		HttpClient client = new HttpClient();
 		int status = client.executeMethod(get);
 		File respFile = File.createTempFile("fits", ".html");
 		IOUtils.copy(get.getResponseBodyAsStream(), new FileOutputStream(respFile));
 		get.releaseConnection();
		return respFile;
 	}
	
	
	private File viewGenericFile(InputStream istream, String fileType, String dataViewerServiceTargetURL) throws FileNotFoundException, IOException, URISyntaxException {
 
		MultiPart multiPart = null;
		try {
			File file = File.createTempFile("tmpInputstreamFile", ".tmp");
			OutputStream outputStream = new FileOutputStream(file);
 
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = istream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			FileDataBodyPart filePart = new FileDataBodyPart(file.getName(), file);
			multiPart =  new FormDataMultiPart().field("mimetype", fileType).bodyPart(filePart);
			
			outputStream.flush();
			outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
		}
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
		WebTarget target = client.target(dataViewerServiceTargetURL);
		Response response =target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
		
		File respFile = File.createTempFile("result", ".html");
		IOUtils.copy(response.readEntity(InputStream.class), new FileOutputStream(respFile));
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
