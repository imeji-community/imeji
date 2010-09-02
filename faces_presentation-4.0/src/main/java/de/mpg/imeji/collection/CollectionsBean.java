package de.mpg.imeji.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.beans.SuperContainerBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.vo.CollectionImeji;

public class CollectionsBean extends SuperContainerBean<ViewCollectionBean>
{
  
    private int totalNumberOfRecords;
    private SessionBean sb;
  
    

    public CollectionsBean()
    {
        super();
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
       
        
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
    public List<ViewCollectionBean> retrieveList(int offset, int limit)
    {
        CollectionController controller = new CollectionController(sb.getUser());
        Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();
        try
        {
            collections = controller.search(new ArrayList<SearchCriterion>(), null, -1, offset);
            totalNumberOfRecords = collections.size();
            
            
            SortCriterion sortCriterion = new SortCriterion();
            sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
            sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        
        
      
            collections = controller.search(new ArrayList<SearchCriterion>(), sortCriterion, limit, offset);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        return ImejiFactory.collectionListToBeanList(collections);
    }


   
}
