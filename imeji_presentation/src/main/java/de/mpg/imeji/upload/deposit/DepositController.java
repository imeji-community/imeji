/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.upload.deposit;

import java.io.InputStream;
import java.net.URI;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.resources.om.item.Item;
import de.mpg.imeji.escidoc.EscidocHelper;
import de.mpg.imeji.util.PropertyReader;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Image.Visibility;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.User;

/**
 * @author yu
 */
public class DepositController
{
  
    /**
     * Create the escidoc item with the images as components (for version from 1.3)
     * 
     * @param inputStream
     * @param title
     * @param mimetype
     * @param format
     * @return
     * @throws Exception
     */
    public Item createEscidocItem(InputStream inputStream, String title, String mimetype, String format) throws Exception
    {
    	EscidocHelper escidocHelper = new EscidocHelper();
    	Authentication auth = escidocHelper.login();
    	
    	Item item = escidocHelper.initNewItem(PropertyReader.getProperty("escidoc.imeji.content-model.id")
    			, PropertyReader.getProperty("escidoc.imeji.context.id"));
    	
    	item = escidocHelper.loadFiles(item, inputStream, title, mimetype, format, auth);
    	
    	return escidocHelper.createItem(item, auth);
    }
    
    /**
     * Create the {@link Image} in Jena.
     * 
     * @param collection
     * @param user
     * @param escidocId (if created in eSciDoc)
     * @param title
     * @param fullImageURL
     * @param thumbnailURL
     * @param webURL
     * @return
     * @throws Exception 
     */
    public Image createImejiImage(CollectionImeji collection, User user, String escidocId, String title, URI fullImageURI, URI thumbnailURI, URI webURI) throws Exception
    {
    	ImageController imageController = new ImageController(user);
        Image img = new Image();        
        img.setCollection(collection.getId());
        img.setFullImageUrl(fullImageURI);
        img.setThumbnailImageUrl(thumbnailURI);
        img.setWebImageUrl(webURI);
        img.setVisibility(Visibility.PUBLIC);
        img.setFilename(title);
        
        if (escidocId != null)
        {
        	  img.setEscidocId(escidocId);
        }
        if(collection.getProperties().getStatus() == Status.RELEASED)
        {
        	img.getProperties().setStatus(Status.RELEASED);
        }
        
        imageController.create(img, collection.getId());
        
        return img;
    }
}
