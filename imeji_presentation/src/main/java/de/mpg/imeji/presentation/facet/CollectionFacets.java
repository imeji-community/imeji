/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.facet;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.vo.SearchIndexes;
import de.mpg.imeji.logic.search.vo.SearchCriterion;
import de.mpg.imeji.logic.search.vo.SearchCriterion.Filtertype;
import de.mpg.imeji.logic.search.vo.SearchCriterion.Operator;
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

    public CollectionFacets(CollectionImeji col, List<SearchCriterion> scList) throws Exception
    {
        this.colURI = col.getId();
        MetadataProfile profile = ObjectCachedLoader.loadProfile(col.getProfile());
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        String baseURI = nav.getImagesUrl() + col.getId().getPath() + "?q=";
        ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class)).init(profile);
        FacetURIFactory uriFactory = new FacetURIFactory(scList);
        int count = 0;
        int sizeAllImages = getCount(scList, null);
        for (Statement st : profile.getStatements())
        {
            List<Facet> group = new ArrayList<Facet>();
            if (!fs.isFilter(getName(st.getId())) || !fs.isFilter("No " + getName(st.getId())))
            {
                SearchCriterion sc = new SearchCriterion(Operator.AND, SearchIndexes.IMAGE_METADATA_STATEMENT, st
                        .getId().toString(), Filtertype.URI);
                count = getCount(new ArrayList<SearchCriterion>(scList), sc);
                if (count > 0 || true)
                {
                    group.add(new Facet(uriFactory.createFacetURI(baseURI, sc, getName(st.getId()),
                            FacetType.COLLECTION), getName(st.getId()), count, FacetType.COLLECTION, st.getId()));
                }
                if (count < sizeAllImages || true)
                {
                    sc = new SearchCriterion(Operator.NOTAND, SearchIndexes.IMAGE_METADATA_STATEMENT, st.getId()
                            .toString(), Filtertype.URI);
                    group.add(new Facet(uriFactory.createFacetURI(baseURI, sc, "No " + getName(st.getId()),
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

    public int getCount(List<SearchCriterion> scList, SearchCriterion sc)
    {
        ItemController ic = new ItemController(sb.getUser());
        if (sc != null)
            scList.add(sc);
        return ic.countImagesInContainer(colURI, scList);
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
