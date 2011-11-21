package de.mpg.jena.export;


import java.io.OutputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.mpg.jena.ImejiJena;
import de.mpg.jena.export.format.RDFExport;
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
		if("rdf".equals(format))
		{
			RDFExport rdfExport = new RDFExport();
			rdfExport.export(out, sr);
		}
		else
		{
			// Default, copy only what is stored in Jena
			Model m = exportIntoModel(sr);
			m.write(out,"RDF/XML-ABBREV");
		}
	}
	
	private Model exportIntoModel(SearchResult sr)
	{
		Model exportModel = TDBFactory.createModel();

		for (String s : sr.getResults())
		{
			try 
			{
				Resource resource = ImejiJena.imageModel.getResource(s);

				exportResource(resource, exportModel);

			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return exportModel;
	}

	private Model exportResource(Resource r, Model m)
	{
		for (StmtIterator iterator = r.listProperties(); iterator.hasNext();) 
		{
			Statement st = iterator.next();

			try
			{
				if (st.getResource().getURI() == null)
				{				
					exportResource(st.getResource(), m);
				}
			}
			catch (Exception e) 
			{
				// Not to be handle
			}

			m.add(st);

		}
		return m;
	}
}
