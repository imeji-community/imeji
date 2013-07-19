/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.module.digilib;

import java.io.IOException;
import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.digilib.Diglib;
import de.mpg.imeji.presentation.collection.CollectionImagesBean;
import de.mpg.imeji.presentation.image.SingleImageBrowse;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Bean for the detail item page when viewed within a collection
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class DigilibCollectionImageBean extends DigilibImageBean
{
	
	private CollectionImagesBean collectionImagesBean;
	
    private Diglib digilibScaler;
    
    public DigilibCollectionImageBean() throws Exception
    {
        super();
        
    	this.digilibScaler = new Diglib();
        
        this.prettyLink = "pretty:EditImageOfDigilib";
    }
    
    /**
     * Scale the image using the DIGILIB scaler.
     * @param sid
     * @param uri
     * @param query
     * @return
     */
    public byte[] getScaledImage(String uri, String query) {
    	return this.digilibScaler.getScaledImage(uri, query);
    }
    
    public CollectionImagesBean getCollectionImagesBean()
    {
        return collectionImagesBean;
    }

    public void setCollectionImagesBean(CollectionImagesBean collectionImagesBean)
    {
        this.collectionImagesBean = collectionImagesBean;
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
                .redirect(navigation.getDigilibUrl() + "/" + navigation.ITEM.getPath() + "/" + getId() + "/" + navigation.getBrowsePath());
    }

    @Override
    public String getPageUrl()
    {
        return navigation.getDigilibUrl() + "/" + navigation.ITEM.getPath() + "/" + getId();
    }

    @Override
    public String getNavigationString()
    {
        return "pretty:DegilibCollectionItem";
    }
}
