package de.mpg.imeji.logic.export.format;

import java.util.HashMap;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.vo.MetadataProfile;

/**
 * {@link RDFExport} for {@link MetadataProfile}
 * 
 * @author saquet
 */
public class RDFProfileExport extends RDFExport
{
    private String[] filteredTriples = { "http://imeji.org/terms/metadata/pos",
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://imeji.org/terms/metadata/id",
            "http://imeji.org/terms/id", "http://imeji.org/terms/metadata/searchValue",
            "http://purl.org/escidoc/metadata/profiles/0.1/pos" };

    @Override
    public void init()
    {
        modelURI = ImejiJena.profileModel;
        super.filteredTriples = this.filteredTriples;
    }

    @Override
    protected void initNamespaces()
    {
        super.namespaces = new HashMap<String, String>();
        super.namespaces.put("http://imeji.org/terms/", "imeji");
        super.namespaces.put("http://imeji.org/terms/metadata/", "imeji-metadata");
        super.namespaces.put("http://purl.org/escidoc/metadata/terms/0.1/", "eterms");
        super.namespaces.put("http://purl.org/dc/elements/1.1/", "dcterms");
        super.namespaces.put("http://purl.org/escidoc/metadata/profiles/0.1/", "eprofiles");
    }

    @Override
    protected String openTagResource(String uri)
    {
        return "<imeji:mdprofile rdf:about=\"" + uri + "\">";
    }

    @Override
    protected String closeTagResource()
    {
        return "</imeji:mdprofile>";
    }
}
