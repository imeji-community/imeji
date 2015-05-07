/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.vo.SearchElement;
import de.mpg.imeji.logic.search.vo.SearchElement.SEARCH_ELEMENTS;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * A {@link SearchGroupForm} is a group of {@link SearchMetadataForm}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchGroupForm {
	private List<SearchMetadataForm> elements;
	private String profileId;
	private String collectionId;
	private List<SelectItem> collectionsMenu;
	private List<SelectItem> statementMenu;

	/**
	 * Default Constructor
	 */
	public SearchGroupForm() {
		reset();
	}

	private void reset() {
		profileId = null;
		collectionId = null;
		elements = new ArrayList<SearchMetadataForm>();
		statementMenu = new ArrayList<SelectItem>();
		collectionsMenu = new ArrayList<SelectItem>();
	}

	/**
	 * Constructor for a {@link SearchGroup} and {@link MetadataProfile}
	 * 
	 * @param searchGroup
	 * @param profile
	 * @param collectionId
	 * @throws ImejiException
	 */
	public SearchGroupForm(SearchGroup searchGroup, MetadataProfile profile)
			throws ImejiException {
		this();
		if (profile != null) {
			this.setProfileId(profile.getId().toString());
			this.collectionId = SearchFormularHelper
					.getCollectionId(searchGroup);
			SearchGroup metadataGroup;
			if (this.collectionId != null) {
				// case where: query = (collection AND (metadata))
				metadataGroup = (SearchGroup) searchGroup.getElements().get(2);
			} else {
				// case where: query = (metadata)
				metadataGroup = searchGroup;
			}
			for (SearchElement se : metadataGroup.getElements()) {
				if (se.getType().equals(SEARCH_ELEMENTS.GROUP)) {
					// metadata search
					elements.add(new SearchMetadataForm((SearchGroup) se,
							profile));
				} else if (elements.size() > 0
						&& se.getType().equals(
								SEARCH_ELEMENTS.LOGICAL_RELATIONS)) {
					elements.get(elements.size() - 1).setLogicalRelation(
							((SearchLogicalRelation) se).getLogicalRelation());
				}
			}
			initStatementsMenu(profile);
		}
	}

	/**
	 * Return the {@link SearchGroupForm} as a {@link SearchGroup}
	 * 
	 * @return
	 */
	public SearchGroup getAsSearchGroup() {

		SearchGroup groupWithAllMetadata = new SearchGroup();
		for (SearchMetadataForm e : elements) {
			groupWithAllMetadata.addGroup(e.getAsSearchGroup());
			groupWithAllMetadata.addLogicalRelation(e.getLogicalRelation());
		}
		if (collectionId != null && !"".equals(collectionId)) {
			SearchGroup searchGroup = new SearchGroup();
			searchGroup.addPair(new SearchPair(SPARQLSearch
					.getIndex(SearchIndex.IndexNames.col),
					SearchOperators.EQUALS, collectionId.toString()));
			searchGroup.addLogicalRelation(LOGICAL_RELATIONS.AND);
			searchGroup.addGroup(groupWithAllMetadata);
			return searchGroup;
		}
		return groupWithAllMetadata;
	}

	/**
	 * Initialize the {@link Statement} for the select menu in the form
	 * 
	 * @param p
	 * @throws ImejiException
	 */
	public void initStatementsMenu(MetadataProfile p) throws ImejiException {
		if (p != null) {
			if (p.getStatements() != null) {
				for (Statement st : p.getStatements()) {
					String stName = ((MetadataLabels) BeanHelper
							.getSessionBean(MetadataLabels.class))
							.getInternationalizedLabels().get(st.getId());
					statementMenu.add(new SelectItem(st.getId().toString(),
							stName));
				}
			}
			setCollectionsMenu(getCollectionsMenu(p));
		} else {
			reset();
		}

	}

	/**
	 * Load all the {@link CollectionImeji} using a {@link MetadataProfile} and
	 * return it as menu for the searchgroup
	 * 
	 * @param p
	 * @return
	 * @throws ImejiException
	 */
	private List<SelectItem> getCollectionsMenu(MetadataProfile p)
			throws ImejiException {
		CollectionController cc = new CollectionController();
		SearchQuery q = new SearchQuery();
		q.addPair(new SearchPair(SPARQLSearch
				.getIndex(SearchIndex.IndexNames.prof), SearchOperators.EQUALS,
				p.getId().toString()));
		List<SelectItem> l = new ArrayList<SelectItem>();
		SessionBean session = (SessionBean) BeanHelper
				.getSessionBean(SessionBean.class);
		l.add(new SelectItem(null, session
				.getLabel("adv_search_collection_restrict")));
		for (String uri : cc.search(q, null, -1, 0, session.getUser(),
				session.getSelectedSpaceString()).getResults()) {
			CollectionImeji c = ObjectLoader.loadCollectionLazy(
					URI.create(uri), session.getUser());
			l.add(new SelectItem(c.getId().toString(), c.getMetadata()
					.getTitle()));
		}
		return l;
	}

	public int getSize() {
		return elements.size();
	}

	public List<SearchMetadataForm> getSearchElementForms() {
		return elements;
	}

	public void setSearchElementForms(List<SearchMetadataForm> elements) {
		this.elements = elements;
	}

	public List<SelectItem> getStatementMenu() {
		return statementMenu;
	}

	public void setStatementMenu(List<SelectItem> statementMenu) {
		this.statementMenu = statementMenu;
	}

	/**
	 * @return the profileId
	 */
	public String getProfileId() {
		return profileId;
	}

	/**
	 * @param profileId
	 *            the profileId to set
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getCollection() {
		return collectionId;
	}

	public void setCollection(String collection) {
		this.collectionId = collection;
	}

	public List<SelectItem> getCollectionsMenu() {
		return collectionsMenu;
	}

	public void setCollectionsMenu(List<SelectItem> collectionsMenu) {
		this.collectionsMenu = collectionsMenu;
	}

}
