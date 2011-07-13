package de.mpg.imeji.converter;

import java.net.URI;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class URIConverter implements Converter
{

    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
    {
        return URI.create(arg2.trim());
    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
    {
        return arg2.toString();
    }
}
