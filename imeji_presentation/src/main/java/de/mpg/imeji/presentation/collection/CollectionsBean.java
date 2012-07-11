/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.beans.SuperContainerBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;

public class CollectionsBean extends SuperContainerBean<CollectionListItem>
{
    private int totalNumberOfRecords;
    private SessionBean sb;
    private String query = "";

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
        // ImejiJena.printModel(ImejiJena.imageModel);
        // initMenus();
        if (sb.getUser() != null)
        {
            sb.setUser(uc.retrieve(sb.getUser().getEmail()));
        }
        CollectionController controller = new CollectionController(sb.getUser());
        Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();
        SearchQuery searchQuery = new SearchQuery();
        if (getFilter() != null)
        {
            searchQuery.addLogicalRelation(LOGICAL_RELATIONS.AND);
            searchQuery.addPair(getFilter());
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("q"))
        {
            query = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("q");
        }
        if (!"".equals(query))
        {
//            if (query.startsWith("\"") && query.endsWith("\""))
//            {
//                scList.add(new SearchCriterion(SearchIndexes.CONTAINER_METADATA_TITLE, query));
//                scList.add(new SearchCriterion(Operator.OR, SearchIndexes.CONTAINER_METADATA_DESCRIPTION, query,
//                        Filtertype.REGEX));
//                scList.add(new SearchCriterion(Operator.OR, SearchIndexes.CONTAINER_METADATA_PERSON_FAMILY_NAME, query,
//                        Filtertype.REGEX));
//                scList.add(new SearchCriterion(Operator.OR, SearchIndexes.CONTAINER_METADATA_PERSON_GIVEN_NAME, query,
//                        Filtertype.REGEX));
//                scList.add(new SearchCriterion(Operator.OR, SearchIndexes.CONTAINER_METADATA_PERSON_COMPLETE_NAME,
//                        query, Filtertype.REGEX));
//                scList.add(new SearchCriterion(Operator.OR, SearchIndexes.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME,
//                        query, Filtertype.REGEX));
//                scList.add(new SearchCriterion(Operator.OR, SearchIndexes.COLLECTION_PROFILE, query, Filtertype.URI));
//            }
//            else
//            {
//                for (String s : query.split("\\s"))
//                {
//                    scList.add(new SearchCriterion(SearchIndexes.CONTAINER_METADATA_TITLE, s));
//                    scList.add(new SearchCriterion(Operator.OR, SearchIndexes.CONTAINER_METADATA_DESCRIPTION, s,
//                            Filtertype.REGEX));
//                    scList.add(new SearchCriterion(Operator.OR, SearchIndexes.CONTAINER_METADATA_PERSON_FAMILY_NAME, s,
//                            Filtertype.REGEX));
//                    scList.add(new SearchCriterion(Operator.OR, SearchIndexes.CONTAINER_METADATA_PERSON_GIVEN_NAME, s,
//                            Filtertype.REGEX));
//                    scList.add(new SearchCriterion(Operator.OR,
//                            SearchIndexes.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME, s, Filtertype.REGEX));
//                    scList.add(new SearchCriterion(Operator.OR, SearchIndexes.COLLECTION_PROFILE, s, Filtertype.URI));
//                }
//            }
        }
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setIndex(Search.getIndex(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        SearchResult results = controller.search(searchQuery, sortCriterion, limit, offset);
        collections = controller.load(results.getResults(), limit, offset);
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
