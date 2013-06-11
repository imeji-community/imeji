/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.filter.Filter;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Java Bean for {@link Container} browse pages (collections and albums)
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @param <T>
 */
public abstract class SuperContainerBean<T> extends BasePaginatorListSessionBean<T>
{
    protected String selectedMenu;
    private String selectedSortCriterion;
    private String selectedSortOrder;
    private String selectedFilter;
    private SessionBean sb;
    private List<SelectItem> sortMenu = new ArrayList<SelectItem>();
    protected List<SelectItem> filterMenu = new ArrayList<SelectItem>();
    private static Logger logger = Logger.getLogger(SuperContainerBean.class);

    /**
     * Constructor
     */
    public SuperContainerBean()
    {
        selectedMenu = "SORTING";
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (sessionBean.getUser() != null)
        {
            selectedFilter = "my";
        }
        else
        {
            selectedFilter = "all";
        }
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        initMenus();
        selectedSortCriterion = SearchIndex.names.modified.name();
        selectedSortOrder = SortOrder.DESCENDING.name();
        try
        {
            setElementsPerPage(Integer.parseInt(PropertyReader.getProperty("imeji.container.list.size")));
        }
        catch (Exception e)
        {
            logger.error("Error loading property imeji.container.list.size", e);
        }
        try
        {
            String options = PropertyReader.getProperty("imeji.container.list.size.options");
            for (String option : options.split(","))
            {
                getElementsPerPageSelectItems().add(new SelectItem(option));
            }
        }
        catch (Exception e)
        {
            logger.error("Error reading property imeji.container.list.size.options", e);
        }
    }
    
    /**
     * Initialize the page
     * 
     * @return
     */
    public String getInit()
    {
        if (UrlHelper.getParameterValue("f") != null && !UrlHelper.getParameterValue("f").equals(""))
        {
            selectedFilter = UrlHelper.getParameterValue("f");
        }
        if (UrlHelper.getParameterValue("tab") != null && !UrlHelper.getParameterValue("tab").equals(""))
        {
            selectedMenu = UrlHelper.getParameterValue("tab");
        }
        initMenus();
        return "";
    }

    /**
     * Initialize the menus of the page
     */
    protected void initMenus()
    {
        sortMenu = new ArrayList<SelectItem>();
        sortMenu.add(new SelectItem(SearchIndex.names.cont_title.name(), sb.getLabel("sort_title")));
        sortMenu.add(new SelectItem(SearchIndex.names.modified.name(), sb.getLabel("sort_date_mod")));
        filterMenu = new ArrayList<SelectItem>();
        filterMenu.add(new SelectItem("all", sb.getLabel("all_except_withdrawn")));
        if (sb.getUser() != null)
        {
            sortMenu.add(new SelectItem(SearchIndex.names.status.name(), sb.getLabel("sort_status")));
            filterMenu.add(new SelectItem("my", sb.getLabel("my_except_withdrawn")));
            filterMenu.add(new SelectItem("private", sb.getLabel("only_private")));
        }
        filterMenu.add(new SelectItem("public", sb.getLabel("only_public")));
        filterMenu.add(new SelectItem("withdrawn", sb.getLabel("only_withdrawn")));
    }

    /**
     * Reset the page, called when url paramenter "init" is set to 1
     */
    @Override
    public void reset()
    {
        super.reset();
        initMenus();
    }

    /**
     * Return the current {@link Filter} for the {@link List} of {@link Container}
     * 
     * @return
     */
    public SearchPair getFilter()
    {
        SearchPair pair = null;
        if ("my".equals(selectedFilter))
        {
            pair = new SearchPair(Search.getIndex(SearchIndex.names.user), SearchOperators.EQUALS, ObjectHelper.getURI(
                    User.class, sb.getUser().getEmail()).toString());
        }
        else if ("private".equals(selectedFilter))
        {
            pair = new SearchPair(Search.getIndex(SearchIndex.names.status), SearchOperators.EQUALS,
                    "http://imeji.org/terms/status#PENDING");
        }
        else if ("public".equals(selectedFilter))
        {
            pair = new SearchPair(Search.getIndex(SearchIndex.names.status), SearchOperators.EQUALS,
                    "http://imeji.org/terms/status#RELEASED");
        }
        else if ("withdrawn".equals(selectedFilter))
        {
            pair = new SearchPair(Search.getIndex(SearchIndex.names.status), SearchOperators.EQUALS,
                    "http://imeji.org/terms/status#WITHDRAWN");
        }
        return pair;
    }

    /**
     * setter
     * 
     * @param selectedMenu
     */
    public void setSelectedMenu(String selectedMenu)
    {
        this.selectedMenu = selectedMenu;
    }

    /**
     * getter: Return the current tab name in the actions div on the xhtml page
     * 
     * @return
     */
    public String getSelectedMenu()
    {
        return selectedMenu;
    }

    /**
     * select all {@link Container} on the page
     * 
     * @return
     */
    public String selectAll()
    {
        return getNavigationString();
    }

    /**
     * Unselect all {@link Container} on the page
     * 
     * @return
     */
    public String selectNone()
    {
        return getNavigationString();
    }

    /**
     * setter
     * 
     * @param sortMenu
     */
    public void setSortMenu(List<SelectItem> sortMenu)
    {
        this.sortMenu = sortMenu;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<SelectItem> getSortMenu()
    {
        return sortMenu;
    }

    public void setSelectedSortOrder(String selectedSortOrder)
    {
        this.selectedSortOrder = selectedSortOrder;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getSelectedSortOrder()
    {
        return selectedSortOrder;
    }

    /**
     * setter
     * 
     * @param selectedSortCriterion
     */
    public void setSelectedSortCriterion(String selectedSortCriterion)
    {
        this.selectedSortCriterion = selectedSortCriterion;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getSelectedSortCriterion()
    {
        return selectedSortCriterion;
    }

    /**
     * Change the sort sort order
     * 
     * @return
     */
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

    /**
     * return the {@link Filter} name defined in the url as a {@link String}
     * 
     * @return
     */
    public String getSelectedFilter()
    {
        return selectedFilter;
    }

    /**
     * setter
     * 
     * @param selectedFilter
     */
    public void setSelectedFilter(String selectedFilter)
    {
        this.selectedFilter = selectedFilter;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<SelectItem> getFilterMenu()
    {
        return filterMenu;
    }

    /**
     * setter
     * 
     * @param filterMenu
     */
    public void setFilterMenu(List<SelectItem> filterMenu)
    {
        this.filterMenu = filterMenu;
    }

    /**
     * return the internationalized labels of the {@link Filter}
     * 
     * @return
     */
    public String getFilterLabel()
    {
        for (SelectItem si : filterMenu)
        {
            if (selectedFilter.equals(si.getValue()))
            {
                return si.getLabel();
            }
        }
        return sb.getLabel("all_except_withdrawn");
    }
}