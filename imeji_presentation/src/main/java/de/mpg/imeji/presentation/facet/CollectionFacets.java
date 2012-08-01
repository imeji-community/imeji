/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.facet;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.richfaces.model.SortOrder;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.facet.Facet.FacetType;
import de.mpg.imeji.presentation.filter.FiltersSession;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;

public class CollectionFacets
{
    private SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    private FiltersSession fs = (FiltersSession)BeanHelper.getSessionBean(FiltersSession.class);
    private List<List<Facet>> facets = new ArrayList<List<Facet>>();
    private URI colURI = null;

    public CollectionFacets(CollectionImeji col, SearchQuery searchQuery) throws Exception
    {
        this.colURI = col.getId();
        MetadataProfile profile = ObjectCachedLoader.loadProfile(col.getProfile());
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        String baseURI = nav.getImagesUrl() + col.getId().getPath() + "?q=";
        ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class)).init(profile);
        FacetURIFactory uriFactory = new FacetURIFactory(searchQuery);
        int count = 0;
        SearchResult allImages = retrieveAllImages(searchQuery);
        int sizeAllImages = allImages.getNumberOfRecords();
        for (Statement st : profile.getStatements())
        {
            List<Facet> group = new ArrayList<Facet>();
            if (!fs.isFilter(getName(st.getId())))
            {
                SearchPair pair = new SearchPair(Search.getIndex(SearchIndex.names.IMAGE_METADATA_STATEMENT),
                        SearchOperators.URI, st.getId().toString());
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
        return ic.countImagesInContainer(colURI, sq, collectionImages);
    }

    public SearchResult retrieveAllImages(SearchQuery searchQuery)
    {
        ItemController ic = new ItemController(sb.getUser());
        return ic.searchImagesInContainer(colURI, searchQuery, new SortCriterion(), 0, 0);
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
