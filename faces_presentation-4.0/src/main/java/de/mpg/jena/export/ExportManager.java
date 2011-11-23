package de.mpg.jena.export;


import java.io.IOException;
import java.io.OutputStream;

import de.mpg.jena.search.SearchResult;

public class ExportManager 
{
	private OutputStream out;
	private Export export;
	
	public ExportManager(OutputStream out, String format) 
	{
		this.out = out;
		export = Export.factory(format);
	}
	
	public void export(SearchResult sr)
	{
		if (export != null)
		{
			export.export(out, sr);
		}
		else
		{
			try 
			{
				out.write("Unknown format".getBytes());
			} 
			catch (IOException e) 
			{
				throw new RuntimeException(e);
			}
		}
		
	}
	
	public String getContentType()
	{
		return export.getContentType();
	}
}
