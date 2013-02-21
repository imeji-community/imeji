/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format;

import java.util.Collection;
import java.util.HashMap;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.vo.User;

/**
 * {@link RDFExport} for {@link Collection}
 * 
 * @author Friederike Kleinfercher
 */
public class RDFCollectionExport extends RDFExport
{
    private String[] filteredTriples = { "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
            "http://purl.org/escidoc/metadata/profiles/0.1/pos", "http://imeji.org/terms/id",
            "http://imeji.org/terms/metadata/id" };

    @Override
    public void init()
    {
        modelURI = ImejiJena.collectionModel;
        super.filteredTriples = this.filteredTriples;
    }

    @Override
    protected void initNamespaces()
    {
        super.namespaces = new HashMap<String, String>();
        super.namespaces.put("http://imeji.org/terms/", "imeji");
        super.namespaces.put("http://imeji.org/terms/container/", "imeji-metadata");
        super.namespaces.put("http://purl.org/escidoc/metadata/terms/0.1/", "eterms");
        super.namespaces.put("http://purl.org/dc/elements/1.1/", "dc");
        super.namespaces.put("http://purl.org/dc/terms/", "dcterms");
        super.namespaces.put("http://xmlns.com/foaf/0.1/", "foaf");
        super.namespaces.put("http://purl.org/escidoc/metadata/profiles/0.1/", "eprofiles");
    }

    @Override
    protected String openTagResource(String uri)
    {
        return "<imeji:collection rdf:about=\"" + uri + "\">";
    }

    @Override
    protected String closeTagResource()
    {
        return "</imeji:collection>";
    }

    @Override
    protected void filterResources(SearchResult sr, User user)
    {
        // TODO Auto-generated method stub
    }
}
