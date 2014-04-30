/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.io.IOException;

import javax.faces.context.FacesContext;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.image.ImageBean;
import de.mpg.imeji.presentation.image.SingleImageBrowse;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Bean for the detail item page when viewed within a collection
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CollectionImageBean extends ImageBean
{
    private String collectionId;
    private Navigation navigation;
    private CollectionImagesBean collectionImagesBean;

    public CollectionImagesBean getCollectionImagesBean()
    {
        return collectionImagesBean;
    }

    public void setCollectionImagesBean(CollectionImagesBean collectionImagesBean)
    {
        this.collectionImagesBean = collectionImagesBean;
    }

    public CollectionImageBean() throws Exception
    {
        super();
        this.prettyLink = "pretty:EditImageOfCollection";
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }

    @Override
    public void initBrowsing()
    {
        String tempId = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("CollectionImagesBean.id");
        setBrowse(new SingleImageBrowse((CollectionImagesBean)BeanHelper.getSessionBean(CollectionImagesBean.class),
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
        return "pretty:CollectionItem";
    }
}
