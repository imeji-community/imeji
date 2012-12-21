/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

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

    public SuperContainerBean()
    {
        selectedMenu = "SORTING";
        selectedFilter = "all";
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        initMenus();
        selectedSortCriterion = Search.getIndex("PROPERTIES_LAST_MODIFICATION_DATE").getName();
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

    protected void initMenus()
    {
        sortMenu = new ArrayList<SelectItem>();
        sortMenu.add(new SelectItem("PROPERTIES_STATUS", sb.getLabel(Search.getIndex("PROPERTIES_STATUS").getName())));
        sortMenu.add(new SelectItem("CONTAINER_METADATA_TITLE", sb.getLabel(Search.getIndex("CONTAINER_METADATA_TITLE")
                .getName())));
        sortMenu.add(new SelectItem("PROPERTIES_LAST_MODIFICATION_DATE", sb.getLabel(Search.indexes.get(
                "PROPERTIES_LAST_MODIFICATION_DATE").getName())));
        filterMenu = new ArrayList<SelectItem>();
        filterMenu.add(new SelectItem("all", sb.getLabel("all_except_withdrawn")));
        if (sb.getUser() != null)
        {
            filterMenu.add(new SelectItem("my", sb.getLabel("my_except_withdrawn")));
            filterMenu.add(new SelectItem("private", sb.getLabel("only_private")));
        }
        filterMenu.add(new SelectItem("public", sb.getLabel("only_public")));
        filterMenu.add(new SelectItem("withdrawn", sb.getLabel("only_withdrawn")));
    }

    public String getInitMenus()
    {
        initMenus();
        return "";
    }

    public void reset()
    {
        super.reset();
        initMenus();
    }

    public SearchPair getFilter()
    {
        SearchPair pair = null;
        if ("my".equals(selectedFilter))
        {
            pair = new SearchPair(Search.getIndex("MY_IMAGES"), SearchOperators.URI, ObjectHelper.getURI(User.class,
                    sb.getUser().getEmail()).toString());
        }
        else if ("private".equals(selectedFilter))
        {
            pair = new SearchPair(Search.getIndex("PROPERTIES_STATUS"), SearchOperators.URI,
                    "http://imeji.org/terms/status#PENDING");
        }
        else if ("public".equals(selectedFilter))
        {
            pair = new SearchPair(Search.getIndex("PROPERTIES_STATUS"), SearchOperators.URI,
                    "http://imeji.org/terms/status#RELEASED");
        }
        else if ("withdrawn".equals(selectedFilter))
        {
            pair = new SearchPair(Search.getIndex("PROPERTIES_STATUS"), SearchOperators.URI,
                    "http://imeji.org/terms/status#WITHDRAWN");
        }
        return pair;
    }

    public void setSelectedMenu(String selectedMenu)
    {
        this.selectedMenu = selectedMenu;
    }

    public String getSelectedMenu()
    {
        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("tab"))
        {
            selectedMenu = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tab");
        }
        return selectedMenu;
    }

    public String selectAll()
    {
        return getNavigationString();
    }

    public String selectNone()
    {
        return getNavigationString();
    }

    public void setSortMenu(List<SelectItem> sortMenu)
    {
        this.sortMenu = sortMenu;
    }

    public List<SelectItem> getSortMenu()
    {
        return sortMenu;
    }

    public void setSelectedSortOrder(String selectedSortOrder)
    {
        this.selectedSortOrder = selectedSortOrder;
    }

    public String getSelectedSortOrder()
    {
        return selectedSortOrder;
    }

    public void setSelectedSortCriterion(String selectedSortCriterion)
    {
        this.selectedSortCriterion = selectedSortCriterion;
    }

    public String getSelectedSortCriterion()
    {
        return selectedSortCriterion;
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

    public String getSelectedFilter()
    {
        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("f")
                && !FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("f").toString()
                        .equals(""))
        {
            selectedFilter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("f");
        }
        return selectedFilter;
    }

    public void setSelectedFilter(String selectedFilter)
    {
        this.selectedFilter = selectedFilter;
    }

    public List<SelectItem> getFilterMenu()
    {
        return filterMenu;
    }

    public void setFilterMenu(List<SelectItem> filterMenu)
    {
        this.filterMenu = filterMenu;
    }

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
