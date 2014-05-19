/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format.xml;

import java.io.OutputStream;
import java.util.Collection;

import javax.xml.bind.JAXBException;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.export.format.XMLExport;
import de.mpg.imeji.logic.ingest.jaxb.JaxbUtil;
import de.mpg.imeji.logic.ingest.vo.Items;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Export the information for the ingest issue
 * 
 * @author hnguyen
 */
public class XMLItemsExport extends XMLExport
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
        ItemController ic = new ItemController(session.getUser());
        Collection<Item> itemList = ic.loadItems(sr.getResults(), -1, 0);
        Items items = new Items(itemList);
        try
        {
            JaxbUtil.writeToOutputStream(items, out);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }
}
