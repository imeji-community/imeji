/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import com.hp.hpl.jena.sparql.pfunction.library.container;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.query.URLQueryTransformer;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.beans.SuperContainerBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.mpg.imeji.logic.notification.CommonMessages.getSuccessCollectionDeleteMessage;

/**
 * Bean for the collections page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CollectionsBean extends SuperContainerBean<CollectionListItem>
{
    private int totalNumberOfRecords;
    private SessionBean sb;
    /**
     * The comment required to discard a {@link container}
     */
    private String discardComment = "";

    /**
     * Bean for the collections page
     */
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
    public List<CollectionListItem> retrieveList(int offset, int limit) throws Exception
    {   
        // initMenus();
        CollectionController controller = new CollectionController();
        Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();
        SearchQuery searchQuery = new SearchQuery();
      
        if (!"".equals(query))
        {
            searchQuery = URLQueryTransformer.parseStringQuery(query);
        }
        SearchPair sp = getFilter();
        if (sp != null)
        {
            searchQuery.addLogicalRelation(LOGICAL_RELATIONS.AND);
            searchQuery.addPair(sp);
        }
        SortCriterion sortCriterion = new SortCriterion();  
        sortCriterion.setIndex(SPARQLSearch.getIndex(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        SearchResult results = controller.search(searchQuery, sortCriterion, limit, offset, sb.getUser());
        collections = controller.retrieveLazy(results.getResults(), limit, offset, sb.getUser());
        totalNumberOfRecords = results.getNumberOfRecords();
        return ImejiFactory.collectionListToListItem(collections, sb.getUser());
    }

    /**
     * getter
     * 
     * @return
     */
    public SessionBean getSb()
    {
        return sb;
    }

    /**
     * setter
     * 
     * @param sb
     */
    public void setSb(SessionBean sb)
    {
        this.sb = sb;
    }

    @Override
    public String selectAll()
    {
        for (CollectionListItem bean : getCurrentPartList())
        {
            if (Status.PENDING.toString().equals(bean.getStatus()))
            {
                bean.setSelected(true);
                if (!(sb.getSelectedCollections().contains(bean.getUri())))
                {
                    sb.getSelectedCollections().add(bean.getUri());
                }
            }
        }
        return "";
    }

    @Override
    public String selectNone()
    {
        sb.getSelectedCollections().clear();
        return "";
    }

    /**
     * Delete all selected {@link CollectionImeji}
     * 
     * @return
     * @throws Exception
     */
    public String deleteAll() throws Exception
    {
        int count = 0;
        for (URI uri : sb.getSelectedCollections())
        {
            CollectionController collectionController = new CollectionController();
            CollectionImeji collection = collectionController.retrieve(uri, sb.getUser());
            collectionController.delete(collection, sb.getUser());
            count++;
            
            BeanHelper.info(getSuccessCollectionDeleteMessage(collection.getMetadata().getTitle(), sb));
        }
        sb.getSelectedCollections().clear();
        if (count == 0)
        {
            BeanHelper.warn(sb.getMessage("error_delete_no_collection_selected"));
        }
        return "pretty:collections";
    }

    /**
     * needed for searchQueryDisplayArea.xhtml component
     * 
     * @return
     */
    public String getSimpleQuery()
    {
        if (query != null)
        {
            return query;
        }
        return "";
    }

    /**
     * Collection search is always a simple search (needed for searchQueryDisplayArea.xhtml component)
     * 
     * @return
     */
    public boolean isSimpleSearch()
    {
        return true;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getDiscardComment()
    {
        return discardComment;
    }

    /**
     * setter
     * 
     * @param discardComment
     */
    public void setDiscardComment(String discardComment)
    {
        this.discardComment = discardComment;
    }
}
