package de.mpg.escidoc.faces.util;

import javax.naming.InitialContext;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class ContextHelper
{
    public static ContextVO getContext(String contextId, String userHandle)
    {
	try
	{
	    InitialContext context = new InitialContext();
	    XmlTransforming xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
	    String contextXml = ServiceLocator.getContextHandler(userHandle).retrieve(contextId);
	    return xmlTransforming.transformToContext(contextXml);
	} 
	catch (Exception e)
	{
	   throw new RuntimeException("ContextHelper: Error retrieving Context: " + e);
	}
    }
}
