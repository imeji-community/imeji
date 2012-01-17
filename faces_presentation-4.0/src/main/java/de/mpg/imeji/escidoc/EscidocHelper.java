/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.escidoc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ajax4jsf.resource.image.animatedgif.GifDecoder;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.StorageType;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.ComponentContent;
import de.escidoc.core.resources.om.item.component.ComponentProperties;
import de.escidoc.core.resources.om.item.component.Components;
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.upload.helper.ImageHelper;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.PropertyReader;

public class EscidocHelper 
{
	private static Logger logger = Logger.getLogger(EscidocHelper.class);
	
	public Item initNewItem(String contentModel, String context) throws IOException, URISyntaxException, ParserConfigurationException
	{
		Item item = new Item();

		item.getProperties().setContext(new ContextRef(context));
		item.getProperties().setContentModel(new ContentModelRef(contentModel));

		MetadataRecords mdrs = new MetadataRecords();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		MetadataRecord mdRecord = new MetadataRecord("escidoc");
		Element element = doc.createElementNS(null, "imeji-metadata");
		mdRecord.setContent(element);
		mdrs.add(mdRecord);
		item.setMetadataRecords(mdrs);

		return item;
	}

	public Component initNewComponent(String contentCategory, String filename, String mimeType, String href)
	{
		Component c = new Component();

		ComponentProperties cp = new ComponentProperties();
		cp.setContentCategory(contentCategory);
		cp.setVisibility("private");
		cp.setFileName(filename);
		cp.setMimeType(mimeType);
		c.setProperties(cp);

		ComponentContent cc = new ComponentContent();
		cc.setStorage(StorageType.INTERNAL_MANAGED);
		cc.setXLinkHref(href);
		c.setContent(cc);

		return c;
	}

	public Authentication login() throws AuthenticationException, TransportException, MalformedURLException, IOException, URISyntaxException
	{
		return new Authentication(
				new URL(PropertyReader.getProperty("escidoc.framework_access.framework.url"))
				, PropertyReader.getProperty("imeji.escidoc.user")
				, PropertyReader.getProperty("imeji.escidoc.password"));
	}

	public Item createItem(Item item, Authentication auth) throws EscidocException, InternalClientException, TransportException
	{
		ItemHandlerClient handler = new ItemHandlerClient(auth.getServiceAddress());
		handler.setHandle(auth.getHandle());
		return handler.create(item);
	}

	public Item loadFiles(Item item, InputStream inputStream, String fileName, String mimetype, String format, Authentication auth) throws URISyntaxException, Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		int b;
		while((b = inputStream.read()) != -1)
		{
			bos.write(b);
		}
		byte[] imageStream = bos.toByteArray();

		bos.flush();
		bos.close();

		item = uploadFile(item, ImageHelper.getOrig(), imageStream, fileName, mimetype, format, auth);
		item = uploadFile(item, ImageHelper.getWeb(), imageStream, fileName, mimetype, format, auth);
		item = uploadFile(item, ImageHelper.getThumb(), imageStream, fileName, mimetype, format, auth);

		return item;
	}

	public Item uploadFile(Item item, String contentCategory, byte[] imageStream, String fileName, String mimetype, String format, Authentication auth) throws Exception
	{
		URL url = null;
		byte[] scaledImageStream = null;

		if (contentCategory.equals(ImageHelper.getThumb()))
		{   
			BufferedImage bufferedImage;
			try
			{
				bufferedImage= ImageIO.read( new ByteArrayInputStream(imageStream));

				if(bufferedImage.getWidth() > Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail"))
						|| bufferedImage.getHeight() > Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail")))
				{
					bufferedImage = ImageHelper.scaleImage(bufferedImage, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail")), contentCategory);
				}
				ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
				// use imageIO.write to encode the image back into a byte[]
				ImageIO.write(bufferedImage, format, byteOutput);
				scaledImageStream = byteOutput.toByteArray();
				url = uploadFileContent(scaledImageStream, mimetype, auth);
			}
			catch(Exception e)
			{
				logger.error("Error transforming image", e);
				Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
				String test = navigation.getApplicationUrl() + "resources/icon/defaultThumb.gif";
				URL noThumbUrl = new URL(test);
				int contentLength = noThumbUrl.openConnection().getContentLength();
				InputStream openStream =noThumbUrl.openStream();
				byte[] data = new byte[contentLength];
				openStream.read(data);
				openStream.close();

				url = uploadFileContent(data, mimetype, auth);
			}
		}
		else if (contentCategory.equals(ImageHelper.getWeb()))
		{   
			BufferedImage bufferedImage;
			try
			{
				bufferedImage= ImageIO.read( new ByteArrayInputStream(imageStream));

				if(bufferedImage.getWidth() < Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")) 
						&& bufferedImage.getHeight() < Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")))
				{
					scaledImageStream = imageStream;
				}
				else
				{
					if(format.equalsIgnoreCase("gif"))
					{
						GifDecoder gifDecoder = ImageHelper.checkAnimation(imageStream);
						if(gifDecoder.getFrameCount()>1)
						{
							scaledImageStream = ImageHelper.scaleAnimation(imageStream, gifDecoder, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")));
						}
						else
						{
							bufferedImage = ImageHelper.scaleImage(bufferedImage, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")),contentCategory);
							ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
							// use imageIO.write to encode the image back into a byte[]
							ImageIO.write(bufferedImage, format, byteOutput);
							scaledImageStream = byteOutput.toByteArray();
						} 
					}
					else
					{
						bufferedImage = ImageHelper.scaleImage(bufferedImage, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")), contentCategory);
						ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
						// use imageIO.write to encode the image back into a byte[]
						ImageIO.write(bufferedImage, format, byteOutput);
						scaledImageStream = byteOutput.toByteArray();
					} 
				}
				url = uploadFileContent(scaledImageStream, mimetype, auth);
			}
			catch(Exception e)
			{
				url = uploadFileContent(imageStream, mimetype, auth);
			}
		}
		else if(contentCategory.equals(ImageHelper.getOrig()))
		{
			url = uploadFileContent(imageStream, mimetype, auth);
		}

		if (item.getComponents() == null)
		{
			Components cs = new Components();
			item.setComponents(cs);
		}
		item.getComponents().add(initNewComponent(contentCategory, fileName, mimetype, url.toExternalForm()));


		return item;
	}

	public URL uploadFileContent(byte[] image, String mimetype, Authentication auth) throws Exception
	{
		StagingHandlerClient handler = new StagingHandlerClient(auth.getServiceAddress());
		handler.setHandle(auth.getHandle());
		return handler.upload(new ByteArrayInputStream(image));
	}

	public static String getThumbnailUrl(Item item) throws Exception
	{
		return getContentUrl(item, ImageHelper.getThumb());
	}

	public static String getWebResolutionUrl(Item item) throws Exception
	{
		return getContentUrl(item, ImageHelper.getWeb());
	}

	public static String getOriginalResolution(Item item) throws Exception
	{
		return getContentUrl(item, ImageHelper.getOrig());
	}

	public static String getContentUrl(Item item, String contentCategory) throws Exception
	{
		for (Component c : item.getComponents())
		{
			if (c.getProperties().getContentCategory().equals(contentCategory))
			{
				return PropertyReader.getProperty("escidoc.framework_access.framework.url") +  c.getContent().getXLinkHref();
			}
		}
		return null;
	}
}
