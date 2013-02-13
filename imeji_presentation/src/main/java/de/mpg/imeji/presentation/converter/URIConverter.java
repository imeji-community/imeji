/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.converter;

import java.net.URI;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class URIConverter implements Converter
{
    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
    {
        return URI.create(arg2.trim());
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
    {
        if (arg2 == null)
            return "";
        return arg2.toString();
    }
}
