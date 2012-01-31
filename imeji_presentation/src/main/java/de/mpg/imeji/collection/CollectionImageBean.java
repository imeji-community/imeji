/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.collection;

import java.io.Serializable;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.image.SingleImageBrowse;
import de.mpg.imeji.util.BeanHelper;

public class CollectionImageBean extends ImageBean implements Serializable
{
    private String collectionId;
    private Navigation navigation;

    public CollectionImageBean() throws Exception
    {
        super();
        this.prettyLink = "pretty:EditImageOfCollection";
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }
    
    public void initBrowsing()
    {
    	setBrowse(new SingleImageBrowse((CollectionImagesBean) BeanHelper.getSessionBean(CollectionImagesBean.class), getImage()));
    }

    public String getCollectionId()
    {
        return collectionId;
    }

    public void setCollectionId(String collectionId)
    {
        this.collectionId = collectionId;
    }

    public String getPageUrl()
    {
        return navigation.getApplicationUrl() + "collection/" + collectionId + "/image/" + this.getId();
    }
    
    public String getNavigationString()
    {
        return "pretty:viewImageCollection";
    }
    
}
