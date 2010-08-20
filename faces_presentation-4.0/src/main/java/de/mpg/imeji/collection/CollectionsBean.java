package de.mpg.imeji.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.beans.SuperContainerBean;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.vo.CollectionImeji;

public class CollectionsBean extends SuperContainerBean<CollectionImeji>
{
    private CollectionController controller;
    private int totalNumberOfRecords;
  
    

    public CollectionsBean()
    {
        super();
        this.controller = new CollectionController(null);
    }

    @Override
    public String getNavigationString()
    {
        return "pretty:collections";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
       return totalNumberOfRecords;
    }

    @Override
    public List<CollectionImeji> retrieveList(int offset, int limit)
    {
        Collection<CollectionImeji> collections = controller.retrieveAll();
        totalNumberOfRecords = collections.size();
        
        
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        
        
        collections = controller.search(null, new ArrayList<SearchCriterion>(), sortCriterion, limit, offset);
        
        return (List<CollectionImeji>)collections;
    }


   
}
