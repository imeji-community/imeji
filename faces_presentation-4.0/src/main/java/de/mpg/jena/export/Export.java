/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.export;

import java.io.OutputStream;
import java.util.Map;

import de.mpg.jena.export.format.JenaExport;
import de.mpg.jena.export.format.RDFExport;
import de.mpg.jena.export.format.SitemapExport;
import de.mpg.jena.search.SearchResult;

public abstract class Export 
{
	private Map<String, String[]> params;

	public abstract void export(OutputStream out, SearchResult sr);
	public abstract String getContentType();
	public abstract void init();

	public static Export factory(Map<String, String[]> params)
	{
		Export export = null; 
		
		String format = getParam(params, "format");

		if("rdf".equals(format))
		{
			export = new RDFExport();
		}
		else if ("jena".equals(format) || format == null)
		{
			export = new JenaExport();
		}
		else if("sitemap".equals(format))
		{
			export = new SitemapExport();
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
