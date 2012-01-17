/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.export;

import java.io.OutputStream;
import java.util.Map;

import org.apache.http.client.HttpResponseException;

import de.mpg.jena.export.format.JenaExport;
import de.mpg.jena.export.format.RDFAlbumExport;
import de.mpg.jena.export.format.RDFCollectionExport;
import de.mpg.jena.export.format.RDFImageExport;
import de.mpg.jena.export.format.SitemapExport;
import de.mpg.jena.search.SearchResult;

public abstract class Export 
{
	private Map<String, String[]> params;

	public abstract void export(OutputStream out, SearchResult sr);
	public abstract String getContentType();
	public abstract void init();

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

		if("rdf".equals(format))
		{	supportedFormat = true;
			
			if (type == null || type.equals(""))
			{
				throw new HttpResponseException(400, "Required parameter 'type' is missing.");
			}
			
			if (type.equalsIgnoreCase("image"))
			{
				supportedType = true;
				export = new RDFImageExport();
			}
			else if (type.equalsIgnoreCase("collection"))
			{
				supportedType = true;
				export = new RDFCollectionExport();
			}		
			else if (type.equalsIgnoreCase("album"))
			{
				supportedType = true;
				export = new RDFAlbumExport();
			}
		}
		
		else if ("jena".equals(format) || format == null)
		{	supportedFormat = true;
			supportedType = true; //default, no type necessary here
			
			export = new JenaExport();
		}
		
		else if("sitemap".equals(format))
		{	supportedFormat = true;
			supportedType = true;//default, no type necessary here
			
			export = new SitemapExport();
		}
		
		if (!supportedFormat)
		{
			throw new HttpResponseException(400, "Format "+format+" is not supported.");
		}
		if (!supportedType)
		{
			throw new HttpResponseException(400, "Type "+type+" is not supported.");
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

	public Map<String, String[]> getParams() 
	{
		return params;
	}
	public void setParams(Map<String, String[]> params) 
	{
		this.params = params;
	}


}
