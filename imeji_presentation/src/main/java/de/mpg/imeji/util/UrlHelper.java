/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.util;

import java.net.URI;

import javax.faces.context.FacesContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import de.mpg.imeji.beans.SessionBean;

public class UrlHelper
{
    public static String getParameterValue(String parameterName)
    {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterName);
    }

    public static boolean getParameterBoolean(String parameterName)
    {
        String str = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterName);
        if ("1".equals(str))
        {
            return true;
        }
        return false;
    }

    public static boolean isValidURI(URI uri)
    {
        try
        {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(uri.toString());
            client.executeMethod(method);
            return true;
        }
        catch (Exception e)
        {
        	BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("error") + " (Non valid URL): " + e);
        }
        return false;
    }
}
