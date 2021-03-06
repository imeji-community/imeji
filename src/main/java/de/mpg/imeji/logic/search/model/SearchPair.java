package de.mpg.imeji.logic.search.model;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.search.model.SearchIndex.SearchFields;

/**
 * Define a pair of a {@link SearchIndex} with a {@link String} value, related by a
 * {@link SearchOperators}<br/>
 * {@link SearchPair} are {@link SearchElement} of a {@link SearchQuery}
 *
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchPair extends SearchElement {
  private boolean not = false;
  private SearchIndex index;
  private SearchOperators operator;
  private String value;
  private SearchFields field;

  /**
   * Default constructor
   */
  public SearchPair() {

  }

  public SearchPair(SearchFields field, SearchOperators operator, String value, boolean not) {
    this.operator = operator;
    this.value = value;
    this.not = not;
    this.field = field;
  }

  public SearchIndex getIndex() {
    return index;
  }

  public void setIndex(SearchIndex index) {
    this.index = index;
  }

  public SearchOperators getOperator() {
    return operator;
  }

  public void setOperator(SearchOperators operator) {
    this.operator = operator;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public SEARCH_ELEMENTS getType() {
    return SEARCH_ELEMENTS.PAIR;
  }

  @Override
  public List<SearchElement> getElements() {
    return new ArrayList<SearchElement>();
  }

  public void setNot(boolean not) {
    this.not = not;
  }

  public boolean isNot() {
    return not;
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
