package de.mpg.imeji.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;
import de.mpg.imeji.vo.CollectionVO;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.vo.CollectionImeji;

public abstract class SuperContainerBean<T> extends BasePaginatorListSessionBean<T>
{
    private CollectionController controller;
    private String selectedMenu;
    
    private String selectedSortCriterion;
    private String selectedSortOrder;
    

    
    private List<SelectItem> sortMenu = new ArrayList<SelectItem>();
    

    public SuperContainerBean()
    {
        initMenus();
    }
    

    private void initMenus()
    {
         sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_STATUS,ImejiNamespaces.PROPERTIES_STATUS.name()));
         sortMenu.add(new SelectItem(ImejiNamespaces.CONTAINER_METADATA_TITLE,ImejiNamespaces.CONTAINER_METADATA_TITLE.name()));
         sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE,ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE.name()));
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
        return null;
    }
    
    public String selectNone()
    {
        return null;
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
