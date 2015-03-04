/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.search.vo.*;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
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
import de.mpg.imeji.presentation.util.UrlHelper;
import org.apache.log4j.Logger;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Java bean for the advanced search page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AdvancedSearchBean
{
    private SearchForm formular = null;
    // Menus
    private List<SelectItem> profilesMenu;
    private List<SelectItem> operatorsMenu;
    private List<SelectItem> fileTypesMenu;
    private List<String> fileTypesSelected;
    /**
     * True if the query got an error (for instance wrong date format). Then the message is written in red
     */
    private boolean errorQuery = false;
    private SessionBean session;
    private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);

    /**
     * Constructor for the {@link AdvancedSearchBean}
     */
    public AdvancedSearchBean()
    {
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    /**
     * Called when the page is called per get request. Read the query in the url and initialize the form with it
     * 
     * @return
     */
    public String getNewSearch()
    {
        initMenus();
        try
        {
            String query = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("q");
            if (!UrlHelper.getParameterBoolean("error"))
            {
                errorQuery = false;
                initForm(URLQueryTransformer.parseStringQuery(query));
            }
        }
        catch (Exception e)
        {
            logger.error("Error initializing advanced search", e);
            BeanHelper.error("Error initializing advanced search");
        }
        return "";
    }

    /**
     * Init the menus of the page
     */
    private void initMenus()
    {
        operatorsMenu = new ArrayList<SelectItem>();
        operatorsMenu.add(new SelectItem(LOGICAL_RELATIONS.AND, session.getLabel("and_small")));
        operatorsMenu.add(new SelectItem(LOGICAL_RELATIONS.OR, session.getLabel("or_small")));
        ConfigurationBean config = (ConfigurationBean)BeanHelper.getApplicationBean(ConfigurationBean.class);
        fileTypesMenu = new ArrayList<>();
        fileTypesSelected = new ArrayList<>();
        for (Type type : config.getFileTypes().getTypes())
        {
            fileTypesMenu.add(new SelectItem(type.getName(session.getLocale().getLanguage())));
        }
    }

    /**
     * Initialized the search form with the {@link SearchQuery}
     * 
     * @param searchQuery
     * @throws Exception
     */
    public void initForm(SearchQuery searchQuery) throws Exception
    {
        Map<String, MetadataProfile> profs = loadProfilesAndInitMenu(loadCollections());
        ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class)).init1((new ArrayList<MetadataProfile>(profs
                .values())));
        formular = new SearchForm(searchQuery, profs);
        parseFileTypesQuery(formular.getFileTypesQuery());
        if (formular.getGroups().size() == 0)
        {
            formular.addSearchGroup(0);
        }
    }

    /**
     * Reset the Search form with empty values
     * 
     * @throws Exception
     */
    public String reset() throws Exception
    {
        initForm(new SearchQuery());
        return "";
    }

    /**
     * Load all {@link CollectionImeji} which are searchable for the current {@link User}
     * 
     * @return
     * @throws Exception 
     */
    private List<CollectionImeji> loadCollections() throws Exception
    {
        CollectionController cc = new CollectionController();
        List<CollectionImeji> l = new ArrayList<>();
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setIndex(SPARQLSearch.getIndex(SearchIndex.names.cont_title.name()));
        sortCriterion.setSortOrder(SortOrder.valueOf(SortOrder.DESCENDING.name()));
        for (String uri : cc.search(new SearchQuery(), sortCriterion, -1, 0,session.getUser()).getResults())
        {
            CollectionImeji c = ObjectLoader.loadCollectionLazy(URI.create(uri), session.getUser());
            l.add(c);
        }
        return l;
    }

    /**
     * Load the {@link MetadataProfile} of the {@link Collection} and init the collectionmenu
     * 
     * @param collections
     * @return
     */
    private Map<String, MetadataProfile> loadProfilesAndInitMenu(Collection<CollectionImeji> collections)
    {
        profilesMenu = new ArrayList<SelectItem>();
        profilesMenu.add(new SelectItem(null, session.getLabel("select_collection")));
        Map<String, MetadataProfile> map = new HashMap<String, MetadataProfile>();
        for (CollectionImeji c : collections)
        {
            MetadataProfile p = ObjectLoader.loadProfile(c.getProfile(), session.getUser());
            if (p != null && p.getStatements() != null && p.getStatements().size() > 0 && !isEmpty(c))
            {
                map.put(p.getId().toString(), p);
                profilesMenu.add(new SelectItem(p.getId().toString(), c.getMetadata().getTitle()));
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
    private boolean isEmpty(CollectionImeji c)
    {
        return ImejiSPARQL.exec(SPARQLQueries.selectCollectionItems(c.getId(), session.getUser(), 1), null).size() == 0;
    }

    /**
     * Method called when form is submitted
     * 
     * @return
     * @throws IOException
     */
    public String search() throws IOException
    {
        FiltersSession filtersSession = (FiltersSession)BeanHelper.getSessionBean(FiltersSession.class);
        filtersSession.getFilters().clear();
        goToResultPage();
        return "";
    }

    /**
     * Redirect to the search result page
     * 
     * @throws IOException
     */
    public void goToResultPage() throws IOException
    {
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        try
        {
            errorQuery = false;
            SearchQuery query = formular.getFormularAsSearchQuery();
            query.addLogicalRelation(LOGICAL_RELATIONS.AND);
            query.addPair(new SearchPair(SPARQLSearch.getIndex(SearchIndex.names.filetype), SearchOperators.REGEX,
                    getFileTypesQuery()));
            String q = URLQueryTransformer.transform2UTF8URL(query);
            if (!"".equals(q))
            {
                FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getBrowseUrl() + "?q=" + q);
            }
            else
                BeanHelper.error(session.getMessage("error_search_query_emtpy"));
        }
        catch (Exception e)
        {
            errorQuery = true;
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(navigation.getSearchUrl() + "?error=1&types=" + getFileTypesQuery());
        }
    }

    /**
     * REturn the file types Selected as a query
     * 
     * @return
     */
    private String getFileTypesQuery()
    {
        String qf = "";
        for (String type : fileTypesSelected)
        {
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
    private void parseFileTypesQuery(String query)
    {
        fileTypesSelected = new ArrayList<String>();
        for (String t : query.split(Pattern.quote("|")))
            fileTypesSelected.add(t);
    }

    /**
     * Change the {@link SearchGroup}
     */
    public void changeGroup()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        formular.changeSearchGroup(gPos);
    }

    /**
     * Add a new {@link SearchGroupForm}
     */
    public void addGroup()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        formular.addSearchGroup(gPos);
    }

    /**
     * Remove a {@link SearchGroupForm}
     */
    public void removeGroup()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        formular.removeSearchGroup(gPos);
        if (formular.getGroups().size() == 0)
        {
            formular.addSearchGroup(0);
        }
    }

    /**
     * Change a {@link SearchMetadataForm}. The search value is removed
     */
    public void changeElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.changeElement(gPos, elPos, false);
    }

    /**
     * Update a {@link SearchMetadataForm}. The search value is keeped
     */
    public void updateElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.changeElement(gPos, elPos, true);
    }

    /**
     * Add a new {@link SearchMetadataForm}
     */
    public void addElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.addElement(gPos, elPos);
    }

    /**
     * Remove a new {@link SearchMetadataForm}
     */
    public void removeElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.removeElement(gPos, elPos);
        if (formular.getGroups().get(gPos).getSearchElementForms().size() == 0)
        {
            formular.removeSearchGroup(gPos);
            formular.addSearchGroup(gPos);
        }
    }

    /**
     * Return the current {@link SearchQuery} in the form as a user friendly query
     * 
     * @return
     */
    public String getSimpleQuery()
    {
        try
        {
            errorQuery = false;
            return URLQueryTransformer.searchQuery2PrettyQuery(formular.getFormularAsSearchQuery());
        }
        catch (Exception e)
        {
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
    public List<SelectItem> getProfilesMenu()
    {
        return profilesMenu;
    }

    /**
     * Setter
     * 
     * @param collectionsMenu
     */
    public void setProfilesMenu(List<SelectItem> profilesMenu)
    {
        this.profilesMenu = profilesMenu;
    }

    /**
     * Getter
     * 
     * @return
     */
    public SearchForm getFormular()
    {
        return formular;
    }

    /**
     * stter
     * 
     * @param formular
     */
    public void setFormular(SearchForm formular)
    {
        this.formular = formular;
    }

    /**
     * Getter
     * 
     * @return
     */
    public List<SelectItem> getOperatorsMenu()
    {
        return operatorsMenu;
    }

    /**
     * Setter
     * 
     * @param operatorsMenu
     */
    public void setOperatorsMenu(List<SelectItem> operatorsMenu)
    {
        this.operatorsMenu = operatorsMenu;
    }

    /**
     * @return the errorQuery
     */
    public boolean getErrorQuery()
    {
        return errorQuery;
    }

    /**
     * @param errorQuery the errorQuery to set
     */
    public void setErrorQuery(boolean errorQuery)
    {
        this.errorQuery = errorQuery;
    }

    /**
     * @return the fileTypesMenu
     */
    public List<SelectItem> getFileTypesMenu()
    {
        return fileTypesMenu;
    }

    /**
     * @param fileTypesMenu the fileTypesMenu to set
     */
    public void setFileTypesMenu(List<SelectItem> fileTypesMenu)
    {
        this.fileTypesMenu = fileTypesMenu;
    }

    /**
     * @return the fileTypesSelected
     */
    public List<String> getFileTypesSelected()
    {
        return fileTypesSelected;
    }

    /**
     * @param fileTypesSelected the fileTypesSelected to set
     */
    public void setFileTypesSelected(List<String> fileTypesSelected)
    {
        this.fileTypesSelected = fileTypesSelected;
    }
}
