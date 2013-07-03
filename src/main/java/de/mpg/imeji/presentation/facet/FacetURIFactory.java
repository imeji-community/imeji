/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.facet;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.presentation.facet.Facet.FacetType;
import de.mpg.imeji.presentation.search.URLQueryTransformer;

/**
 * Factory for URI used in Facets and Filters
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class FacetURIFactory
{
    private SearchQuery searchQuery = new SearchQuery();

    /**
     * Constructor for one {@link SearchQuery}
     * 
     * @param searchQuery
     */
    public FacetURIFactory(SearchQuery searchQuery)
    {
        this.searchQuery = searchQuery;
    }

    /**
     * Create an {@link URI} attached to a {@link Facet} (for the link on which the user click to see the facet)
     * 
     * @param baseURI
     * @param pair
     * @param facetName
     * @param type
     * @return
     * @throws UnsupportedEncodingException
     */
    public URI createFacetURI(String baseURI, SearchPair pair, String facetName, FacetType type)
            throws UnsupportedEncodingException
    {
        SearchQuery sq = new SearchQuery(searchQuery.getElements());
        sq.addLogicalRelation(LOGICAL_RELATIONS.AND);
        sq.addPair(pair);
        String uri = baseURI + getCommonURI(sq, facetName, type);
        return URI.create(uri);
    }

    /**
     * Return parameters part of {@link URI} for this {@link Facet}
     * 
     * @param sq
     * @param facetName
     * @param type
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getCommonURI(SearchQuery sq, String facetName, FacetType type) throws UnsupportedEncodingException
    {
        return URLQueryTransformer.transform2UTF8URL(sq) + "&f=" + URLEncoder.encode(facetName, "UTF-8") + "&t="
                + URLEncoder.encode(type.name().toLowerCase(), "UTF-8") + "&page=1";
    }
}
