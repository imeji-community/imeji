/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import javax.faces.context.FacesContext;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.image.ImageBean;
import de.mpg.imeji.presentation.image.SingleImageBrowse;
import de.mpg.imeji.presentation.util.BeanHelper;

public class CollectionImageBean extends ImageBean
{
    private String collectionId;
    private Navigation navigation;
    private CollectionImagesBean collectionImagesBean;

    public CollectionImagesBean getCollectionImagesBean() {
		return collectionImagesBean;
	}

	public void setCollectionImagesBean(CollectionImagesBean collectionImagesBean) {
		this.collectionImagesBean = collectionImagesBean;
	}

	public CollectionImageBean() throws Exception
    {
        super();
        this.prettyLink = "pretty:EditImageOfCollection";
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }

    public void initBrowsing()
    {
    	String tempId=(String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("CollectionImagesBean.id");
        setBrowse(new SingleImageBrowse((CollectionImagesBean)BeanHelper.getSessionBean(CollectionImagesBean.class),
                getImage(),"collection", tempId));
       
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
        return navigation.getCollectionUrl() + collectionId + "/" + navigation.ITEM.getPath() + "/" + getId();
    }

    public String getNavigationString()
    {
        return "pretty:CollectionItem";
    }
}
