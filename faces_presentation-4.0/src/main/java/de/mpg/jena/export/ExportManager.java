package de.mpg.jena.export;


import java.io.IOException;
import java.io.OutputStream;

import de.mpg.jena.search.SearchResult;

public class ExportManager 
{
	private OutputStream out;
	
	public ExportManager(OutputStream out) 
	{
		this.out = out;
	}
	
	public void export(SearchResult sr, String format)
	{
		Export export = Export.factory(format);
		
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
}
