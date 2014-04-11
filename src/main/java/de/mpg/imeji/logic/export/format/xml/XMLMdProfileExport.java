/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format.xml;

import java.io.OutputStream;
import java.net.URI;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.export.format.XMLExport;
import de.mpg.imeji.logic.ingest.jaxb.JaxbUtil;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Export the information for the ingest issue
 * 
 * @author hnguyen
 */
public class XMLMdProfileExport extends XMLExport
{
    @Override
    public void init()
    {
        // No initialization so far
    }

    @Override
    public void export(OutputStream out, SearchResult sr)
    {
        SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        ProfileController pc = new ProfileController();
        if (sr.getNumberOfRecords() == 1)
        {
            try
            {
                JaxbUtil.writeToOutputStream(pc.retrieve(URI.create(sr.getResults().get(0)), session.getUser()), out);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            throw new RuntimeException(sr.getNumberOfRecords() + " profile(s) found. Only 1 profile should be found");
        }
    }
}
