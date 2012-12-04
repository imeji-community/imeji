/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format;

import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import de.mpg.imeji.logic.export.Export;
import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.collection.ViewCollectionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Export the information for the ingest issue
 * 
 * @author hnguyen
 */
public class IngestMdProfileExport extends Export
{
    @Override
    public void init()
    {
        // Not initialization so far
    }

    @Override
    public void export(OutputStream out, SearchResult sr)
    {
    	MetadataProfile md = ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getProfile();
    	
        try {
			JaxbIngestProfile.writeToOutputStream(md,out);
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
