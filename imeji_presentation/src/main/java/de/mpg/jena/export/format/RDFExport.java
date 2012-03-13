package de.mpg.jena.export.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.mpg.jena.export.Export;
import de.mpg.jena.search.SearchResult;

public abstract class RDFExport extends Export
{
	protected String[] filteredTriples = {};
	protected Map<String, String> namespaces;
	protected Model model;

	@Override
	public void export(OutputStream out, SearchResult sr) 
	{
		initNamespaces();
		System.out.println(namespaces);
		exportIntoOut(sr, out);
	}

	@Override
	public String getContentType() 
	{
		return "application/xml";
	}

	@Override
	public abstract void init();
	
    protected abstract void initNamespaces();

	private void exportIntoOut(SearchResult sr, OutputStream out)
	{
		StringWriter writer = new StringWriter();

		writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		writer.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");

		for (String key : namespaces.keySet())
		{
			writer.append(" xmlns:" + namespaces.get(key) + "=\"" + key  + "\"");
		}

		writer.append(">");

		for (String s : sr.getResults())
		{
			Resource resource = model.getResource(s);
			newLine(writer);
			writer.append(openTagResource(s));
			writer.append(exportResource(resource).getBuffer());
			writer.append(closeTagResource());
		}
		newLine(writer);
		writer.append("</rdf:RDF>");

		try 
		{
			out.write(writer.getBuffer().toString().getBytes());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private StringWriter exportResource(Resource r) 
	{
		StringWriter writer = new StringWriter();

		newLine(writer);
		for (StmtIterator iterator = r.listProperties(); iterator.hasNext();) 
		{
			Statement st = iterator.next();
			
			if (!isFiltered(st))
			{
				try
				{
					writer.append(openTag(st, st.getResource().getURI()));

					if (st.getResource().getURI() == null)
					{				
						writer.append(exportResource(st.getResource()).getBuffer());
					}
				}
				catch (Exception e) 
				{
					/*Not a resource*/
				}

				try
				{
					if (st.getLiteral().toString() != null)
					{
						writer.append(openTag(st, null));
						writer.append(st.getLiteral().getString());
					}
				}
				catch (Exception e) 
				{
					/*Not a literal*/
				}

				writer.append(closeTag(st));
				newLine(writer);
			}
		}
		return writer;
	}

	protected abstract String openTagResource(String uri);

	protected abstract String closeTagResource();

	private String openTag(Statement st, String resourceURI)
	{
		String tag = "<" + getNamespace(st.getPredicate().getNameSpace()) + ":"+ st.getPredicate().getLocalName();

		if (resourceURI != null) 
		{
			tag += " rdf:resource=\"" + resourceURI + "\"";
		}

		tag += ">";

		return tag;
	}

	private String closeTag(Statement st)
	{
		return "</" + getNamespace(st.getPredicate().getNameSpace()) + ":" + st.getPredicate().getLocalName() + ">";
	}

	private String tagValue(Statement st)
	{
		String s ="";
		try 
		{
			s = st.getResource().toString();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return s;
	}
	
	private String getNamespace(String ns)
	{
		String ns1 = namespaces.get(ns);

		if (ns1 != null)
		{
			return ns1;
		}
		return ns;
	}

	private void newLine(StringWriter writer)
	{
		writer.append("\n");
	}

	private boolean isFiltered(Statement st)
	{
		for (String s : filteredTriples) 
		{
			if (s.equals(st.getPredicate().getURI())) 
			{
				return true;
			}
		}
		return false;
	}

}
