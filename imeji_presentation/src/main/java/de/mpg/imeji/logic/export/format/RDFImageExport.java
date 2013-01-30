/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format;

import java.util.HashMap;
import java.util.List;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.vo.Item;

/**
 * {@link RDFExport} for {@link Item}
 * 
 * @author saquet
 */
public class RDFImageExport extends RDFExport
{
    private String[] filteredTriples = { "http://imeji.org/terms/metadata/pos",
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://imeji.org/terms/metadata/id",
            "http://imeji.org/terms/id", "http://imeji.org/terms/metadata/searchValue",
            "http://purl.org/escidoc/metadata/profiles/0.1/pos" };

    @Override
    public void init()
    {
        modelURI = ImejiJena.imageModel;
        super.filteredTriples = this.filteredTriples;
    }

    @Override
    protected void initNamespaces()
    {
        super.namespaces = new HashMap<String, String>();
        super.namespaces.put("http://imeji.org/terms/", "imeji");
        super.namespaces.put("http://imeji.org/terms/metadata/", "imeji-metadata");
        super.namespaces.put("http://purl.org/escidoc/metadata/terms/0.1/", "eterms");
        super.namespaces.put("http://purl.org/dc/elements/1.1/", "dc");
        super.namespaces.put("http://purl.org/dc/terms/", "dcterms");
        super.namespaces.put("http://purl.org/escidoc/metadata/profiles/0.1/", "eprofiles");
        super.namespaces.put("http://xmlns.com/foaf/0.1/", "foaf");
        super.namespaces.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
    }

    @Override
    protected String openTagResource(String uri)
    {
        return "<imeji:image rdf:about=\"" + uri + "\">";
    }

    @Override
    protected String closeTagResource()
    {
        return "</imeji:image>";
    }

    @Override
    protected void filterResources(SearchResult sr)
    {
        // find profile related to search results
        Search s = new Search(SearchType.ALL, null);
        String q1 = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {  ?p a <http://imeji.org/terms/mdprofile> . "
                + "?p <http://imeji.org/terms/statement> ?s . ?s <http://imeji.org/terms/restricted> ?r .FILTER(?r='true'^^<http://www.w3.org/2001/XMLSchema#boolean>)}";
        List<String> statements = s.searchSimpleForQuery(q1, null);
        String q2 = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {  ?im a <http://imeji.org/terms/image> . "
                + "?im <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?s . ?s <http://imeji.org/terms/statement> ?st"
                + " ";
        boolean first = true;
        for (String stURI : statements)
        {
//            if (first)
//                q2 += ". FILTER(";
//            else
//                q2 += " && ";
//            q2 += "?st=<" + stURI + ">";
//            first = false;
        }
        if (!first)
            q2 += ")";
        q2 += "}";
        System.out.println(q2);
        filteredResources = s.searchSimpleForQuery(q2, null);
        System.out.println("filtered");
        for (String str : filteredResources)
        {
            System.out.println(str);
        }
    }
}
