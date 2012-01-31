/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.export.format;

import java.io.OutputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.mpg.jena.ImejiJena;
import de.mpg.jena.export.Export;
import de.mpg.jena.search.SearchResult;

/**
 * Export data as they are in stored in Jena
 * 
 * @author saquet
 *
 */
public class JenaExport extends Export
{
	@Override
	public void init() 
	{
		// Not initialization so far
	}
	
	@Override
	public void export(OutputStream out, SearchResult sr)
	{
		Model m = exportIntoModel(sr);
		m.write(out,"RDF/XML");
	}
	
	@Override
	public String getContentType()
	{
		return "application/xml";
	}

	/**
	 * Create a model with all Search results
	 * @param sr
	 * @return
	 */
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

	/**
	 * Write all properties of a resource in the model
	 * @param r
	 * @param m
	 * @return
	 */
	private void exportResource(Resource r, Model m)
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
	}
}
