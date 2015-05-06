/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.search.query.URLQueryTransformer;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.ConfigurationBean;
import de.mpg.imeji.presentation.beans.FileTypes.Type;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.filter.FiltersSession;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * Java bean for the advanced search page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AdvancedSearchBean {
	private SearchForm formular = null;
	// Menus
	private List<SelectItem> profilesMenu;
	private List<SelectItem> collectionsMenu;
	private List<SelectItem> operatorsMenu;
	private List<SelectItem> fileTypesMenu;
	private List<String> fileTypesSelected;
	/**
	 * True if the query got an error (for instance wrong date format). Then the
	 * message is written in red
	 */
	private boolean errorQuery = false;
	private SessionBean session;
	private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);

	/**
	 * Constructor for the {@link AdvancedSearchBean}
	 */
	public AdvancedSearchBean() {
		session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	}

	/**
	 * Called when the page is called per get request. Read the query in the url
	 * and initialize the form with it
	 * 
	 * @return
	 */
	public String getNewSearch() {
		initMenus();
		try {
			String query = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap().get("q");
			if (!UrlHelper.getParameterBoolean("error")) {
				errorQuery = false;
				initForm(URLQueryTransformer.parseStringQuery(query));
			}
		} catch (Exception e) {
			logger.error("Error initializing advanced search", e);
			BeanHelper.error("Error initializing advanced search");
		}
		return "";
	}

	/**
	 * Init the menus of the page
	 */
	private void initMenus() {
		operatorsMenu = new ArrayList<SelectItem>();
		operatorsMenu.add(new SelectItem(LOGICAL_RELATIONS.AND, session
				.getLabel("and_small")));
		operatorsMenu.add(new SelectItem(LOGICAL_RELATIONS.OR, session
				.getLabel("or_small")));
		ConfigurationBean config = (ConfigurationBean) BeanHelper
				.getApplicationBean(ConfigurationBean.class);
		fileTypesMenu = new ArrayList<>();
		fileTypesSelected = new ArrayList<>();
		for (Type type : config.getFileTypes().getTypes()) {
			fileTypesMenu.add(new SelectItem(type.getName(session.getLocale()
					.getLanguage())));
		}
	}

	/**
	 * Initialized the search form with the {@link SearchQuery}
	 * 
	 * @param searchQuery
	 * @throws Exception
	 */
	public void initForm(SearchQuery searchQuery) throws Exception {
		Map<String, MetadataProfile> profs = loadProfilesAndInitMenu();
		((MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class))
				.init1((new ArrayList<MetadataProfile>(profs.values())));
		formular = new SearchForm(searchQuery, profs);
		parseFileTypesQuery(formular.getFileTypesQuery());
		if (formular.getGroups().size() == 0) {
			formular.addSearchGroup(0);
		}
	}

	/**
	 * Reset the Search form with empty values
	 * 
	 * @throws Exception
	 */
	public String reset() throws Exception {
		initForm(new SearchQuery());
		return "";
	}

	/**
	 * Load all available profiles
	 * 
	 * @return
	 * @throws ImejiException
	 */
	private Map<String, MetadataProfile> loadProfilesAndInitMenu()
			throws ImejiException {
		profilesMenu = new ArrayList<SelectItem>();
		profilesMenu.add(new SelectItem(null, session
				.getLabel("select_profile")));
		ProfileController controller = new ProfileController();

		Map<String, MetadataProfile> map = new HashMap<String, MetadataProfile>();
		for (MetadataProfile p : controller.search(session.getUser())) {
			if (p != null && p.getStatements() != null
					&& p.getStatements().size() > 0) {
				map.put(p.getId().toString(), p);
				profilesMenu.add(new SelectItem(p.getId().toString(), p
						.getTitle()));
			}
		}
		return map;
	}

	/**
	 * True if the {@link CollectionImeji} is empty
	 * 
	 * @param c
	 * @return
	 */
	private boolean isEmpty(CollectionImeji c) {
		return ImejiSPARQL.exec(
				SPARQLQueries.selectCollectionItems(c.getId(),
						session.getUser(), 1), null).size() == 0;
	}

	/**
	 * Method called when form is submitted
	 * 
	 * @return
	 * @throws IOException
	 */
	public String search() throws IOException {
		FiltersSession filtersSession = (FiltersSession) BeanHelper
				.getSessionBean(FiltersSession.class);
		filtersSession.getFilters().clear();
		goToResultPage();
		return "";
	}

	/**
	 * Redirect to the search result page
	 * 
	 * @throws IOException
	 */
	public void goToResultPage() throws IOException {
		Navigation navigation = (Navigation) BeanHelper
				.getApplicationBean(Navigation.class);
		try {
			errorQuery = false;
			SearchQuery query = formular.getFormularAsSearchQuery();
			query.addLogicalRelation(LOGICAL_RELATIONS.AND);
			query.addPair(new SearchPair(SPARQLSearch
					.getIndex(SearchIndex.IndexNames.filetype),
					SearchOperators.REGEX, getFileTypesQuery()));
			String q = URLQueryTransformer.transform2UTF8URL(query);
			if (!"".equals(q)) {
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect(navigation.getBrowseUrl() + "?q=" + q);
			} else
				BeanHelper
						.error(session.getMessage("error_search_query_emtpy"));
		} catch (Exception e) {
			errorQuery = true;
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							navigation.getSearchUrl() + "?error=1&types="
									+ getFileTypesQuery());
		}
	}

	/**
	 * REturn the file types Selected as a query
	 * 
	 * @return
	 */
	private String getFileTypesQuery() {
		String qf = "";
		for (String type : fileTypesSelected) {
			if (!qf.equals(""))
				qf += "|";
			qf += type;
		}
		return qf;
	}

	/**
	 * Parse the selected file types in the seach query
	 * 
	 * @param query
	 */
	private void parseFileTypesQuery(String query) {
		fileTypesSelected = new ArrayList<String>();
		for (String t : query.split(Pattern.quote("|")))
			fileTypesSelected.add(t);
	}

	/**
	 * Change the {@link SearchGroup}
	 * 
	 * @throws ImejiException
	 */
	public void changeGroup() throws ImejiException {
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("gPos"));
		formular.changeSearchGroup(gPos);
	}

	/**
	 * Add a new {@link SearchGroupForm}
	 */
	public void addGroup() {
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("gPos"));
		formular.addSearchGroup(gPos);
	}

	/**
	 * Remove a {@link SearchGroupForm}
	 */
	public void removeGroup() {
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("gPos"));
		formular.removeSearchGroup(gPos);
		if (formular.getGroups().size() == 0) {
			formular.addSearchGroup(0);
		}
	}

	/**
	 * Change a {@link SearchMetadataForm}. The search value is removed
	 */
	public void changeElement() {
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("gPos"));
		int elPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("elPos"));
		formular.changeElement(gPos, elPos, false);
	}

	/**
	 * Update a {@link SearchMetadataForm}. The search value is keeped
	 */
	public void updateElement() {
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("gPos"));
		int elPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("elPos"));
		formular.changeElement(gPos, elPos, true);
	}

	/**
	 * Add a new {@link SearchMetadataForm}
	 */
	public void addElement() {
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("gPos"));
		int elPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("elPos"));
		formular.addElement(gPos, elPos);
	}

	/**
	 * Remove a new {@link SearchMetadataForm}
	 */
	public void removeElement() {
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("gPos"));
		int elPos = Integer.parseInt(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("elPos"));
		formular.removeElement(gPos, elPos);
		if (formular.getGroups().get(gPos).getSearchElementForms().size() == 0) {
			formular.removeSearchGroup(gPos);
			formular.addSearchGroup(gPos);
		}
	}

	/**
	 * Return the current {@link SearchQuery} in the form as a user friendly
	 * query
	 * 
	 * @return
	 */
	public String getSimpleQuery() {
		try {
			errorQuery = false;
			return URLQueryTransformer.searchQuery2PrettyQuery(formular
					.getFormularAsSearchQuery());
		} catch (Exception e) {
			errorQuery = true;
			if ("Wrong date format".equals(e.getMessage()))
				return session.getMessage("error_date_format");
			return e.getMessage();
		}
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public List<SelectItem> getProfilesMenu() {
		return profilesMenu;
	}

	/**
	 * Setter
	 * 
	 * @param collectionsMenu
	 */
	public void setProfilesMenu(List<SelectItem> profilesMenu) {
		this.profilesMenu = profilesMenu;
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public SearchForm getFormular() {
		return formular;
	}

	/**
	 * stter
	 * 
	 * @param formular
	 */
	public void setFormular(SearchForm formular) {
		this.formular = formular;
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public List<SelectItem> getOperatorsMenu() {
		return operatorsMenu;
	}

	/**
	 * Setter
	 * 
	 * @param operatorsMenu
	 */
	public void setOperatorsMenu(List<SelectItem> operatorsMenu) {
		this.operatorsMenu = operatorsMenu;
	}

	/**
	 * @return the errorQuery
	 */
	public boolean getErrorQuery() {
		return errorQuery;
	}

	/**
	 * @param errorQuery
	 *            the errorQuery to set
	 */
	public void setErrorQuery(boolean errorQuery) {
		this.errorQuery = errorQuery;
	}

	/**
	 * @return the fileTypesMenu
	 */
	public List<SelectItem> getFileTypesMenu() {
		return fileTypesMenu;
	}

	/**
	 * @param fileTypesMenu
	 *            the fileTypesMenu to set
	 */
	public void setFileTypesMenu(List<SelectItem> fileTypesMenu) {
		this.fileTypesMenu = fileTypesMenu;
	}

	/**
	 * @return the fileTypesSelected
	 */
	public List<String> getFileTypesSelected() {
		return fileTypesSelected;
	}

	/**
	 * @param fileTypesSelected
	 *            the fileTypesSelected to set
	 */
	public void setFileTypesSelected(List<String> fileTypesSelected) {
		this.fileTypesSelected = fileTypesSelected;
	}

	public List<SelectItem> getCollectionsMenu() {
		return collectionsMenu;
	}

	public void setCollectionsMenu(List<SelectItem> collectionsMenu) {
		this.collectionsMenu = collectionsMenu;
	}
}
