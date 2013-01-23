/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.filter;

import java.net.URI;

import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.presentation.facet.Facet;
import de.mpg.imeji.presentation.search.URLQueryTransformer;
/**
 * 
 * @author saquet
 *
 */
public class Filter extends Facet
{
    private String query = "";
    private URI collectionID;
    private String label = "Search";
    private int count = 0;
    private String removeQuery = "";
    private SearchQuery searchQuery;


    public Filter(String label, String query, int count, FacetType type, URI metadataURI)
    {
        super(null, label, count, type, metadataURI);
        this.label = label;
        this.query = query;
        this.count = count;
        init();
    }

    public void init()
    {
        if (label == null)
        {
            label = "Search";
        }
        try
        {
            if (FacetType.SEARCH == getType())
            {
                searchQuery = URLQueryTransformer.parseStringQuery(query);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public URI getCollectionID()
    {
        return collectionID;
    }

    public void setCollectionID(URI collectionID)
    {
        this.collectionID = collectionID;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getRemoveQuery()
    {
        return removeQuery;
    }

    public void setRemoveQuery(String removeQuery)
    {
        this.removeQuery = removeQuery;
    }

    public SearchQuery getSearchQuery()
    {
        return searchQuery;
    }

    public void setSearchQuery(SearchQuery searchQuery)
    {
        this.searchQuery = searchQuery;
    }
}
