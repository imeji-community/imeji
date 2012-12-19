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

public class FacetsBean
{
    private List<List<Facet>> facets = new ArrayList<List<Facet>>();

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
