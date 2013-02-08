/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.upload.deposit;

import java.net.URI;

import de.escidoc.core.resources.om.item.Item;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;

/**
 * @author yu
 */
public class DepositController
{
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
        de.mpg.imeji.logic.vo.Item item = ImejiFactory.newItem(collection);
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
        if (collection.getStatus() == Status.RELEASED)
        {
            item.setStatus(Status.RELEASED);
        }
        itemController.create(item, collection.getId());
        return item;
    }
}
