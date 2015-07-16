package de.mpg.imeji.logic.search.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * A search query composed of {@link SearchElement}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchQuery extends SearchElement {
  /**
   * The elements of the {@link SearchQuery}
   */
  private List<SearchElement> elements = null;

  /**
   * Construct an empty {@link SearchQuery}
   */
  public SearchQuery() {
    elements = new ArrayList<SearchElement>();
  }

  /**
   * Construct a {@link SearchQuery} with a {@link List} of {@link SearchElement}
   * 
   * @param elements
   */
  public SearchQuery(List<SearchElement> elements) {
    this.elements = new ArrayList<SearchElement>(elements);
  }

  /**
   * Clear the {@link SearchElement} of the {@link SearchQuery}
   */
  public void clear() {
    elements.clear();
  }

  public void setElements(List<SearchElement> elements) {
    this.elements = elements;
  }

  @Override
  public List<SearchElement> getElements() {
    return elements;
  }

  @Override
  public SEARCH_ELEMENTS getType() {
    return SEARCH_ELEMENTS.QUERY;
  }
}
