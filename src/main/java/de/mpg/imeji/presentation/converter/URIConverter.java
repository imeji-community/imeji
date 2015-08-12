/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.converter;

import java.net.URI;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

/**
 * {@link Converter} for {@link URI}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class URIConverter implements Converter {
  @Override
  public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
    if (arg2 == null || "".equals(arg2.trim()))
      return null;
    arg2 = arg2.replaceAll(" ", "");
    URI uri = null;
    try {
      uri = URI.create(URIUtil.encodeQuery(arg2));
    if (!uri.isAbsolute())
      uri = URI.create(URIUtil.encodeQuery("http://" + arg2));
    } catch (URIException e) {
      e.printStackTrace();
    }
    return uri;
  }

  @Override
  public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
    if (arg2 == null)
      return "";
    return arg2.toString().trim();
  }
}
