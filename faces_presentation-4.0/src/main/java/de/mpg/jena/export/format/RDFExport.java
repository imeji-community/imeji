package de.mpg.jena.export.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.mpg.jena.ImejiJena;
import de.mpg.jena.search.SearchResult;

public class RDFExport 
{
	private String[] forbiddenTriples = 
		{
			"http://imeji.mpdl.mpg.de/metadata/pos", 
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
			"http://imeji.mpdl.mpg.de/metadata/id",
			"http://imeji.mpdl.mpg.de/id"
		};
	
	private String indentatation = " ";
	
	public void export(OutputStream out, SearchResult sr)
	{
		exportIntoOut(sr, out);
	}

	private void exportIntoOut(SearchResult sr, OutputStream out)
	{
		StringWriter writer = new StringWriter();
		
		writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		writer.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">");

		for (String s : sr.getResults())
		{
			Resource resource = ImejiJena.imageModel.getResource(s);
			indent();
			newLine(writer);
			writer.append(openTagImage(s));
			writer.append(exportResource(resource).getBuffer());
			writer.append(closeTagImage());
			unIndent();
			newLine(writer);
			
		}
		
		
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
		indent();
		newLine(writer);
		for (StmtIterator iterator = r.listProperties(); iterator.hasNext();) 
		{
			Statement st = iterator.next();
			
			if (!isForbidden(st))
			{
				writer.append(openTag(st));
	
				try
				{
					if (st.getResource().getURI() == null)
					{				
						writer.append(exportResource(st.getResource()).getBuffer());
					}
					else
					{
						writer.append(tagValue(st));
					}
				}
				catch (Exception e) 
				{
					/* is not a resource*/
				}

				try 
				{
					writer.append(st.getLiteral().getString());
				} 
				catch (Exception e) 
				{
					/* is not a literal*/
				}
				
				if (!iterator.hasNext())
				{
					unIndent();
				}
				
				writer.append(closeTag(st));
				newLine(writer);
			}
		}
		return writer;
	}
	
	private String openTagImage(String uri)
	{
		return "<image rdf:about=\"" + uri +"\">";
	}
	
	private String closeTagImage()
	{
		return "</image>";
	}

	private String openTag(Statement st)
	{
		return "<" + st.getPredicate().getNameSpace() + ""+ st.getPredicate().getLocalName() + ">";
	}

	private String closeTag(Statement st)
	{
		return "</" + st.getPredicate().getLocalName() + ">";
	}

	private String tagValue(Statement st)
	{
		String s ="";
		try 
		{
			s = st.getResource().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;

	}
	
	private void newLine(StringWriter writer)
	{
		writer.append("\n" + indentatation);
	}
	
	private void indent()
	{
		indentatation += "    ";
	}
	
	private void unIndent()
	{
		indentatation = indentatation.substring(0, indentatation.length() - 4);
	}

	private boolean isForbidden(Statement st)
	{
		for (String s : forbiddenTriples) 
		{
			if (s.equals(st.getPredicate().getURI())) 
			{
				return true;
			}
		}
		return false;
	}
}
