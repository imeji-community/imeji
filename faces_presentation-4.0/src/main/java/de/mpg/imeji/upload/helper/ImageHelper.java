package de.mpg.imeji.upload.helper;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.naming.InitialContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.xmlbeans.XmlCursor;

import de.escidoc.schemas.components.x09.ComponentDocument.Component;
import de.escidoc.schemas.components.x09.ComponentDocument.Component.Content.Storage.Enum;
import de.escidoc.schemas.components.x09.PropertiesDocument.Properties;
import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.escidoc.ItemVO;

public class ImageHelper
{
    public static Item setComponent(String contentCategory, Item item, BufferedImage bufferedImage, String fileName,
            String mimetype, String format, String userHandle) throws Exception
    {
        URL url = null;
        if (item.getComponents() == null)
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
        component.getProperties().setMimeType(mimetype);
        if (contentCategory.equals(getThumb()))
        {
            bufferedImage = scaleImage(bufferedImage, Integer.parseInt(PropertyReader
                    .getProperty("xsd.resolution.thumbnail")));
        }
        if (contentCategory.equals(getWeb()))
        {
            bufferedImage = scaleImage(bufferedImage, Integer
                    .parseInt(PropertyReader.getProperty("xsd.resolution.web")));
        }
        component.getProperties().setContentCategory(contentCategory);
        // use imageIO.write to encode the image back into a byte[]
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, format, byteOutput);
        url = ImageHelper.uploadFile(new ByteArrayInputStream(byteOutput.toByteArray()), mimetype, userHandle);
        component.getContent().setHref(url.toExternalForm());
        Enum enu = Enum.forString("internal-managed");
        component.getContent().setStorage(enu);
        return item;
    }

    public static BufferedImage scaleImage(BufferedImage image, int width) throws Exception
    {
        Image rescaledImage = image.getScaledInstance(width, -1, Image.SCALE_SMOOTH);
        BufferedImage rescaledBufferedImage = new BufferedImage(rescaledImage.getWidth(null), rescaledImage
                .getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = rescaledBufferedImage.getGraphics();
        g.drawImage(rescaledImage, 0, 0, null);
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
    public static URL uploadFile(InputStream in, String mimetype, String userHandle) throws Exception
    {
        // Prepare the HttpMethod.
        String fwUrl = ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(in));
        method.setRequestHeader("Content-Type", mimetype);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        InitialContext context = new InitialContext();
        XmlTransforming ctransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
        return ctransforming.transformUploadResponseToFileURL(response);
    }

    public static String getThumbnailUrl(ItemVO item) throws Exception
    {
        return getContentUrl(item, getThumb());
    }

    public static String getWebResolutionUrl(ItemVO item) throws Exception
    {
        return getContentUrl(item, getWeb());
    }

    public static String getOriginalResolution(ItemVO item) throws Exception
    {
        return getContentUrl(item, getOrig());
    }

    public static String getContentUrl(ItemVO item, String contentCategory) throws Exception
    {
        for (Component c : item.getItemDocument().getItem().getComponents().getComponentArray())
        {
            if (c.getProperties().getContentCategory().equals(contentCategory))
            {
                return ServiceLocator.getFrameworkUrl() + "/ir/item/" + item.getItemDocument().getItem().getObjid()
                        + "/components/component/" + c.getObjid() + "/content";
            }
        }
        return null;
    }

    public static String getThumb() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("xsd.metadata.content-category.thumbnail");
    }

    public static String getWeb() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("xsd.metadata.content-category.web-resolution");
    }

    public static String getOrig() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("xsd.metadata.content-category.original-resolution");
    }
}
