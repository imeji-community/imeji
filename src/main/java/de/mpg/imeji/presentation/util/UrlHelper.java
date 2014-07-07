/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.util;

import java.net.URI;

import javax.faces.context.FacesContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Some Method to read URLs
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UrlHelper
{
    /**
     * Return the value of a parameter in an url
     * 
     * @param parameterName
     * @return
     */
    public static String getParameterValue(String parameterName)
    {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterName);
    }

    /**
     * Return a value as boolean of a parameter in a url: true if value is1, false if value is -1
     * 
     * @param parameterName
     * @return
     */
    public static boolean getParameterBoolean(String parameterName)
    {
        String str = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterName);
        if ("1".equals(str))
        {
            return true;
        }
        return false;
    }

    /**
     * Check if the uri is valid
     * 
     * @param uri
     * @return
     */
    public static boolean isValidURI(URI uri)
    {
        try
        {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(uri.toString());
            // client.executeMethod(method);
            ProxyHelper.executeMethod(client, method);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Add to the url the parameter
     * 
     * @param url
     * @param param
     * @param value
     * @return
     */
    public static String addParameter(String url, String param, String value)
    {
        String[] params = url.split("\\?", 2);
        if (params.length > 1 && !"".equals(params[1]))
            return url + "&" + param + "=" + value;
        return url + "?" + param + "=" + value;
    }
}
