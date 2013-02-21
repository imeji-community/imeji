/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.facet;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.collection.CollectionImagesBean;
import de.mpg.imeji.presentation.facet.Facet.FacetType;
import de.mpg.imeji.presentation.filter.FiltersSession;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;

/**
 * Facets for the images browsed within a collection
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CollectionFacets
{
    private SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    private FiltersSession fs = (FiltersSession)BeanHelper.getSessionBean(FiltersSession.class);
    private List<List<Facet>> facets = new ArrayList<List<Facet>>();
    private URI colURI = null;

    /**
     * Constructor for the {@link Facet}s of one {@link CollectionImeji} with one {@link SearchQuery}
     * 
     * @param col
     * @param searchQuery
     * @throws Exception
     */
    public CollectionFacets(CollectionImeji col, SearchQuery searchQuery) throws Exception
    {
        this.colURI = col.getId();
        MetadataProfile profile = ObjectCachedLoader.loadProfile(col.getProfile());
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        String baseURI = nav.getApplicationUri() + col.getId().getPath() + "/" + nav.getBrowsePath() + "?q=";
        ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class)).init(profile);
        FacetURIFactory uriFactory = new FacetURIFactory(searchQuery);
        int count = 0;
        SearchResult allImages = retrieveAllImages(searchQuery);
        int sizeAllImages = allImages.getNumberOfRecords();
        for (Statement st : profile.getStatements())
        {
            List<Facet> group = new ArrayList<Facet>();
            if (st.isPreview() && !fs.isFilter(getName(st.getId())))
            {
                SearchPair pair = new SearchPair(Search.getIndex(SearchIndex.names.statement), SearchOperators.URI, st
                        .getId().toString());
                count = getCount(searchQuery, pair, allImages.getResults());
                if (count > 0 || true)
                {
                    group.add(new Facet(uriFactory.createFacetURI(baseURI, pair, getName(st.getId()),
                            FacetType.COLLECTION), getName(st.getId()), count, FacetType.COLLECTION, st.getId()));
                }
                if (count < sizeAllImages)
                {
                    pair.setNot(true);
                    group.add(new Facet(uriFactory.createFacetURI(baseURI, pair, "No " + getName(st.getId()),
                            FacetType.COLLECTION), "No " + getName(st.getId()), sizeAllImages - count,
                            FacetType.COLLECTION, st.getId()));
                }
            }
            facets.add(group);
        }
    }

    public String getName(URI uri)
    {
        MetadataLabels metadataLabels = (MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class);
        String name = metadataLabels.getLabels().get(uri);
        return name;
    }

    /**
     * Count {@link Item} for one facet
     * 
     * @param searchQuery
     * @param pair
     * @param collectionImages
     * @return
     */
    public int getCount(SearchQuery searchQuery, SearchPair pair, List<String> collectionImages)
    {
        ItemController ic = new ItemController(sb.getUser());
        // SearchQuery sq = new SearchQuery(searchQuery.getElements());
        SearchQuery sq = new SearchQuery();
        if (pair != null)
        {
            sq.addLogicalRelation(LOGICAL_RELATIONS.AND);
            sq.addPair(pair);
        }
        return ic.search(colURI, sq, null, collectionImages).getNumberOfRecords();
    }

    public SearchResult retrieveAllImages(SearchQuery searchQuery)
    {
        return ((CollectionImagesBean)BeanHelper.getSessionBean(CollectionImagesBean.class)).getSearchResult();
        // ItemController ic = new ItemController(sb.getUser());
        // return ic.search(colURI, searchQuery, new SortCriterion(), null);
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
