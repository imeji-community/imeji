package de.mpg.imeji.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import com.ocpsoft.pretty.PrettyContext;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.beans.SuperContainerBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
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
    public List<ViewCollectionBean> retrieveList(int offset, int limit)
    {
        CollectionController controller = new CollectionController(sb.getUser());
        Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();
        try
        {
            totalNumberOfRecords = controller.countAllCollections();            
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
    
    public SessionBean getSb() {
		return sb;
	}

	public void setSb(SessionBean sb) {
		this.sb = sb;
	}
	    
	public String selectAll() {
		for(CollectionBean bean: getCurrentPartList()){
			if(bean.getCollection().getProperties().getStatus() != Status.RELEASED){
				bean.setSelected(true);
				if(!(sb.getSelectedCollections().contains(bean.getCollection().getId())))
					sb.getSelectedCollections().add(bean.getCollection().getId());
			}
		}
		return "";
	}
	
	public String selectNone(){
		sb.getSelectedCollections().clear();
		return "";
	}
	
	public String deleteAll() throws Exception{
		for(URI uri : sb.getSelectedCollections()){
			CollectionController collectionController = new CollectionController(sb.getUser());
			CollectionImeji collection = collectionController.retrieve(uri);
			collectionController.delete(collection, sb.getUser());
		}
		sb.getSelectedCollections().clear();
		return "pretty:collections";
	}
  	

   
}
