/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format.rdf;

import java.util.HashMap;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiNamespaces;
import de.mpg.imeji.logic.export.format.RDFExport;
import de.mpg.imeji.logic.resource.vo.Album;
import de.mpg.imeji.logic.resource.vo.User;
import de.mpg.imeji.logic.search.SearchResult;

/**
 * {@link RDFExport} for {@link Album}
 * 
 * @author Friederike Kleinfercher
 */
public class RDFAlbumExport extends RDFExport {
  private String[] filteredTriples = {"http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
      "http://purl.org/escidoc/metadata/profiles/0.1/pos", ImejiNamespaces.CREATOR,
      ImejiNamespaces.MODIFIED_BY};

  @Override
  public void init() {
    modelURI = Imeji.albumModel;
    super.filteredTriples = this.filteredTriples;
  }

  @Override
  protected void initNamespaces() {
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
  protected String openTagResource(String uri) {
    return "<imeji:album rdf:about=\"" + uri + "\">";
  }

  @Override
  protected String closeTagResource() {
    return "</imeji:album>";
  }

  @Override
  protected void filterResources(SearchResult sr, User user) {
    // TODO Auto-generated method stub
  }
}
