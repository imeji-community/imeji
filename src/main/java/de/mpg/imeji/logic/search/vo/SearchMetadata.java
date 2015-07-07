package de.mpg.imeji.logic.search.vo;

import java.net.URI;
import java.util.List;

import de.mpg.imeji.logic.vo.Metadata;

/**
 * Specific {@link SearchPair} for {@link Metadata} search
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchMetadata extends SearchPair {
  private URI statement;

  /**
   * Default Constructor
   */
  public SearchMetadata() {
    // TODO Auto-generated constructor stub
  }

  public SearchMetadata(SearchIndex index, SearchOperators operator, String value, URI statement) {
    this(index, operator, value, statement, false);
  }

  public SearchMetadata(SearchIndex index, SearchOperators operator, String value, URI statement,
      boolean not) {
    super(index, operator, value, not);
    this.statement = statement;
  }

  @Override
  public SEARCH_ELEMENTS getType() {
    return SEARCH_ELEMENTS.METADATA;
  }

  @Override
  public List<SearchElement> getElements() {
    return null;
  }

  public URI getStatement() {
    return statement;
  }

  public void setStatement(URI statement) {
    this.statement = statement;
  }
}
