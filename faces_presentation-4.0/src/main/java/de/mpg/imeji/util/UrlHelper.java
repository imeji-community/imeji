package de.mpg.imeji.util;

import javax.faces.context.FacesContext;

public class UrlHelper
{
    public static String getParameterValue(String parameterName)
    {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterName);
    }
    
}
