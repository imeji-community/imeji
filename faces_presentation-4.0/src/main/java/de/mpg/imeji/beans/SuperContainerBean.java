package de.mpg.imeji.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;

public abstract class SuperContainerBean<T> extends BasePaginatorListSessionBean<T>
{
    private String selectedMenu;
    
    private String selectedSortCriterion;
    private String selectedSortOrder;

    private SessionBean sb;

    
    private List<SelectItem> sortMenu = new ArrayList<SelectItem>();
    

    public SuperContainerBean()
    {
    	selectedMenu = "SORTING";
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        initMenus();
    }
    

    private void initMenus()
    {
         sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_STATUS,sb.getLabel(ImejiNamespaces.PROPERTIES_STATUS.name())));
         sortMenu.add(new SelectItem(ImejiNamespaces.CONTAINER_METADATA_TITLE,sb.getLabel(ImejiNamespaces.CONTAINER_METADATA_TITLE.name())));
         sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE,sb.getLabel(ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE.name())));
         selectedSortCriterion = ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE.name();
         selectedSortOrder = SortOrder.DESCENDING.name();
        
    }
    
    public void reset()
    {
        super.reset();
        initMenus();
    }


    public void setSelectedMenu(String selectedMenu)
    {
        this.selectedMenu = selectedMenu;
    }

    public String getSelectedMenu()
    {
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
        if(selectedSortOrder.equals("DESCENDING"))
        {
            selectedSortOrder="ASCENDING";
        }
        else
        {
            selectedSortOrder="DESCENDING";
        }
        return getNavigationString();
        
    }
}
