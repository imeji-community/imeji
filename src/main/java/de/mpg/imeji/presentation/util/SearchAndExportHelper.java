/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.util;

import java.net.URI;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;

public class SearchAndExportHelper
{
    private static Pattern ESCIDOC_ID_PATTERN = Pattern.compile("escidoc:[0-9]+");

    public static String getCitation(Publication publication)
    {
        URI uri = publication.getUri();
        URI searchAndExportUri = URI.create("http://" + uri.getHost() + "/search/SearchAndExport");
        String itemId = null;
        Matcher matcher = ESCIDOC_ID_PATTERN.matcher(uri.toString());
        if (matcher.find())
            itemId = matcher.group();
        if (UrlHelper.isValidURI(searchAndExportUri))
        {
            try
            {
                HttpClient client = new HttpClient();
                String exportUri = searchAndExportUri.toString()
                        + "?cqlQuery="
                        + URLEncoder.encode("escidoc.objid=" + itemId + " or escidoc.property.version.objid=" + itemId,
                                "UTF-8") + "&exportFormat=" + publication.getExportFormat()
                        + "&outputFormat=html_linked";
                System.out.println(exportUri);
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
