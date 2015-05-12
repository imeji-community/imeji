/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.facet;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.collection.CollectionItemsBean;
import de.mpg.imeji.presentation.facet.Facet.FacetType;
import de.mpg.imeji.presentation.filter.FiltersSession;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;

/**
 * Facets for the item browsed within a collection
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CollectionFacets extends Facets {
	private SessionBean sb = (SessionBean) BeanHelper
			.getSessionBean(SessionBean.class);
	private FiltersSession fs = (FiltersSession) BeanHelper
			.getSessionBean(FiltersSession.class);
	private List<List<Facet>> facets = new ArrayList<List<Facet>>();
	private URI colURI = null;
	private SearchQuery searchQuery;
	private Navigation nav = (Navigation) BeanHelper
			.getApplicationBean(Navigation.class);
	private SearchResult allImages = ((CollectionItemsBean) BeanHelper
			.getSessionBean(CollectionItemsBean.class)).getSearchResult();
	private MetadataProfile profile;

	/**
	 * Constructor for the {@link Facet}s of one {@link CollectionImeji} with
	 * one {@link SearchQuery}
	 * 
	 * @param col
	 * @param searchQuery
	 */
	public CollectionFacets(CollectionImeji col, SearchQuery searchQuery) {
		this.colURI = col.getId();
		this.searchQuery = searchQuery;
		this.profile = ObjectCachedLoader.loadProfile(col.getProfile());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.imeji.presentation.facet.Facets#init()
	 */
	public void init() {
		// Use relativ url instead of aboslut, due to issues with space url
		// String baseURI = nav.getCollectionUrl() + ObjectHelper.getId(colURI)
		// + "/" + nav.getBrowsePath() + "?q=";
		String baseURI = "?q=";
		FacetURIFactory uriFactory = new FacetURIFactory(searchQuery);

		int count = 0;
		int sizeAllImages = allImages.getNumberOfRecords();
		try {
			for (Statement st : profile.getStatements()) {
				List<Facet> group = new ArrayList<Facet>();
				if (st.isPreview() && !fs.isFilter(getName(st.getId()))) {
					SearchPair pair = new SearchPair(
							SPARQLSearch
									.getIndex(SearchIndex.IndexNames.statement),
							SearchOperators.EQUALS, st.getId().toString());
					count = getCount(searchQuery, pair, allImages.getResults());

					group.add(new Facet(uriFactory.createFacetURI(baseURI,
							pair, getName(st.getId()), FacetType.COLLECTION),
							getName(st.getId()), count, FacetType.COLLECTION,
							st.getId()));

					if (count <= sizeAllImages) {
						pair.setNot(true);
						group.add(new Facet(uriFactory.createFacetURI(baseURI,
								pair, "No " + getName(st.getId()),
								FacetType.COLLECTION), "No "
								+ getName(st.getId()), sizeAllImages - count,
								FacetType.COLLECTION, st.getId()));
					}
				}
				facets.add(group);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get
	 * 
	 * @param uri
	 * @return
	 */
	public String getName(URI uri) {
		return ObjectHelper.getId(uri);
	}

	/**
	 * Count {@link Item} for one facet
	 * 
	 * @param searchQuery
	 * @param pair
	 * @param collectionImages
	 * @return
	 */
	public int getCount(SearchQuery searchQuery, SearchPair pair,
			List<String> collectionImages) {
		ItemController ic = new ItemController();
		SearchQuery sq = new SearchQuery();
		if (pair != null) {
			sq.addLogicalRelation(LOGICAL_RELATIONS.AND);
			sq.addPair(pair);
		}
		return ic
				.search(colURI, sq, null, collectionImages, sb.getUser(), null)
				.getNumberOfRecords();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.imeji.presentation.facet.Facets#getFacets()
	 */
	public List<List<Facet>> getFacets() {
		return facets;
	}
}
