/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.filter;

import de.mpg.imeji.logic.search.query.URLQueryTransformer;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.presentation.facet.Facet;
import org.apache.log4j.Logger;

import java.net.URI;

/**
 * {@link Facet} with extended
 * 
 * @author saquet
 */
public class Filter extends Facet
{
    private String query = "";
    private URI collectionID;
    private String label = "Search";
    private int count = 0;
    private String removeQuery = "";
    private SearchQuery searchQuery;

    /**
     * Constructor
     * 
     * @param label
     * @param query
     * @param count
     * @param type
     * @param metadataURI
     */
    public Filter(String label, String query, int count, FacetType type, URI metadataURI)
    {
        super(null, label, count, type, metadataURI);
        this.label = label;
        this.query = query;
        this.count = count;
        init();
    }

    /**
     * Initialize the {@link Filter}
     */
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
        	Logger.getLogger(Filter.class).error("Some issues during Filter initialization", e);
        }
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Getter
     * 
     * @return
     */
    public URI getCollectionID()
    {
        return collectionID;
    }

    /**
     * Setter
     * 
     * @param collectionID
     */
    public void setCollectionID(URI collectionID)
    {
        this.collectionID = collectionID;
    }

    @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public void setCount(int count)
    {
        this.count = count;
    }

    /**
     * Getter
     * 
     * @return
     */
    public String getQuery()
    {
        return query;
    }

    /**
     * setter
     * 
     * @param query
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getRemoveQuery()
    {
        return removeQuery;
    }

    /**
     * setter
     * 
     * @param removeQuery
     */
    public void setRemoveQuery(String removeQuery)
    {
        this.removeQuery = removeQuery;
    }

    /**
     * getter
     * 
     * @return
     */
    public SearchQuery getSearchQuery()
    {
        return searchQuery;
    }

    /**
     * setter
     * 
     * @param searchQuery
     */
    public void setSearchQuery(SearchQuery searchQuery)
    {
        this.searchQuery = searchQuery;
    }
}
