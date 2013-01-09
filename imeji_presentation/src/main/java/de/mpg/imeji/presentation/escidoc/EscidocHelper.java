/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.escidoc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import de.mpg.imeji.presentation.upload.UploadManager;
import de.mpg.imeji.presentation.upload.helper.ImageHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * Helper for escidoc operation
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class EscidocHelper
{
    private static Logger logger = Logger.getLogger(EscidocHelper.class);

    /**
     * Factory an {@link Item} with imeji values
     * 
     * @param contentModel
     * @param context
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParserConfigurationException
     */
    public Item itemFactory(String contentModel, String context) throws IOException, URISyntaxException,
            ParserConfigurationException
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

    /**
     * Factory for a {@link Component} for imeji files
     * 
     * @param contentCategory
     * @param filename
     * @param mimeType
     * @param href
     * @return
     */
    public Component componentFactory(String contentCategory, String filename, String mimeType, String href)
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

    /**
     * Login in eSciDoc
     * 
     * @return
     * @throws AuthenticationException
     * @throws TransportException
     * @throws MalformedURLException
     * @throws IOException
     * @throws URISyntaxException
     */
    public Authentication login() throws AuthenticationException, TransportException, MalformedURLException,
            IOException, URISyntaxException
    {
        return new Authentication(new URL(PropertyReader.getProperty("escidoc.framework_access.framework.url")),
                PropertyReader.getProperty("imeji.escidoc.user"), PropertyReader.getProperty("imeji.escidoc.password"));
    }

    /**
     * Create an {@link Item} in eSciDoc
     * 
     * @param item
     * @param auth
     * @return
     * @throws EscidocException
     * @throws InternalClientException
     * @throws TransportException
     */
    public Item createItemInEscidoc(Item item, Authentication auth) throws EscidocException, InternalClientException,
            TransportException
    {
        ItemHandlerClient handler = new ItemHandlerClient(auth.getServiceAddress());
        handler.setHandle(auth.getHandle());
        return handler.create(item);
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

    /**
     * Return the url of content of a {@link Component} of an {@link Item} according to its content-category
     * 
     * @param item
     * @param contentCategory
     * @return
     * @throws Exception
     */
    public static String getContentUrl(Item item, String contentCategory) throws Exception
    {
        for (Component c : item.getComponents())
        {
            if (c.getProperties().getContentCategory().equals(contentCategory))
            {
                return PropertyReader.getProperty("escidoc.framework_access.framework.url")
                        + c.getContent().getXLinkHref();
            }
        }
        return null;
    }

    /**
     * Upload a {@link Byte} array file into eSciDoc {@link Item} as a {@link Components}
     * 
     * @param item
     * @param contentCategory
     * @param imageStream
     * @param fileName
     * @param mimetype
     * @param format
     * @param auth
     * @return
     * @throws Exception
     */
    public Item uploadFile(Item item, String contentCategory, byte[] imageStream, String fileName,
            String mimetype, String format, Authentication auth) throws Exception
    {
        UploadManager uploadManager = new UploadManager();
        try
        {
            imageStream = uploadManager.prepareImageForUpload(imageStream, contentCategory, format);
        }
        catch (Exception e)
        {
            logger.error("Error transforming image", e);
            uploadFileContent(uploadManager.getDefaultThumbnailAsByteArray(), mimetype, auth);
        }
        URL url = uploadFileContent(imageStream, mimetype, auth);
        return addImageToEscidocItem(item, url, contentCategory, fileName, mimetype);
    }

    /**
     * Upload a {@link Byte} in the staging area of eSciDoc
     * 
     * @param image
     * @param mimetype
     * @param auth
     * @return
     * @throws Exception
     */
    public URL uploadFileContent(byte[] image, String mimetype, Authentication auth) throws Exception
    {
        StagingHandlerClient handler = new StagingHandlerClient(auth.getServiceAddress());
        handler.setHandle(auth.getHandle());
        return handler.upload(new ByteArrayInputStream(image));
    }

    /**
     * Add the image (defined by its url returned by the staging area) to the {@link Item} as a {@link Component}
     * 
     * @param item
     * @param imageUrl
     * @param contentCategory
     * @param fileName
     * @param mimetype
     * @return
     */
    public Item addImageToEscidocItem(Item item, URL imageUrl, String contentCategory, String fileName, String mimetype)
    {
        if (item.getComponents() == null)
        {
            Components cs = new Components();
            item.setComponents(cs);
        }
        item.getComponents().add(componentFactory(contentCategory, fileName, mimetype, imageUrl.toExternalForm()));
        return item;
    }
}
