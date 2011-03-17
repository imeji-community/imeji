package de.mpg.imeji.collection;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.util.BeanHelper;

public class CollectionImageBean extends ImageBean
{
    private String collectionId;
    private Navigation navigation;

    public CollectionImageBean() throws Exception
    {
        super();
        this.prettyLink = "pretty:EditImageOfCollection";
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
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
        return navigation.getApplicationUrl() + "collection/" + this.collectionId + "/image/" + this.getId();
    }
    
    public String getNavigationString()
    {
        return "pretty:viewImageCollection";
    }
    
}
