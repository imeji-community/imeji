/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.module.digilib;

import java.io.IOException;
import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.digilib.Diglib;
import de.mpg.imeji.presentation.collection.CollectionImagesBean;
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
public class DigilibImageBean extends ImageBean
{
	
    private Diglib digilibScaler;
    
    public DigilibImageBean() throws Exception
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
 

    @Override
    public void initBrowsing()
    {
    	String tempId = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("DigilibImageBean.id");
        setBrowse(new SingleImageBrowse((CollectionImagesBean)BeanHelper.getSessionBean(CollectionImagesBean.class),
                getImage(), "digilibImage", tempId));
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
        return "pretty:DegilibItem";
    }
}
