/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format;

import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.export.Export;
import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.ingest.vo.Items;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.collection.ViewCollectionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Export the information for the ingest issue
 * 
 * @author hnguyen
 */
public class IngestItemsExport extends Export
{
    @Override
    public void init()
    {
        // Not initialization so far
    }

    @Override
    public void export(OutputStream out, SearchResult sr)
    {
    	
    	SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    	CollectionImeji collection = ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getCollection();
        
    	ItemController ic = new ItemController(session.getUser());
        Items items = new Items(ic.retrieveAllFromCollection(collection));
        
        try {
			JaxbIngestProfile.writeToOutputStream(items,out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   		
    }

    @Override
    public String getContentType()
    {
        return "application/xml";
    }
}
