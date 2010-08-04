package de.mpg.escidoc.faces.upload.helper;

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.naming.InitialContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import de.escidoc.schemas.components.x09.ComponentDocument.Component;
import de.escidoc.schemas.components.x09.ComponentDocument.Component.Content;
import de.escidoc.schemas.components.x09.ComponentDocument.Component.Content.Storage.Enum;
import de.escidoc.schemas.components.x09.ComponentsDocument.Components;
import de.escidoc.schemas.components.x09.PropertiesDocument.Properties;
import de.escidoc.schemas.contentstreams.x07.ContentStreamDocument.ContentStream;
import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.escidoc.schemas.metadatarecords.x05.MdRecordDocument.MdRecord;
import de.escidoc.schemas.metadatarecords.x05.MdRecordsDocument.MdRecords;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Storage;
import de.mpg.escidoc.services.framework.PropertyReader;

public class ImageHelper 

{
	
	private static String thumb;
	private static String web;
	private static String orig;
	
	private static String thumb_width;
	private static String web_width;
	public ImageHelper()
	{
		try{
			init();
		}catch(Exception e){
			//TODO
			e.getMessage();
		}
		
	}
	
	private void init() throws IOException, URISyntaxException {
		
		thumb = PropertyReader.getProperty("xsd.metadata.content-category.thumbnail");
		web = PropertyReader.getProperty("xsd.metadata.content-category.web-resolution");
		orig = PropertyReader.getProperty("xsd.metadata.content-category.original-resolution");
		thumb_width = PropertyReader.getProperty("xsd.resolution.thumbnail");
		web_width = PropertyReader.getProperty("xsd.resolution.web");
	}

	public Item setComponent(String contentCategory, Item item, BufferedImage bufferedImage, String fileName, String mimetype, String userHandle) throws Exception
	{
		ImageHelper helper = new ImageHelper();
		URL url = null;
		
		if(item.getComponents()==null)
		{
			item.addNewComponents();
		}

		Component component = item.getComponents().addNewComponent();
		component.addNewContent();
		component.addNewProperties();
		component.getProperties().addNewValidStatus();
		Properties props = component.getProperties();
		props.addNewVisibility();
		XmlCursor propCursor = component.getProperties().getVisibility().newCursor();
		propCursor.toEndToken();
		propCursor.insertChars("private");
		component.getProperties().setFileName(fileName);
		component.getProperties().setMimeType("image/jpg");
		
//		BufferedImage bufferedImage = ImageIO.read(inputStream);
		
		if(contentCategory.equals(thumb)){
			bufferedImage = scaleImage(bufferedImage, Integer.parseInt(thumb_width));
			
		}
		if(contentCategory.equals(web)){
			bufferedImage = scaleImage(bufferedImage, Integer.parseInt(web_width));

		}
		component.getProperties().setContentCategory(contentCategory);

		//use imageIO.write to encode the image back into a byte[]
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage,"jpg", byteOutput);
		url = helper.uploadFile(new ByteArrayInputStream(byteOutput.toByteArray()), mimetype, userHandle);

		component.getContent().setHref(url.toExternalForm());
		Enum enu = Enum.forString("internal-managed");
		component.getContent().setStorage(enu);

//		item.getComponents().setComponentArray(0, component);
		
		return item;
	}
	
	public static BufferedImage scaleImage(BufferedImage image, int width) throws Exception
	{
		Image rescaledImage = image.getScaledInstance(width, -1, Image.SCALE_SMOOTH);
		BufferedImage rescaledBufferedImage = new BufferedImage(rescaledImage.getWidth(null), rescaledImage.getHeight(null),BufferedImage.TYPE_INT_RGB);
		Graphics g = rescaledBufferedImage.getGraphics();
		g.drawImage(rescaledImage, 0,0, null);
	
		return rescaledBufferedImage;
	}
	
    /**
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * 
     * @param InputStream to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userhandle to use for upload
     * @return The URL of the uploaded file.
     * @throws Exception If anything goes wrong...
     */
    public URL uploadFile(InputStream in, String mimetype, String userHandle) throws Exception
    {
        // Prepare the HttpMethod.
        String fwUrl = de.mpg.escidoc.services.framework.ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(in));
        method.setRequestHeader("Content-Type", "image/jpg");
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        InitialContext context = new InitialContext();
        XmlTransforming ctransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
        return ctransforming.transformUploadResponseToFileURL(response);
    }
    

	public static String getThumb() {
		return thumb;
	}


	public static String getWeb() {
		return web;
	}

	public static String getOrig() {
		return orig;
	}
}
