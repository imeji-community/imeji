/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export;

import java.io.OutputStream;
import java.util.Map;

import org.apache.http.client.HttpResponseException;

import de.mpg.imeji.logic.export.format.ExplainSearch;
import de.mpg.imeji.logic.export.format.IngestItemsExport;
import de.mpg.imeji.logic.export.format.IngestMdProfileExport;
import de.mpg.imeji.logic.export.format.JenaExport;
import de.mpg.imeji.logic.export.format.RDFAlbumExport;
import de.mpg.imeji.logic.export.format.RDFCollectionExport;
import de.mpg.imeji.logic.export.format.RDFImageExport;
import de.mpg.imeji.logic.export.format.RDFProfileExport;
import de.mpg.imeji.logic.export.format.SitemapExport;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.vo.User;

/**
 * Export of data
 * 
 * @author saquet
 */
public abstract class Export
{
    /**
     * The {@link User} doing the export
     */
    protected User user;
    /**
     * The params in the url ofr the export
     */
    private Map<String, String[]> params;

    /**
     * Export a {@link SearchResult} in an {@link OutputStream}
     * 
     * @param out
     * @param sr
     */
    public abstract void export(OutputStream out, SearchResult sr);

    /**
     * Return the Mime-type of the http response
     * 
     * @return
     */
    public abstract String getContentType();

    /**
     * Initialize the {@link Export}
     */
    public abstract void init();

    /**
     * Factory to create an {@link Export} from url paramters
     * 
     * @param params
     * @return
     * @throws HttpResponseException
     */
    public static Export factory(Map<String, String[]> params) throws HttpResponseException
    {
        Export export = null;
        boolean supportedFormat = false;
        boolean supportedType = false;
        String format = getParam(params, "format");
        String type = getParam(params, "type");
        if (format == null || format.equals(""))
        {
            throw new HttpResponseException(400, "Required parameter 'format' is missing.");
        }
        if ("rdf".equals(format))
        {
            supportedFormat = true;
            if ("image".equalsIgnoreCase(type))
            {
                supportedType = true;
                export = new RDFImageExport();
            }
            else if ("collection".equalsIgnoreCase(type))
            {
                supportedType = true;
                export = new RDFCollectionExport();
            }
            else if ("album".equalsIgnoreCase(type))
            {
                supportedType = true;
                export = new RDFAlbumExport();
            }
            else if ("profile".equals(type))
            {
                supportedType = true;
                export = new RDFProfileExport();
            }
        }
        else if ("jena".equals(format))
        {
            supportedFormat = true;
            supportedType = true; // default, no type necessary here
            export = new JenaExport();
        }
        else if ("sitemap".equals(format))
        {
            supportedFormat = true;
            supportedType = true;// default, no type necessary here
            export = new SitemapExport();
        }
        else if ("xml".equals(format))
        {
            supportedFormat = true;
            if ("image".equals(type))
            {
                supportedType = true;
                export = new IngestItemsExport();
            }
            else if ("profile".equals(type))
            {
                supportedType = true;
                export = new IngestMdProfileExport();
            }
        }
        else if ("explain".equals(format))
        {
            supportedFormat = true;
            if ("search".equals(type))
            {
                supportedType = true;
                export = new ExplainSearch();
            }
        }
        if (!supportedType)
        {
            throw new HttpResponseException(400, "Type " + type + " is not supported.");
        }
        else if (!supportedFormat)
        {
            throw new HttpResponseException(400, "Format " + format + " is not supported.");
        }
        export.setParams(params);
        export.init();
        return export;
    }

    public String getParam(String s)
    {
        return getParam(params, s);
    }

    public static String getParam(Map<String, String[]> params, String s)
    {
        String[] values = params.get(s);
        if (values != null)
        {
            return values[0];
        }
        return null;
    }

    /**
     * @return
     */
    public Map<String, String[]> getParams()
    {
        return params;
    }

    /**
     * @param params
     */
    public void setParams(Map<String, String[]> params)
    {
        this.params = params;
    }

    /**
     * @return
     */
    public User getUser()
    {
        return user;
    }

    /**
     * @param user
     */
    public void setUser(User user)
    {
        this.user = user;
    }
}
