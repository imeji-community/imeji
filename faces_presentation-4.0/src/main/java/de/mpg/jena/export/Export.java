package de.mpg.jena.export;

import java.io.OutputStream;

import de.mpg.jena.export.format.JenaExport;
import de.mpg.jena.export.format.RDFExport;
import de.mpg.jena.search.SearchResult;

public abstract class Export 
{
	public abstract void export(OutputStream out, SearchResult sr);
	public abstract String getContentType();

	public static Export factory(String format)
	{
		Export export = null;

		if("rdf".equals(format))
		{
			export = new RDFExport();
		}
		else if ("jena".equals(format) || format == null)
		{
			export = new JenaExport();
		}

		return export;
	}
}
