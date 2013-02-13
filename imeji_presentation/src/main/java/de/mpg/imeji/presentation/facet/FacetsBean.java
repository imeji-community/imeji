/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.facet;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.search.vo.SearchQuery;
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
public class FacetsBean
{
    private List<List<Facet>> facets = new ArrayList<List<Facet>>();

    /**
     * Initialize the {@link FacetsBean} for one {@link SearchQuery} from the item browse page
     * 
     * @param searchQuery
     */
    public FacetsBean(SearchQuery searchQuery)
    {
        try
        {
            facets = new TechnicalFacets(searchQuery).getFacets();
        }
        catch (Exception e)
        {
            BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error")
                    + ", Facets intialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize the {@link FacetsBean} for one {@link SearchQuery} from the collection browse page
     * 
     * @param col
     * @param searchQuery
     */
    public FacetsBean(CollectionImeji col, SearchQuery searchQuery)
    {
        try
        {
            facets = new CollectionFacets(col, searchQuery).getFacets();
        }
        catch (Exception e)
        {
            BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error")
                    + ", Facets intialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<List<Facet>> getFacets()
    {
        return facets;
    }

    public void setFacets(List<List<Facet>> facets)
    {
        this.facets = facets;
    }
}
