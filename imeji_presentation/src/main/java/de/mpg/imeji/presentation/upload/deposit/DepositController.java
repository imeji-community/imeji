/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.upload.deposit;

import java.io.InputStream;
import java.net.URI;

import com.hp.hpl.jena.query.Dataset;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.resources.om.item.Item;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.factory.ItemFactory;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.escidoc.EscidocHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

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
    public Item createEscidocItem(InputStream inputStream, String title, String mimetype, String format)
            throws Exception
    {
        EscidocHelper escidocHelper = new EscidocHelper();
        Authentication auth = escidocHelper.login();
        Item item = escidocHelper.initNewItem(PropertyReader.getProperty("escidoc.imeji.content-model.id"),
                PropertyReader.getProperty("escidoc.imeji.context.id"));
        item = escidocHelper.loadFiles(item, inputStream, title, mimetype, format, auth);
        return escidocHelper.createItem(item, auth);
    }

    /**
     * Create the {@link Item} in Jena.
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
    public de.mpg.imeji.logic.vo.Item createImejiImage(CollectionImeji collection, User user, String escidocId,
            String title, URI fullImageURI, URI thumbnailURI, URI webURI) throws Exception
    {
        ItemController itemController = new ItemController(user);
        de.mpg.imeji.logic.vo.Item item = ItemFactory.create(collection);
        if (collection == null || collection.getId() == null)
        {
            throw new RuntimeException("Can not create item with a collection null");
        }
        item.setCollection(collection.getId());
        item.setFullImageUrl(fullImageURI);
        item.setThumbnailImageUrl(thumbnailURI);
        item.setWebImageUrl(webURI);
        item.setVisibility(Visibility.PUBLIC);
        item.setFilename(title);
        if (escidocId != null)
        {
            item.setEscidocId(escidocId);
        }
        if (collection.getProperties().getStatus() == Status.RELEASED)
        {
            item.getProperties().setStatus(Status.RELEASED);
        }
        itemController.create(item, collection.getId());
        return item;
    }
}
