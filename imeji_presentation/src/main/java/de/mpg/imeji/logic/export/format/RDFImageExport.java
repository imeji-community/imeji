/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format;

import java.util.HashMap;

import de.mpg.imeji.logic.ImejiJena;

/**
 * Export in a pretty RDF (without technical triples) of images
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
}
