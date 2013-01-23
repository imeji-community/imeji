/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.facet.Facet.FacetType;
import de.mpg.imeji.presentation.facet.FacetsBean;
import de.mpg.imeji.presentation.filter.Filter;
import de.mpg.imeji.presentation.filter.FiltersBean;
import de.mpg.imeji.presentation.filter.FiltersSession;
import de.mpg.imeji.presentation.history.HistorySession;
import de.mpg.imeji.presentation.search.URLQueryTransformer;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.session.SessionObjectsController;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * The bean for all list of images
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImagesBean extends BasePaginatorListSessionBean<ThumbnailBean>
{
    private int totalNumberOfRecords;
    private SessionBean session;
    private List<SelectItem> sortMenu;
    private String selectedSortCriterion;
    private String selectedSortOrder;
    private FacetsBean facets;
    protected FiltersBean filters;
    private String query;
    private Navigation navigation;
    private Filter searchFilter;
    private boolean isSimpleSearch;
    private SearchQuery searchQuery = new SearchQuery();
    private String discardComment;
    private String selectedImagesContext;

    /**
     * The bean for all list of images
     */
    public ImagesBean()
    {
        super();
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        filters = new FiltersBean();
        initMenus();
        selectedSortCriterion = null;
        try
        {
            setElementsPerPage(Integer.parseInt(PropertyReader.getProperty("imeji.image.list.size")));
        }
        catch (Exception e)
        {
            logger.error("Error loading property imeji.image.list.size", e);
        }
        try
        {
            String options = PropertyReader.getProperty("imeji.image.list.size.options");
            for (String option : options.split(","))
            {
                getElementsPerPageSelectItems().add(new SelectItem(option));
            }
        }
        catch (Exception e)
        {
            logger.error("Error reading property imeji.image.list.size.options", e);
        }
    }

    /**
     * Init the page when it is called
     * 
     * @return
     */
    public String getInitPage()
    {
        getNavigationString();
        initMenus();
        if (facets != null)
        {
            facets.getFacets().clear();
        }
        initBackPage();
        try
        {
            query = UrlHelper.getParameterValue("q");
            searchQuery = URLQueryTransformer.parseStringQuery(query);
        }
        catch (Exception e)
        {
            BeanHelper.error("Error parsing query");
            logger.error("Error parsing query", e);
        }
        searchFilter = null;
        for (Filter f : filters.getSession().getFilters())
        {
            if (FacetType.SEARCH.equals(f.getType()))
            {
                searchFilter = f;
            }
        }
        isSimpleSearch = URLQueryTransformer.isSimpleSearch(searchQuery);
        return "";
    }

    /**
     * Init all menus of the page
     */
    private void initMenus()
    {
        sortMenu = new ArrayList<SelectItem>();
        sortMenu.add(new SelectItem(null, session.getLabel("default")));
        sortMenu.add(new SelectItem(SearchIndex.names.PROPERTIES_CREATION_DATE, session
                .getLabel(SearchIndex.names.PROPERTIES_CREATION_DATE.name())));
        sortMenu.add(new SelectItem(SearchIndex.names.IMAGE_COLLECTION, session
                .getLabel(SearchIndex.names.IMAGE_COLLECTION.name())));
        sortMenu.add(new SelectItem(SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE, session
                .getLabel(SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE.name())));
        selectedSortOrder = SortOrder.DESCENDING.name();
    }

    @Override
    public String getNavigationString()
    {
        if (session.getSelectedImagesContext() != null && !(session.getSelectedImagesContext().equals("pretty:browse")))
        {
            session.getSelected().clear();
        }
        session.setSelectedImagesContext("pretty:browse");
        return "pretty:browse";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return totalNumberOfRecords;
    }

    /**
     * Initialize the {@link SortCriterion} according to the selected value in the sort menu.
     * 
     * @return
     */
    public SortCriterion initSortCriterion()
    {
        SortCriterion sortCriterion = new SortCriterion();
        if (getSelectedSortCriterion() != null && !getSelectedSortCriterion().trim().equals(""))
        {
            sortCriterion.setIndex(Search.getIndex(getSelectedSortCriterion()));
            sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        }
        else
        {
            sortCriterion.setIndex(null);
        }
        return sortCriterion;
    }

    @Override
    public List<ThumbnailBean> retrieveList(int offset, int limit)
    {
        SortCriterion sortCriterion = initSortCriterion();
        SearchResult searchResult = search(searchQuery, sortCriterion);
        searchResult.setQuery(query);
        searchResult.setSort(sortCriterion);
        totalNumberOfRecords = searchResult.getNumberOfRecords();
        // load images
        Collection<Item> items = loadImages(searchResult.getResults());
        return ImejiFactory.imageListToThumbList(items);
    }

    /**
     * Perform the {@link Search}
     * 
     * @param searchQuery
     * @param sortCriterion
     * @return
     */
    public SearchResult search(SearchQuery searchQuery, SortCriterion sortCriterion)
    {
        ItemController controller = new ItemController(session.getUser());
        return controller.searchImages(searchQuery, sortCriterion);
    }

    /**
     * Laod all items (defined by their uri)
     * 
     * @param uris
     * @return
     */
    public Collection<Item> loadImages(List<String> uris)
    {
        ItemController controller = new ItemController(session.getUser());
        return controller.loadItems(uris, getElementsPerPage(), getOffset());
    }

    /**
     * return the current {@link SearchQuery} in a user friendly style.
     * 
     * @return
     */
    public String getSimpleQuery()
    {
        if (searchFilter != null && searchFilter.getSearchQuery() != null)
        {
            return URLQueryTransformer.searchQuery2PrettyQuery(searchFilter.getSearchQuery());
        }
        return "";
    }

    /**
     * Initialize the page when the page has been called by the browser back button
     */
    public void initBackPage()
    {
        HistorySession hs = (HistorySession)BeanHelper.getSessionBean(HistorySession.class);
        FiltersSession fs = (FiltersSession)BeanHelper.getSessionBean(FiltersSession.class);
        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("h") != null)
        {
            filters = new FiltersBean(query, totalNumberOfRecords);
            hs.getCurrentPage().setFilters(fs.getFilters());
            hs.getCurrentPage().setQuery(fs.getWholeQuery());
        }
        else
        {
            filters = new FiltersBean(query, totalNumberOfRecords);
            hs.getCurrentPage().setFilters(fs.getFilters());
            hs.getCurrentPage().setQuery(fs.getWholeQuery());
        }
    }

    /**
     * Add all select {@link Item} to the active {@link Album}, and unselect all {@link Item} from session
     * 
     * @return
     * @throws Exception
     */
    public String addSelectedToActiveAlbum() throws Exception
    {
        addToActiveAlbum(session.getSelected());
        session.getSelected().clear();
        return "pretty:";
    }

    /**
     * Add all {@link Item} of the current {@link ImagesBean} (i.e. browse page) to the active album
     * 
     * @return
     * @throws Exception
     */
    public String addAllToActiveAlbum() throws Exception
    {
        addToActiveAlbum(search(searchQuery, null).getResults());
        return "pretty:";
    }

    /**
     * Delete selected {@link Item}
     * 
     * @return
     * @throws Exception
     */
    public String deleteSelected() throws Exception
    {
        delete(session.getSelected());
        return "pretty:";
    }

    /**
     * Delete all {@link Item} currently browsed
     * 
     * @return
     * @throws Exception
     */
    public String deleteAll() throws Exception
    {
        delete(search(searchQuery, null).getResults());
        return "pretty:";
    }

    /**
     * Withdraw all {@link Item} currently browsed
     * 
     * @return
     * @throws Exception
     */
    public String withdrawAll() throws Exception
    {
        withdraw(search(searchQuery, null).getResults());
        return "pretty:";
    }

    /**
     * Withdraw all selected {@link Item}
     * 
     * @return
     * @throws Exception
     */
    public String withdrawSelected() throws Exception
    {
        withdraw(session.getSelected());
        return "pretty:";
    }

    /**
     * withdraw a list of {@link Item} (defined by their uri)
     * 
     * @param uris
     * @throws Exception
     */
    private void withdraw(List<String> uris) throws Exception
    {
        ItemController ic = new ItemController(session.getUser());
        Collection<Item> items = loadImages(uris);
        int count = 0;
        if ("".equals(discardComment.trim()))
        {
            BeanHelper.error(session.getMessage("error_image_withdraw_discardComment"));
        }
        else
        {
            ic.withdraw((List<Item>)items, discardComment);
            discardComment = null;
            unselect(uris);
            BeanHelper.info(count + " " + session.getLabel("images_withdraw"));
        }
    }

    /**
     * Delete a {@link List} of {@link Item} (defined by their uris).
     * 
     * @param uris
     * @throws Exception
     */
    private void delete(List<String> uris) throws Exception
    {
        Collection<Item> items = loadImages(uris);
        ItemController ic = new ItemController(session.getUser());
        int count = ic.delete((List<Item>)items, session.getUser());
        BeanHelper.info(count + " " + session.getLabel("images_deleted"));
        unselect(uris);
    }

    /**
     * Unselect a list of {@link Item}
     * 
     * @param uris
     */
    private void unselect(List<String> l)
    {
        SessionObjectsController soc = new SessionObjectsController();
        List<String> uris = new ArrayList<String>(l);
        for (String uri : uris)
        {
            soc.unselectItem(uri);
        }
    }

    /**
     * Add a {@link List} of uris to the active album, and write an info message in the {@link FacesMessage}
     * 
     * @param uris
     * @throws Exception
     */
    private void addToActiveAlbum(List<String> uris) throws Exception
    {
        int sizeToAdd = uris.size();
        int sizeBefore = session.getActiveAlbumSize();
        SessionObjectsController soc = new SessionObjectsController();
        soc.addToActiveAlbum(uris);
        int sizeAfter = session.getActiveAlbumSize();
        int added = sizeAfter - sizeBefore;
        int notAdded = sizeToAdd - added;
        String message = "";
        if (added > 0)
        {
            message = " " + added + " " + session.getMessage("images_added_to_active_album");
        }
        if (notAdded > 0)
        {
            message += " " + notAdded + " " + session.getMessage("already_in_active_album");
        }
        BeanHelper.info(message);
    }

    public String getInitComment()
    {
        setDiscardComment("");
        return "";
    }

    public String getSelectedImagesContext()
    {
        return selectedImagesContext;
    }

    public void setSelectedImagesContext(String selectedImagesContext)
    {
        if (selectedImagesContext.equals(session.getSelectedImagesContext()))
        {
            this.selectedImagesContext = selectedImagesContext;
        }
        else
        {
            session.getSelected().clear();
            this.selectedImagesContext = selectedImagesContext;
            session.setSelectedImagesContext(selectedImagesContext);
        }
    }

    public String getImageBaseUrl()
    {
        return navigation.getApplicationUri();
    }

    public String getBackUrl()
    {
        return navigation.getBrowseUrl();
    }

    public String initFacets() throws Exception
    {
        this.setFacets(new FacetsBean(URLQueryTransformer.parseStringQuery(query)));
        return "pretty";
    }

    public List<SelectItem> getSortMenu()
    {
        return sortMenu;
    }

    public void setSortMenu(List<SelectItem> sortMenu)
    {
        this.sortMenu = sortMenu;
    }

    public String getSelectedSortCriterion()
    {
        return selectedSortCriterion;
    }

    public void setSelectedSortCriterion(String selectedSortCriterion)
    {
        this.selectedSortCriterion = selectedSortCriterion;
    }

    public String getSelectedSortOrder()
    {
        return selectedSortOrder;
    }

    public void setSelectedSortOrder(String selectedSortOrder)
    {
        this.selectedSortOrder = selectedSortOrder;
    }

    public String toggleSortOrder()
    {
        if (selectedSortOrder.equals("DESCENDING"))
        {
            selectedSortOrder = "ASCENDING";
        }
        else
        {
            selectedSortOrder = "DESCENDING";
        }
        return getNavigationString();
    }

    public FacetsBean getFacets()
    {
        return facets;
    }

    public void setFacets(FacetsBean facets)
    {
        this.facets = facets;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getQuery()
    {
        return query;
    }

    public FiltersBean getFilters()
    {
        return filters;
    }

    public void setFilters(FiltersBean filters)
    {
        this.filters = filters;
    }

    public String selectAll()
    {
        for (ThumbnailBean bean : getCurrentPartList())
        {
            if (!(session.getSelected().contains(bean.getUri().toString())))
            {
                session.getSelected().add(bean.getUri().toString());
            }
        }
        return getNavigationString();
    }

    public String selectNone()
    {
        session.getSelected().clear();
        return getNavigationString();
    }

    /**
     * Check that at leat one image is editable
     */
    public boolean isImageEditable()
    {
        for (ThumbnailBean tb : getCurrentPartList())
        {
            if (tb.isEditable())
            {
                return true;
            }
        }
        return false;
    }

    public boolean isImageDeletable()
    {
        for (ThumbnailBean tb : getCurrentPartList())
        {
            if (tb.isEditable())
            {
                return true;
            }
        }
        return false;
    }

    public boolean isEditable()
    {
        return false;
    }

    public boolean isVisible()
    {
        return false;
    }

    public boolean isDeletable()
    {
        return false;
    }

    public String getDiscardComment()
    {
        return discardComment;
    }

    public void setDiscardComment(String discardComment)
    {
        this.discardComment = discardComment;
    }

    public void discardCommentListener(ValueChangeEvent event) throws Exception
    {
        discardComment = event.getNewValue().toString();
    }

    public void setSearchQuery(SearchQuery searchQuery)
    {
        this.searchQuery = searchQuery;
    }

    public SearchQuery getSearchQuery()
    {
        return searchQuery;
    }

    public boolean isSimpleSearch()
    {
        return isSimpleSearch;
    }

    public void setSimpleSearch(boolean isSimpleSearch)
    {
        this.isSimpleSearch = isSimpleSearch;
    }

    public Filter getSearchFilter()
    {
        return searchFilter;
    }

    public void setSearchFilter(Filter searchFilter)
    {
        this.searchFilter = searchFilter;
    }
}
