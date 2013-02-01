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

public class FacetURIFactory
{
    private SearchQuery searchQuery = new SearchQuery();

    public FacetURIFactory(SearchQuery searchQuery)
    {
        this.searchQuery = searchQuery;
    }

    public URI createFacetURI(String baseURI, SearchPair pair, String facetName, FacetType type)
            throws UnsupportedEncodingException
    {
        SearchQuery sq = new SearchQuery(searchQuery.getElements());
        sq.addLogicalRelation(LOGICAL_RELATIONS.AND);
        sq.addPair(pair);
        String uri = baseURI + getCommonURI(sq, facetName, type);
        return URI.create(uri);
    }

    private String getCommonURI(SearchQuery sq, String facetName, FacetType type) throws UnsupportedEncodingException
    {
        String commonURI = "";
        commonURI += URLQueryTransformer.transform2UTF8URL(sq) + "&f=" + URLEncoder.encode(facetName, "UTF-8") + "&t="
                + URLEncoder.encode(type.name().toLowerCase(), "UTF-8") + "&page=1";
        return commonURI;
    }
}
