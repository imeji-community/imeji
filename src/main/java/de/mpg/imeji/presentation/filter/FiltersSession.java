/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.filter;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.presentation.facet.Facet.FacetType;

/**
 * Session where the {@link Filter} are stored
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class FiltersSession
{
    private List<Filter> filters = new ArrayList<Filter>();
    private String wholeQuery = "";
    private List<Filter> noResultsFilters = new ArrayList<Filter>();

    /**
     * Check if the name correspond to an existing filter name
     * 
     * @param name
     * @return
     */
    public boolean isFilter(String name)
    {
        for (Filter f : filters)
        {
            if (f.getLabel().equalsIgnoreCase(name))
                return true;
            if (f.getLabel().equalsIgnoreCase("No " + name))
                return true;
        }
        return false;
    }

    /**
     * Check if the filter has no results
     * 
     * @param name
     * @return
     */
    public boolean isNoResultFilter(String name)
    {
        for (Filter f : noResultsFilters)
        {
            if (f.getLabel().equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @return filter with the search query
     */
    public Filter getSearchFilter()
    {
        for (Filter f : filters)
        {
            if (f.getType() == FacetType.SEARCH)
            {
                return f;
            }
        }
        return null;
    }

    public List<Filter> getFilters()
    {
        return filters;
    }

    public void setFilters(List<Filter> filters)
    {
        this.filters = filters;
    }

    public String getWholeQuery()
    {
        return wholeQuery;
    }

    public void setWholeQuery(String wholeQuery)
    {
        this.wholeQuery = wholeQuery;
    }

    public List<Filter> getNoResultsFilters()
    {
        return noResultsFilters;
    }

    public void setNoResultsFilters(List<Filter> noResultsFilters)
    {
        this.noResultsFilters = noResultsFilters;
    }
}
