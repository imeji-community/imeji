/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.module.digilib;

import de.mpg.imeji.logic.digilib.Diglib;
import de.mpg.imeji.presentation.collection.CollectionImagesBean;

/**
 * Bean for the detail item page when viewed within a collection
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class DigilibAlbumsImageBean extends DigilibImagesBean
{
	
	private CollectionImagesBean collectionImagesBean;
	
    private Diglib digilibScaler;
    
    public DigilibAlbumsImageBean() throws Exception
    {
        super();
        
    	this.digilibScaler = new Diglib();
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
    public String getNavigationString()
    {
        return "pretty:DegilibAlumsItem";
    }
}
