package de.mpg.imeji.logic.search.model;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.search.jenasearch.JenaSearch;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.util.MetadataTypesHelper;

/**
 * Element of a {@link SearchPair}, defines the index of the searched elements
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchIndex {
  /**
   * All indexes names, searchable in imeji
   * 
   * @author saquet (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   */
  public static enum SearchFields {
    member, hasgrant, prop, creator, editor, created, modified, status, grant, grant_type, grant_for, filename, visibility, mds, col, alb, prof, metadatatype, cont_md, title, description, cont_person, author_familyname, author_givenname, author_name, cont_person_org, author_org_name, md, statement, all, text, number, date, time, location, coordinates, license, url, label, citation, cone, person, person_completename, person_family, person_given, person_id, person_role, person_org, person_org_name, person_org_id, person_org_description, person_org_city, person_org_country, checksum, filetype, filesize;
  }

  private String name;
  private String namespace;
  private SearchFields field;
  private SearchIndex parent;
  private List<SearchIndex> children = new ArrayList<SearchIndex>();
  private boolean listType = false;

  /**
   * Simple {@link SearchIndex} without namespace.
   * 
   * @param name
   * @param namespace
   */
  public SearchIndex(SearchFields field) {
    this.name = field.name();
    this.field = field;
  }

  /**
   * Construct a new {@link SearchIndex} with a name and a namespace
   * 
   * @param name
   * @param namespace
   */
  public SearchIndex(String name, String namespace) {
    this.name = name;
    this.namespace = namespace;
    this.field = SearchFields.valueOf(name);
  }

  /**
   * Construct a new {@link SearchIndex} with a name and a namespace and parent {@link SearchIndex}
   * 
   * @param name
   * @param namespace
   * @param parent
   */
  public SearchIndex(String name, String namespace, SearchIndex parent) {
    this(name, namespace);
    this.parent = parent;
    if (parent != null && !parent.getChildren().contains(this)) {
      parent.getChildren().add(this);
    }
  }

  /**
   * Return all the necessary {@link SearchIndex} to search for a {@link Metadata} defined with a
   * {@link Statement}
   * 
   * @param st
   * @return
   */
  public static List<SearchIndex> getAllIndexForStatement(Statement st) {
    List<SearchIndex> list = new ArrayList<SearchIndex>();
    switch (MetadataTypesHelper.getTypesForNamespace(st.getType().toString())) {
      case DATE:
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.time.name()));
        break;
      case GEOLOCATION:
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.location.name()));
        break;
      case LICENSE:
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.license.name()));
        break;
      case NUMBER:
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.number.name()));
        break;
      case CONE_PERSON:
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.person_family.name()));
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.person_given.name()));
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.person_org_name.name()));
        break;
      case PUBLICATION:
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.citation.name()));
        break;
      case TEXT:
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.text.name()));
        break;
      case LINK:
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.url.name()));
        list.add(JenaSearch.getIndex(SearchIndex.SearchFields.label.name()));
        break;
    }
    return list;
  }

  public boolean hasParent() {
    return getParent() != null;
  }

  public SearchIndex getParent() {
    return parent;
  }

  public void setParent(SearchIndex parent) {
    this.parent = parent;
  }

  public void setChildren(List<SearchIndex> children) {
    this.children = children;
  }

  public List<SearchIndex> getChildren() {
    return children;
  }

  public void setListType(boolean listType) {
    this.listType = listType;
  }

  public boolean isListType() {
    return listType;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * @return the field
   */
  public SearchFields getField() {
    return field;
  }

  /**
   * @param field the field to set
   */
  public void setField(SearchFields field) {
    this.field = field;
  }
}
