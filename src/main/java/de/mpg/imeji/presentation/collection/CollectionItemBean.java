/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.io.IOException;

import javax.faces.context.FacesContext;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.image.ItemBean;
import de.mpg.imeji.presentation.image.SingleItemBrowse;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Bean for the detail item page when viewed within a collection
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CollectionItemBean extends ItemBean
{
    private String collectionId;
    private Navigation navigation;
    private CollectionItemsBean collectionImagesBean;

    public CollectionItemsBean getCollectionImagesBean()
    {
        return collectionImagesBean;
    }

    public void setCollectionImagesBean(CollectionItemsBean collectionImagesBean)
    {
        this.collectionImagesBean = collectionImagesBean;
    }

    public CollectionItemBean() throws Exception
    {
        super();
        this.prettyLink =  getSessionBean().getPrettySpacePage("pretty:EditImageOfCollection");
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }

    @Override
    public void initBrowsing()
    {
        String tempId = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("CollectionItemsBean.id");
        setBrowse(new SingleItemBrowse((CollectionItemsBean)BeanHelper.getSessionBean(CollectionItemsBean.class),
                getImage(), "collection", tempId));
    }

    @Override
    public void redirectToBrowsePage() throws IOException
    {
        FacesContext.getCurrentInstance().getExternalContext()
                .redirect(navigation.getCollectionUrl() + collectionId + "/" + navigation.getBrowsePath());
    }


    public String getCollectionId()
    {
        return collectionId;
    }

    public void setCollectionId(String collectionId)
    {
        this.collectionId = collectionId;
    }

    @Override
    public String getPageUrl()
    {
        return navigation.getCollectionUrl() + collectionId + "/" + navigation.ITEM.getPath() + "/" + getId();
    }

    @Override
    public String getNavigationString()
    {
        return getSessionBean().getPrettySpacePage("pretty:CollectionItem");
    }
}
