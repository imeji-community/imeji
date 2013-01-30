package de.mpg.imeji.logic.export.format;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.export.Export;
import de.mpg.imeji.logic.search.SearchResult;

/**
 * {@link Export} in rdf
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class RDFExport extends Export
{
    protected String[] filteredTriples = {};
    protected List<String> filteredResources = new ArrayList<String>();
    protected Map<String, String> namespaces;
    protected String modelURI;

    @Override
    public void export(OutputStream out, SearchResult sr)
    {
        initNamespaces();
        filterResources(sr);
        exportIntoOut(sr, out);
    }

    @Override
    public String getContentType()
    {
        return "application/xml";
    }

    @Override
    public abstract void init();

    /**
     * Initialize the namespaces used for the export
     */
    protected abstract void initNamespaces();

    /**
     * Check which {@link Resource} should be filtered
     * @param sr TODO
     */
    protected abstract void filterResources(SearchResult sr);

    private void exportIntoOut(SearchResult sr, OutputStream out)
    {
        ImejiJena.imejiDataSet.begin(ReadWrite.READ);
        try
        {
            Model model = ImejiJena.imejiDataSet.getNamedModel(modelURI);
            StringWriter writer = new StringWriter();
            writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
            for (String key : namespaces.keySet())
            {
                writer.append(" xmlns:" + namespaces.get(key) + "=\"" + key + "\"");
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
            out.write(writer.getBuffer().toString().getBytes());
        }
        catch (Exception e)
        {
            ImejiJena.imejiDataSet.commit();
            throw new RuntimeException("Error in export", e);
        }
        finally
        {
            ImejiJena.imejiDataSet.commit();
        }
    }

    /**
     * Write a {@link Resource} in rdf
     * 
     * @param r
     * @return
     */
    private StringWriter exportResource(Resource r)
    {
        StringWriter writer = new StringWriter();
        if (isFilteredResource(r))
        {
            return writer;
        }
        newLine(writer);
        for (StmtIterator iterator = r.listProperties(); iterator.hasNext();)
        {
            Statement st = iterator.next();
            if (!isFiltered(st))
            {
                try
                {
                    writer.append(openTag(st, st.getResource().getURI()));
                    writer.append(exportResource(st.getResource()).getBuffer());
                }
                catch (Exception e)
                {
                    /* Not a resource */
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
                    /* Not a literal */
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
        String tag = "<" + getNamespace(st.getPredicate().getNameSpace()) + ":" + st.getPredicate().getLocalName();
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

    /**
     * If the Statement is filtered (namespace has been defined as to be filtered), then return false
     * 
     * @param st
     * @return
     */
    private boolean isFiltered(Statement st)
    {
        // Check if the triple is filtered
        for (String s : filteredTriples)
        {
            if (s.equals(st.getPredicate().getURI()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * If the {@link Resource} is filtered (for instance a metadata which should not be displayed, because restricted to
     * current user), return true
     * 
     * @param r
     * @return
     */
    private boolean isFilteredResource(Resource r)
    {
        for (String s : filteredResources)
        {
            if (s.equals(r.getURI().toString()))
            {
                return true;
            }
        }
        return false;
    }
}
