/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format;

import java.util.HashMap;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;

/**
 * {@link RDFExport} for {@link Item}
 * 
 * @author saquet
 */
public class RDFImageExport extends RDFExport
{
    /**
     * Triples that are not displayed. <br/>
     * IMPORTANT: fulltext might be a security issue, so never show
     */
    private String[] filteredTriples = { "http://imeji.org/terms/metadata/pos",
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://imeji.org/terms/metadata/id",
            "http://imeji.org/terms/id", "http://imeji.org/terms/metadata/searchValue",
            "http://purl.org/escidoc/metadata/profiles/0.1/pos", "http://imeji.org/terms/fulltext" };

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

    /*
     * Not implemented : needs of specifaction about restricted metadata (how to meke it really private...)
     * @see de.mpg.imeji.logic.export.format.RDFExport#filterResources(de.mpg.imeji.logic.search.SearchResult,
     * de.mpg.imeji.logic.vo.User)
     */
    @Override
    protected void filterResources(SearchResult sr, User user)
    {
        // List<String> restrictedMetadataWithCollection = ImejiSPARQL
        // .exec(SPARQLQueries.selectMetadataRestricted(), null);
        // filteredResources = new ArrayList<String>();
        // Security security = new Security();
        // for (String str : restrictedMetadataWithCollection)
        // {
        // String[] splited = SortHelper.SORT_VALUE_PATTERN.split(str);
        // // create an emty item with the URI of the collection
        // Item it = new Item();
        // it.setCollection(URI.create(splited[1]));
        // // Check if user is privileged viewer for this item
        // if (!security.isPrivilegedViewer(user, it))
        // {
        // filteredResources.add(splited[0]);
        // }
        // }
    }
}
