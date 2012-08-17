/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.presentation.facet.Facet.FacetType;
import de.mpg.imeji.presentation.search.URLQueryTransformer;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.UrlHelper;

public class FiltersBean
{
    private FiltersSession fs = (FiltersSession)BeanHelper.getSessionBean(FiltersSession.class);
    private int count = 0;

    public FiltersBean()
    {
    }

    public FiltersBean(String query, int count)
    {
        try
        {
            this.count = count;
            String q = UrlHelper.getParameterValue("q");
            String n = UrlHelper.getParameterValue("f");
            String t = UrlHelper.getParameterValue("t");
            if (n != null)
                n = n.toLowerCase();
            if (t == null)
                t = FacetType.SEARCH.name();
            if (q != null)
            {
                q = formatQuery(q);
                List<Filter> filters = parseQueryAndSetFilters(q, n, t);
                resetFiltersSession(q, filters);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reset elements in the filters session
     * 
     * @param q
     * @param filters
     */
    private void resetFiltersSession(String q, List<Filter> filters)
    {
        fs.setFilters(filters);
        if (!q.contains(fs.getWholeQuery()) || "".equals(q))
        {
            fs.getNoResultsFilters().clear();
        }
        fs.setWholeQuery(q);
    }

    /**
     * Parse the query and set the Filters
     * 
     * @param q
     * @param n
     * @param t
     * @return
     * @throws IOException
     */
    private List<Filter> parseQueryAndSetFilters(String q, String n, String t) throws IOException
    {
        System.out.println("complete query before parsing: " + q);
        List<Filter> filters = findAlreadyDefinedFilters(q, n, t);
        String newQuery = removeFiltersQueryFromQuery(q, filters);
        Filter newFilter = createNewFilter(newQuery, n, t);
        if (newFilter != null)
        {
            filters.add(newFilter);
        }
        resetQueriesToRemoveFilters(q, filters);
        return filters;
    }

    /**
     * Define the query as new filter. If the query has been already parsed and cleaned from previous filters, then the
     * created filter is equals to the filter clicked by the user.
     * 
     * @param q
     * @param n
     * @param t
     * @return
     * @throws IOException
     */
    private Filter createNewFilter(String q, String n, String t) throws IOException
    {
        if (q != null && !"".equals(q.trim()))
        {
            return new Filter(n, q, count, FacetType.valueOf(t.toUpperCase()), null);
        }
        return null;
    }

    /**
     * Find the filters which were alredy defined (in previous queries)
     * 
     * @param q
     * @param n
     * @param t
     * @return
     */
    private List<Filter> findAlreadyDefinedFilters(String q, String n, String t)
    {
        List<Filter> filters = new ArrayList<Filter>();
        for (Filter f : fs.getFilters())
        {
            if (q != null && q.contains(f.getQuery()))
            {
                filters.add(f);
            }
        }
        return filters;
    }

    /**
     * Reset the queries to remove the filters
     * 
     * @param q
     * @param filters
     * @return
     */
    private List<Filter> resetQueriesToRemoveFilters(String q, List<Filter> filters)
    {
        System.out.println("Complete query used to remove: " + q);
        for (Filter f : filters)
        {
            f.setRemoveQuery(removeFilterQueryFromQuery(q, f));
            System.out.println("remove query: " + f.getRemoveQuery());
            
        }
        return filters;
    }

    /**
     * Remove the filters from a query
     * 
     * @param q
     * @param filters
     * @return
     */
    private String removeFiltersQueryFromQuery(String q, List<Filter> filters)
    {
        for (Filter f : filters)
        {
            q = removeFilterQueryFromQuery(q, f);
        }
        return q;
    }

    /**
     * Remove one filter from a query
     * 
     * @param q
     * @param filter
     * @return
     */
    private String removeFilterQueryFromQuery(String q, Filter filter)
    {
        try
        {
            System.out.println("To be remove: " + filter.getQuery());
            if (!q.contains(filter.getQuery()))
            {
                throw new RuntimeException("non removable filter: " + q);
            }
            return URLEncoder.encode(q.replace(filter.getQuery(), ""), "UTF-8").trim();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return q;
    }

    /**
     * If q is the complete query, create a query with all information to remove the filter
     * 
     * @param f
     * @param q
     * @return
     */
    public String createQueryToRemoveFilter(Filter f, String q)
    {
        return removeFilterQueryFromQuery(q, f) + "&f=" + f.getLabel();
    }

    /**
     * Make transformation of the query from String to {@link SearchQuery} and from {@link SearchQuery} to String to
     * ensure the query to be always in the same format
     * 
     * @param q
     * @return
     * @throws IOException
     */
    private String formatQuery(String q) throws IOException
    {
        return URLEncoder
                .encode(URLQueryTransformer.transform2URL(URLQueryTransformer.parseStringQuery(q)), "UTF-8");
    }

    public FiltersSession getSession()
    {
        return fs;
    }
}
