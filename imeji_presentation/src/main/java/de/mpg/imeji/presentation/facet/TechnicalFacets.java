/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.facet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.facet.Facet.FacetType;
import de.mpg.imeji.presentation.filter.Filter;
import de.mpg.imeji.presentation.filter.FiltersSession;
import de.mpg.imeji.presentation.image.ImagesBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

public class TechnicalFacets
{
    private SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    private FiltersSession fs = (FiltersSession)BeanHelper.getSessionBean(FiltersSession.class);
    private List<List<Facet>> facets = new ArrayList<List<Facet>>();

    public TechnicalFacets(SearchQuery searchQuery)
    {
        FacetURIFactory uriFactory = new FacetURIFactory(searchQuery);
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        String baseURI = nav.getBrowseUrl() + "?q=";
        List<Facet> techFacets = new ArrayList<Facet>();
        try
        {
            SearchResult allImages = retrieveAllImages(searchQuery);
            int count = 0;
            int sizeAllImages = allImages.getNumberOfRecords();
            if (sizeAllImages > 0)
            {
                if (sb.getUser() != null)
                {
                    if (!fs.isFilter("my_images") && !fs.isNoResultFilter("my_images"))
                    {
                        SearchPair myImageSearchPair = new SearchPair(Search.getIndex(SearchIndex.names.user),
                                SearchOperators.EQUALS, sb.getUser().getEmail());
                        count = getCount(searchQuery, myImageSearchPair, allImages.getResults());
                        if (count > 0)
                        {
                            techFacets.add(new Facet(uriFactory.createFacetURI(baseURI, myImageSearchPair, "my_images",
                                    FacetType.TECHNICAL), "my_images", count, FacetType.TECHNICAL, null));
                        }
                        else
                        {
                            fs.getNoResultsFilters().add(new Filter("My images", "", 0, FacetType.TECHNICAL, null));
                        }
                    }
                    if (!fs.isFilter("pending_images") && !fs.isNoResultFilter("pending_images"))
                    {
                        SearchPair privatePair = new SearchPair(Search.getIndex(SearchIndex.names.status),
                                SearchOperators.URI, Status.PENDING.getUri().toString());
                        count = getCount(searchQuery, privatePair, allImages.getResults());
                        if (count > 0)
                        {
                            techFacets.add(new Facet(uriFactory.createFacetURI(baseURI, privatePair, "pending_images",
                                    FacetType.TECHNICAL), "pending_images", count, FacetType.TECHNICAL, null));
                        }
                    }
                    if (!fs.isFilter("released_images") && !fs.isNoResultFilter("released_images"))
                    {
                        SearchPair publicPair = new SearchPair(Search.getIndex(SearchIndex.names.status),
                                SearchOperators.URI, Status.RELEASED.getUri().toString());
                        count = getCount(searchQuery, publicPair, allImages.getResults());
                        if (count > 0)
                        {
                            techFacets.add(new Facet(uriFactory.createFacetURI(baseURI, publicPair, "released_images",
                                    FacetType.TECHNICAL), "released_images", count, FacetType.TECHNICAL, null));
                        }
                    }
                }
                for (Metadata.Types t : Metadata.Types.values())
                {
                    if (!fs.isFilter(t.name()) && !fs.isNoResultFilter(t.name()))
                    {
                        SearchPair pair = new SearchPair(Search.getIndex(SearchIndex.names.type), SearchOperators.URI,
                                t.getClazzNamespace());
                        count = getCount(searchQuery, pair, allImages.getResults());
                        if (count > 0)
                        {
                            techFacets.add(new Facet(uriFactory.createFacetURI(baseURI, pair, t.name(),
                                    FacetType.TECHNICAL), t.toString(), count, FacetType.TECHNICAL, null));
                        }
                        else
                        {
                            fs.getNoResultsFilters().add(new Filter(t.toString(), "", 0, FacetType.TECHNICAL, null));
                        }
                        count = 0;
                    }
                }
            }
            facets.add(techFacets);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve all {@link Item} currently browsed
     * 
     * @param searchQuery
     * @return
     */
    public SearchResult retrieveAllImages(SearchQuery searchQuery)
    {
        return ((ImagesBean)BeanHelper.getSessionBean(ImagesBean.class)).getSearchResult();
    }

    /**
     * Count the number of item for a facet with one {@link SearchPair}
     * 
     * @param searchQuery
     * @param pair
     * @param allImages
     * @return
     */
    public int getCount(SearchQuery searchQuery, SearchPair pair, List<String> allImages)
    {
        ItemController ic = new ItemController(sb.getUser());
        SearchQuery sq = new SearchQuery();
        sq.addPair(pair);
        return ic.search(null, sq, null, allImages).getNumberOfRecords();
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
