/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format;

import java.io.OutputStream;
import java.net.URI;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.export.Export;
import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.presentation.session.SessionBean;
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
    	SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);        
    	ProfileController pc = new ProfileController(session.getUser());
    	if (sr.getNumberOfRecords() == 1)
    	{
    		try {
				JaxbIngestProfile.writeToOutputStream(pc.retrieve(URI.create(sr.getResults().get(0))),out);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		 
    	}
    	else
    	{
    		throw new RuntimeException(sr.getNumberOfRecords() + " profile(s) found. Only 1 profile sould be found");
    	}
    }       

    @Override
    public String getContentType()
    {
        return "application/xml";
    }
}
