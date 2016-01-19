/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.facet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.model.SearchQuery;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Java Bean for the {@link Facet}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class FacetsBean implements Callable<Boolean> {
  private List<List<Facet>> facets = new ArrayList<List<Facet>>();
  private boolean running = false;
  private Facets facetsClass;

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public Boolean call() throws Exception {
    running = true;
    facetsClass.init();
    facets = facetsClass.getFacets();
    running = false;
    return running;
  }

  /**
   * Initialize the {@link FacetsBean} for one {@link SearchQuery} from the item browse page
   * 
   * @param searchQuery
   */
  public FacetsBean(SearchQuery searchQuery) {
    try {
      facetsClass = new TechnicalFacets(searchQuery);
    } catch (Exception e) {
      BeanHelper.error(((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
          .getLabel("error") + ", Technical Facets intialization: " + e.getMessage());
    }
  }

  /**
   * Initialize the {@link FacetsBean} for one {@link SearchQuery} from the collection browse page
   * 
   * @param col
   * @param searchQuery
   */
  public FacetsBean(CollectionImeji col, SearchQuery searchQuery, SearchResult searchRes) {
    try {
      facetsClass = new CollectionFacets(col, searchQuery, searchRes);
    } catch (Exception e) {
      BeanHelper.error(((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
          .getLabel("error") + ", Collection Facets intialization : " + e.getMessage());
    }
  }

  /**
   * getter
   * 
   * @return
   */
  public List<List<Facet>> getFacets() {
    return facets;
  }

  /**
   * setter
   * 
   * @param facets
   */
  public void setFacets(List<List<Facet>> facets) {
    this.facets = facets;
  }


  /**
   * @return the running
   */
  public boolean isRunning() {
    return running;
  }
}
