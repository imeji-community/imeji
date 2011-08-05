package de.mpg.imeji.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.beans.SuperContainerBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.search.SearchResult;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Properties.Status;

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
    public List<ViewCollectionBean> retrieveList(int offset, int limit) throws Exception
    {
    	UserController uc = new UserController(sb.getUser());
    	initMenus();
        if (sb.getUser() != null)
        {
        	sb.setUser(uc.retrieve(sb.getUser().getEmail()));
        }
        
        CollectionController controller = new CollectionController(sb.getUser());
        Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();
        
        List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
        
        if (getFilter() != null)
        {
        	scList.add(getFilter());
        }
        
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        SearchResult results = controller.search(scList, sortCriterion, limit, offset);
        collections = controller.load(results.getResults(), limit, offset);
        totalNumberOfRecords = results.getNumberOfRecords();
        return ImejiFactory.collectionListToBeanList(collections);
    }
    
    public SessionBean getSb() 
    {
		return sb;
	}

	public void setSb(SessionBean sb) 
	{
		this.sb = sb;
	}
	    
	public String selectAll() 
	{
		for(CollectionBean bean: getCurrentPartList())
		{
			if(bean.getCollection().getProperties().getStatus() != Status.RELEASED)
			{
				bean.setSelected(true);
				if(!(sb.getSelectedCollections().contains(bean.getCollection().getId())))
				{
					sb.getSelectedCollections().add(bean.getCollection().getId());
				}
			}
		}
		return "";
	}
	
	public String selectNone()
	{
		sb.getSelectedCollections().clear();
		return "";
	}
	
	public String deleteAll() throws Exception
	{
		int count = 0;
		for(URI uri : sb.getSelectedCollections())
		{
			CollectionController collectionController = new CollectionController(sb.getUser());
			CollectionImeji collection = collectionController.retrieve(uri);
			collectionController.delete(collection, sb.getUser());
			count++;
		}
		sb.getSelectedCollections().clear();
		BeanHelper.info(count + " " + sb.getMessage("success_collections_delete"));
		return "pretty:collections";
	}
}
