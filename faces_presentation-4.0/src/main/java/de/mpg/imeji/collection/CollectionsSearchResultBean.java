package de.mpg.imeji.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.beans.SuperContainerBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.vo.CollectionImeji;

public class CollectionsSearchResultBean extends SuperContainerBean<ViewCollectionBean>
{
  
    private int totalNumberOfRecords;
    private SessionBean sb;
  
    private String query;
    

    public CollectionsSearchResultBean()
    {
        super();
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
       
        
    }

    @Override
    public String getNavigationString()
    {
        return "pretty:collectionsSearchResults";
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
        
        List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
        scList.add(new SearchCriterion(ImejiNamespaces.CONTAINER_METADATA_TITLE, getQuery()));
        scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_DESCRIPTION, getQuery(), Filtertype.REGEX));
        scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_PERSON_FAMILY_NAME, getQuery(), Filtertype.REGEX));
        scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_PERSON_GIVEN_NAME, getQuery(), Filtertype.REGEX));
        scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME, getQuery(), Filtertype.REGEX));
        scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.COLLECTION_PROFILE, getQuery(), Filtertype.URI));
        try
        {
            collections = controller.search(scList, null, -1, offset);
            totalNumberOfRecords = collections.size();
            logger.info("Found " + totalNumberOfRecords + "collections");
            
            SortCriterion sortCriterion = new SortCriterion();
            sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
            sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        
        
      
            collections = controller.search(scList, sortCriterion, limit, offset);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        return ImejiFactory.collectionListToBeanList(collections);
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getQuery()
    {
        return query;
    }


   
}
