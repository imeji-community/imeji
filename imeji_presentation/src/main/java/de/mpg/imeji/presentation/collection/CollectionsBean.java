/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.beans.SuperContainerBean;
import de.mpg.imeji.presentation.search.URLQueryTransformer;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
/**
 * 
 * Bean for the collections page
 *
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */

public class CollectionsBean extends SuperContainerBean<CollectionListItem>
{
    private int totalNumberOfRecords;
    private SessionBean sb;
    private String query = "";

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
        UserController uc = new UserController(sb.getUser());
        initMenus();
        if (sb.getUser() != null)
        {
            sb.setUser(uc.retrieve(sb.getUser().getEmail()));
        }
        CollectionController controller = new CollectionController(sb.getUser());
        Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();
        SearchQuery searchQuery = new SearchQuery();
        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("q"))
        {
            query = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("q");
        }
        if (!"".equals(query))
        {
            searchQuery = URLQueryTransformer.parseStringQuery(query);
        }
        if (getFilter() != null)
        {
            searchQuery.addLogicalRelation(LOGICAL_RELATIONS.AND);
            searchQuery.addPair(getFilter());
        }
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setIndex(Search.getIndex(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        SearchResult results = controller.search(searchQuery, sortCriterion, limit, offset);
        collections = controller.loadCollectionsLazy(results.getResults(), limit, offset);
        totalNumberOfRecords = results.getNumberOfRecords();
        return ImejiFactory.collectionListToListItem(collections, sb.getUser());
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
        for (CollectionListItem bean : getCurrentPartList())
        {
            if (bean.getStatus() == Status.PENDING.toString())
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

    public String selectNone()
    {
        sb.getSelectedCollections().clear();
        return "";
    }

    public String deleteAll() throws Exception
    {
        int count = 0;
        for (URI uri : sb.getSelectedCollections())
        {
            CollectionController collectionController = new CollectionController(sb.getUser());
            CollectionImeji collection = collectionController.retrieve(uri);
            collectionController.delete(collection, sb.getUser());
            count++;
        }
        sb.getSelectedCollections().clear();
        if (count == 0)
            BeanHelper.warn(sb.getMessage("error_delete_no_collection_selected"));
        else
            BeanHelper.info(count + " " + sb.getMessage("success_collections_delete"));
        return "pretty:collections";
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
