/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.image;

import java.net.URI;

import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Object for the browsing over the detail items
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SingleItemBrowse
{
    private ItemsBean imagesBean = null;
    private Item currentImage = null;
    private String next = null;
    private String previous = null;

    /**
     * Object for the browsing over the detail items. The Browsing is based on a {@link ItemsBean} and the current
     * {@link Item}.
     * 
     * @param imagesBean
     * @param item
     * @param type
     * @param containerId
     */
    public SingleItemBrowse(ItemsBean imagesBean, Item item, String type, String containerId)
    {
        this.imagesBean = imagesBean;
        currentImage = item;
        init(type, containerId);
    }

    /**
     * Initialize the {@link SingleItemBrowse} for the current {@link Item} and {@link ItemsBean} according to:
     * 
     * @param type - if the detail page is initialized within a collection, an album, or a browse page (item)
     * @param path - the id (not the uri) of the current container ({@link Album} or {@link CollectionImeji})
     */
    public void init(String type, String containerId)
    {
        String baseUrl = new String();
        if (type == "collection")
        {
            baseUrl = ((Navigation)BeanHelper.getApplicationBean(Navigation.class)).getCollectionUrl() + containerId
                    + "/item/";
        }
        else if (type == "item")
        {
            baseUrl = ((Navigation)BeanHelper.getApplicationBean(Navigation.class)).getItemUrl();
        }
        else if (type == "album")
        {
            baseUrl = ((Navigation)BeanHelper.getApplicationBean(Navigation.class)).getAlbumUrl() + containerId
                    + "/item/";
        }
        URI nextImage = getNextImageFromList();
        URI prevImage = getPreviousImageFromList();
        String direction = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nav");
        if (nextImage == null && prevImage == null)
        {
            if ("prev".equals(direction))
            {
                loadPreviousPage();
            }
            else if ("next".equals(direction))
            {
                loadNextPage();
            }
            nextImage = getNextImageFromList();
            prevImage = getPreviousImageFromList();
        }
        if (nextImage == null && loadNextPage())
        {
            nextImage = getFirstImageOfList();
            loadPreviousPage();
        }
        if (prevImage == null && loadPreviousPage())
        {
            prevImage = getLastImageOfList();
            loadNextPage();
        }
        if (nextImage != null)
        {
            next = baseUrl + ObjectHelper.getId(nextImage) + "?nav=next";
        }
        if (prevImage != null)
        {
            previous = baseUrl + ObjectHelper.getId(prevImage) + "?nav=prev";
        }
    }

    /**
     * Retrieve the {@link URI} of {@link Item} of the next page
     * 
     * @return
     */
    public URI getNextImageFromList()
    {
        for (int i = 0; i < imagesBean.getCurrentPartList().size() - 1; i++)
        {
            if (imagesBean.getCurrentPartList().get(i).getUri().equals(currentImage.getId()))
            {
                return imagesBean.getCurrentPartList().get(i + 1).getUri();
            }
        }
        return null;
    }

    /**
     * Retrieve the URI of the {@link Item} of the previous page.
     * 
     * @return
     */
    public URI getPreviousImageFromList()
    {
        for (int i = 1; i < imagesBean.getCurrentPartList().size(); i++)
        {
            if (imagesBean.getCurrentPartList().get(i).getUri().equals(currentImage.getId()))
            {
                return imagesBean.getCurrentPartList().get(i - 1).getUri();
            }
        }
        return null;
    }

    /**
     * Return the {@link URI} of the first page of the current {@link ItemsBean}
     * 
     * @return
     */
    private URI getFirstImageOfList()
    {
        if (imagesBean.getCurrentPartList().size() > 0)
        {
            return imagesBean.getCurrentPartList().get(0).getUri();
        }
        return null;
    }

    /**
     * Return the {@link URI} of the last page of the current {@link ItemsBean}
     * 
     * @return
     */
    private URI getLastImageOfList()
    {
        if (imagesBean.getCurrentPartList().size() > 0)
        {
            return imagesBean.getCurrentPartList().get(imagesBean.getCurrentPartList().size() - 1).getUri();
        }
        return null;
    }

    /**
     * Load the next page and make it the current page
     * 
     * @return
     */
    private boolean loadNextPage()
    {
        if (imagesBean.getCurrentPageNumber() < imagesBean.getPaginatorPageSize())
        {
            imagesBean.goToNextPage();
            imagesBean.update();
            return true;
        }
        return false;
    }

    /**
     * load the previous page and make it the current page.
     * 
     * @return
     */
    private boolean loadPreviousPage()
    {
        if (imagesBean.getCurrentPageNumber() > 1)
        {
            imagesBean.goToPreviousPage();
            imagesBean.update();
            return true;
        }
        return false;
    }

    public String getNext()
    {
        return next;
    }

    public void setNext(String next)
    {
        this.next = next;
    }

    public String getPrevious()
    {
        return previous;
    }

    public void setPrevious(String previous)
    {
        this.previous = previous;
    }
}
