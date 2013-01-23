/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata.util;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Date;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;
import de.mpg.imeji.logic.vo.predefinedMetadata.License;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Number;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

public class MetadataHelper
{
    public static boolean isEmpty(Metadata md)
    {
        if (md instanceof Text)
        {
            if (((Text)md).getText() == null || "".equals(((Text)md).getText()))
                return true;
        }
        else if (md instanceof Date)
        {
            if (((Date)md).getDate() == null || "".equals(((Date)md).getDate()) || Double.isNaN(((Date)md).getTime()))
                return true;
        }
        else if (md instanceof Geolocation)
        {
            return Double.isNaN(((Geolocation)md).getLatitude()) || Double.isNaN(((Geolocation)md).getLongitude());
        }
        else if (md instanceof License)
        {
            if (((License)md).getLicense() == null || "".equals(((License)md).getLicense()))
                return true;
        }
        else if (md instanceof Publication)
        {
            if (((Publication)md).getUri() == null || "".equals(((Publication)md).getUri().toString()))
                return true;
        }
        else if (md instanceof Number)
        {
            return Double.isNaN(((Number)md).getNumber());
        }
        else if (md instanceof ConePerson)
        {
            if (((ConePerson)md).getPerson() == null || ((ConePerson)md).getPerson().getFamilyName() == null
                    || "".equals(((ConePerson)md).getPerson().getFamilyName()))
                return true;
        }
        else if (md instanceof Link)
        {
            if (((Link)md).getUri() == null || "".equals(((Link)md).getUri().toString()))
                return true;
        }
        return false;
    }

    public static Metadata setConeID(Metadata md)
    {
        if (md.getTypeNamespace().equals(Metadata.Types.CONE_PERSON.getClazzNamespace()))
        {
            String id = ((ConePerson)md).getPerson().getIdentifier();
            try
            {
                if (id.contains("http"))
                {
                    ((ConePerson)md).setConeId(java.net.URI.create(id));
                    return md;
                }
            }
            catch (Exception e)
            {
                BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error")
                        + " CONE ID");
            }
            ((ConePerson)md).setConeId(null);
        }
        return md;
    }
}
