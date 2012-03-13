package de.mpg.jena.export.format;

import java.util.HashMap;

import de.mpg.jena.ImejiJena;

/**
 * Export in a pretty RDF (without technical triples) of metadata profile
 * 
 * @author saquet
 *
 */
public class RDFProfileExport extends RDFExport
{
	private String[] filteredTriples = 
	{
			"http://imeji.mpdl.mpg.de/metadata/pos", 
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
			"http://imeji.mpdl.mpg.de/metadata/id",
			"http://imeji.mpdl.mpg.de/id",
			"http://imeji.mpdl.mpg.de/metadata/searchValue",
			"http://purl.org/escidoc/metadata/profiles/0.1/pos"
	};
	
	@Override
	public void init() 
	{
		model = ImejiJena.profileModel;
		super.filteredTriples = this.filteredTriples;
	}

	@Override
	protected void initNamespaces() 
	{
		super.namespaces = new HashMap<String, String>();
		super.namespaces.put("http://imeji.mpdl.mpg.de/", "imeji");
		super.namespaces.put("http://imeji.mpdl.mpg.de/metadata/", "imeji-metadata");
		super.namespaces.put("http://purl.org/escidoc/metadata/terms/0.1/", "eterms");
		super.namespaces.put("http://purl.org/dc/elements/1.1/", "dcterms");
		super.namespaces.put("http://purl.org/escidoc/metadata/profiles/0.1/", "eprofiles");
	}

	@Override
	protected String openTagResource(String uri) 
	{
		return "<imeji:mdprofile rdf:about=\"" + uri +"\">";
	}

	@Override
	protected String closeTagResource() 
	{
		return "</imeji:mdprofile>";
	}


}
