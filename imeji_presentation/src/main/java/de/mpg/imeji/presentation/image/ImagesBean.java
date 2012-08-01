/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.album.AlbumBean;
import de.mpg.imeji.presentation.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.facet.Facet.FacetType;
import de.mpg.imeji.presentation.facet.FacetsBean;
import de.mpg.imeji.presentation.filter.Filter;
import de.mpg.imeji.presentation.filter.FiltersBean;
import de.mpg.imeji.presentation.filter.FiltersSession;
import de.mpg.imeji.presentation.history.HistorySession;
import de.mpg.imeji.presentation.search.URLQueryTransformer;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.PropertyReader;

public class ImagesBean extends BasePaginatorListSessionBean<ThumbnailBean>
{
    private int totalNumberOfRecords;
    private SessionBean sb;
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

    public ImagesBean()
    {
        super();
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
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

    public String getInitPage()
    {
        initMenus();
        if (facets != null)
        {
            facets.getFacets().clear();
        }
        initBackPage();
        try
        {
            searchQuery = URLQueryTransformer.parseStringQuery(query);
        }
        catch (Exception e)
        {
            BeanHelper.error("Error parsing query");
            logger.error("Error parsing query", e);
        }
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

    private void initMenus()
    {
        sortMenu = new ArrayList<SelectItem>();
        sortMenu.add(new SelectItem(null, sb.getLabel("default")));
        sortMenu.add(new SelectItem(SearchIndex.names.PROPERTIES_CREATION_DATE, sb
                .getLabel(SearchIndex.names.PROPERTIES_CREATION_DATE.name())));
        sortMenu.add(new SelectItem(SearchIndex.names.IMAGE_COLLECTION, sb.getLabel(SearchIndex.names.IMAGE_COLLECTION
                .name())));
        sortMenu.add(new SelectItem(SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE, sb
                .getLabel(SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE.name())));
        selectedSortOrder = SortOrder.DESCENDING.name();
    }

    @Override
    public String getNavigationString()
    {
        if (sb.getSelectedImagesContext() != null && !(sb.getSelectedImagesContext().equals("pretty:images")))
        {
            sb.getSelected().clear();
        }
        sb.setSelectedImagesContext("pretty:images");
        return "pretty:images";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return totalNumberOfRecords;
    }

    public SortCriterion initSortCriterion()
    {
        SortCriterion sortCriterion = new SortCriterion();
        if (getSelectedSortCriterion() != null)
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
        Collection<Item> items = loadImages(searchResult);
        return ImejiFactory.imageListToThumbList(items);
    }

    public SearchResult search(SearchQuery searchQuery, SortCriterion sortCriterion)
    {
        ItemController controller = new ItemController(sb.getUser());
        return controller.searchImages(searchQuery, sortCriterion);
    }

    // public SearchResult search(List<SearchCriterion> scList, SortCriterion sortCriterion)
    // {
    // ItemController controller = new ItemController(sb.getUser());
    // return controller.searchImages(scList, sortCriterion);
    // }
    public Collection<Item> loadImages(SearchResult searchResult)
    {
        ItemController controller = new ItemController(sb.getUser());
        try
        {
            return controller.loadItems(searchResult.getResults(), getElementsPerPage(), getOffset());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getSimpleQuery()
    {
        if (searchFilter != null && searchFilter.getSearchQuery() != null)
        {
            return URLQueryTransformer.transform2URL(searchFilter.getSearchQuery());
        }
        return "";
    }

    public void initBackPage()
    {
        HistorySession hs = (HistorySession)BeanHelper.getSessionBean(HistorySession.class);
        FiltersSession fs = (FiltersSession)BeanHelper.getSessionBean(FiltersSession.class);
        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("h") != null)
        {
            fs.setFilters(hs.getCurrentPage().getFilters());
            query = hs.getCurrentPage().getQuery();
        }
        else
        {
            filters = new FiltersBean(query, totalNumberOfRecords);
            hs.getCurrentPage().setFilters(fs.getFilters());
            hs.getCurrentPage().setQuery(fs.getWholeQuery());
        }
    }

    public String addToActiveAlbum() throws Exception
    {
        AlbumBean activeAlbum = sb.getActiveAlbum();
        AlbumController ac = new AlbumController(sb.getUser());
        int els = getElementsPerPage();
        int page = getCurrentPageNumber();
        setElementsPerPage(totalNumberOfRecords);
        setCurrentPageNumber(1);
        update();
        setElementsPerPage(els);
        setCurrentPageNumber(page);
        int count = 0;
        for (ThumbnailBean tb : getCurrentPartList())
        {
            if (tb.isInActiveAlbum())
            {
                BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("image")
                        + " "
                        + tb.getFilename()
                        + " "
                        + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                                .getMessage("already_in_active_album"));
            }
            else
            {
                activeAlbum.getAlbum().getImages().add(tb.getUri());
                count++;
            }
        }
        try
        {
            ac.update(activeAlbum.getAlbum());
            BeanHelper.info(count
                    + " "
                    + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                            .getMessage("images_added_to_active_album"));
        }
        catch (Exception e)
        {
            BeanHelper.error(e.getMessage());
            activeAlbum.setAlbum(ac.retrieve(activeAlbum.getAlbum().getId()));
            e.printStackTrace();
        }
        return "pretty:";
    }

    public String deleteAll() throws Exception
    {
        ItemController ic = new ItemController(sb.getUser());
        CollectionController cc = new CollectionController(sb.getUser());
        CollectionImeji coll = null;
        super.setCurrentPageNumber(1);
        super.setElementsPerPage(10000);
        SearchResult searchResult = search(searchQuery, null);
        Collection<Item> items = loadImages(searchResult);
        if (items != null && !items.isEmpty())
            coll = cc.retrieve(items.iterator().next().getCollection());
        int count = 0;
        for (Item im : items)
        {
            try
            {
                ic.delete(im, sb.getUser());
                if (coll.getImages().contains(im.getId()))
                    coll.getImages().remove(im.getId());
                count++;
            }
            catch (Exception e)
            {
                BeanHelper.error(sb.getMessage("error_image_delete") + " " + im.getFilename());
                e.printStackTrace();
            }
        }
        BeanHelper.info(count + " " + sb.getLabel("images_deleted"));
        cc.update(coll);
        sb.getSelected().clear();
        return "pretty:";
    }

    public String withdrawAll() throws Exception
    {
        ItemController ic = new ItemController(sb.getUser());
        SearchResult searchResult = search(searchQuery, null);
        Collection<Item> items = loadImages(searchResult);
        int count = 0;
        if ("".equals(discardComment.trim()))
        {
            BeanHelper.error(sb.getMessage("error_image_withdraw_discardComment"));
        }
        else
        {
            for (Item im : items)
            {
                try
                {
                    im.setDiscardComment(discardComment);
                    ic.withdraw(im);
                    count++;
                }
                catch (Exception e)
                {
                    BeanHelper.error(sb.getMessage("error_image_withdraw") + " " + im.getFilename());
                    e.printStackTrace();
                }
            }
            discardComment = null;
            sb.getSelected().clear();
            BeanHelper.info(count + " " + sb.getLabel("images_withdraw"));
        }
        return "pretty:";
    }

    public String getImageBaseUrl()
    {
        return navigation.getApplicationUri();
    }

    public String getBackUrl()
    {
        return navigation.getImagesUrl();
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
            if (!(sb.getSelected().contains(bean.getUri())))
            {
                sb.getSelected().add(bean.getUri());
            }
        }
        return getNavigationString();
    }

    public String selectNone()
    {
        sb.getSelected().clear();
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
            if (tb.isDeletable())
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
}
