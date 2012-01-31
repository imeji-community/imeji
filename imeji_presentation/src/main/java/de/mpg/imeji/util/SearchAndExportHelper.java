/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.util;

import java.net.URI;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import de.mpg.jena.vo.complextypes.Publication;

public class SearchAndExportHelper
{
    public static String getCitation(Publication publication)
    {
        URI uri = publication.getUri();
        URI searchAndExportUri = URI.create("http://" + uri.getHost() + "/search/SearchAndExport");
        String itemId = null;
        if (uri.getQuery() != null && uri.getQuery().contains("itemId="))
        {
            itemId = uri.getQuery().split("itemId=")[1];
        }
        else if (uri.getPath() != null && uri.getPath().contains("/item/"))
        {
            itemId = uri.getPath().split("/item/")[1];
        }
        if (UrlHelper.isValidURI(searchAndExportUri))
        {
            try
            {
                HttpClient client = new HttpClient();
                String exportUri = searchAndExportUri.toString()
                        + "?cqlQuery="
                        + URLEncoder.encode("escidoc.objid=" + itemId + " or escidoc.property.version.objid=" + itemId,
                                "ISO-8859-1") + "&exportFormat=" + publication.getExportFormat()
                        + "&outputFormat=html_linked";
                GetMethod method = new GetMethod(exportUri);
                client.executeMethod(method);
                return method.getResponseBodyAsString();
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return null;
    }
}
