package de.mpg.imeji.logic.search.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchElement grouping {@link SearchElement}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchGroup extends SearchElement {
  private List<SearchElement> group;
  private boolean not;

  public SearchGroup() {
    group = new ArrayList<SearchElement>();
  }

  public void setGroup(List<SearchElement> group) {
    this.group = group;
  }

  public List<SearchElement> getGroup() {
    return group;
  }

  @Override
  public SEARCH_ELEMENTS getType() {
    return SEARCH_ELEMENTS.GROUP;
  }

  @Override
  public List<SearchElement> getElements() {
    return group;
  }

  public void setNot(boolean not) {
    this.not = not;
  }

  public boolean isNot() {
    return not;
  }
}
