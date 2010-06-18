package de.mpg.escidoc.faces.item;

import org.apache.log4j.Logger;

import de.escidoc.schemas.item.x09.ItemDocument;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.QueryHelper;

/**
 * Extension for FACES solution of an item.
 * It contains some specific features to FACES like Alternative Image, person identifier.
 * @author saquet
 *
 */

public class FacesItemVO extends ItemVO
{
    Logger logger = Logger.getLogger(FacesItemVO.class);
    private SessionBean sessionBean = null;
    
    /**
     * The item of the toggled picture set. For example, if item has picture set a the alternative item is 
     * the same item (i.e same person and same emotion) but with picture-group b.
     */
    private FacesItemVO alternative = null;
    
    /**
     * Constructor for a FACES Item.
     * @param item
     */
    public FacesItemVO(ItemDocument item)
    {
        super(item);
    }
    
    public FacesItemVO(ItemVO item)
    {
        super(item);
    }
    
    /**
     * Retrieve the alternative item from the FW.
     * @return the alternative FacesItemVO. 
     * @throws Exception
     */
    public FacesItemVO getAlternative() throws Exception
    {
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        
        if (alternative != null)
        {
            return alternative;
        }
        else
        {
            String query = "escidoc.face.identifier=" + mdRecords.getValue("identifier")
                        + " and  escidoc.face.emotion=" + mdRecords.getValue("emotion");
            
            if ("a".equals(mdRecords.getValue("picture-group")))
            {
                query += " and escidoc.face.picture-group=" +  "b" + " and escidoc.content-model.objid=escidoc:faces40";
            }
            else if ("b".equals(mdRecords.getValue("picture-group")))
            {
                query += " and escidoc.face.picture-group=" +  "a" + " and escidoc.content-model.objid=escidoc:faces40";
            }

            logger.debug("query: " + query);
            QueryHelper queryHelper = new QueryHelper();
            queryHelper.executeQuery(query, 1, 1, null);
            alternative = new FacesItemVO(queryHelper.getFirstItem());
            sessionBean.setAlternativeItem(alternative);
            return alternative;
        }
    }
    
    public String getAlternativePictureGroup()
    {
        return null;
    }
    
   /**
    * Read the identifier of a person.
    * @return
    */
    public String getPersonId()
    {
        String id = mdRecords.getValue("identifer");
        
        if (id != null)
        {
            return id.substring(0, 3);
        }
        
        return null;
    }
    
}
