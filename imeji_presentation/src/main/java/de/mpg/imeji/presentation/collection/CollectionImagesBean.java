/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.facet.FacetsBean;
import de.mpg.imeji.presentation.image.ImagesBean;
import de.mpg.imeji.presentation.image.ThumbnailBean;
import de.mpg.imeji.presentation.search.URLQueryTransformer;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectLoader;

public class CollectionImagesBean extends ImagesBean
{
    private int totalNumberOfRecords;
    private String id = null;
    private URI uri;
    private SessionBean sb = null;
    private CollectionImeji collection;
    private Navigation navigation;
    private SearchQuery searchQuery = new SearchQuery();

    public CollectionImagesBean()
    {
        super();
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        this.navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }

    public void init()
    {
        collection = ObjectLoader.loadCollectionLazy(ObjectHelper.getURI(CollectionImeji.class, id), sb.getUser());
        List<SelectItem> sortMenu = new ArrayList<SelectItem>();
        sortMenu.add(new SelectItem(null, sb.getLabel("default")));
        sortMenu.add(new SelectItem(SearchIndex.names.PROPERTIES_CREATION_DATE, sb
                .getLabel(SearchIndex.names.PROPERTIES_CREATION_DATE.name())));
        sortMenu.add(new SelectItem(SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE, sb
                .getLabel(SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE.name())));
        setSortMenu(sortMenu);
    }

    @Override
    public String getNavigationString()
    {
        if (collection != null)
        {
            if (sb.getSelectedImagesContext() != null
                    && !(sb.getSelectedImagesContext()
                            .equals("pretty:collectionImages" + collection.getId().toString())))
            {
                sb.getSelected().clear();
            }
            sb.setSelectedImagesContext("pretty:collectionImages" + collection.getId().toString());
        }
        return "pretty:collectionImages";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return totalNumberOfRecords;
    }

    @Override
    public List<ThumbnailBean> retrieveList(int offset, int limit)
    {
        if (getFacets() != null)
        {
            getFacets().getFacets().clear();
        }

        SortCriterion sortCriterion = initSortCriterion();
        initBackPage();
        try
        {
            searchQuery = URLQueryTransformer.parseStringQuery(getQuery());
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        uri = ObjectHelper.getURI(CollectionImeji.class, id);
        SearchResult results = search(searchQuery, sortCriterion);
        totalNumberOfRecords = results.getNumberOfRecords();
        results.setQuery(getQuery());
        results.setSort(sortCriterion);
        List<Item> items = (List<Item>)loadImages(results);
        return ImejiFactory.imageListToThumbList(items);
    }

    public SearchResult search(SearchQuery searchQuery, SortCriterion sortCriterion)
    {
        ItemController controller = new ItemController(sb.getUser());
        return controller.searchImagesInContainer(uri, searchQuery, sortCriterion, getElementsPerPage(), getOffset());
    }

    public String getImageBaseUrl()
    {
        if (collection == null)
        {
            return "";
        }
        return navigation.getApplicationUri() + collection.getId().getPath();
    }

    @Override
    public String initFacets() throws Exception
    {
        searchQuery = URLQueryTransformer.parseStringQuery(getQuery());
        setFacets(new FacetsBean(collection, searchQuery));
        return "pretty";
    }

    public String getBackUrl()
    {
        return navigation.getImagesUrl() + "/collection" + "/" + this.id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    public String release()
    {
        CollectionController cc = new CollectionController(sb.getUser());
        try
        {
            cc.release(collection, sb.getUser());
            BeanHelper.info(sb.getMessage("success_collection_release"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sb.getMessage("error_collection_release"));
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        return "pretty:";
    }

    public String delete()
    {
        CollectionController cc = new CollectionController(sb.getUser());
        try
        {
            cc.delete(collection, sb.getUser());
            BeanHelper.info(sb.getMessage("success_collection_delete"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sb.getMessage("success_collection_delete"));
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        return "pretty:collections";
    }

    public String withdraw() throws Exception
    {
        CollectionController cc = new CollectionController(sb.getUser());
        try
        {
            cc.withdraw(collection);
            BeanHelper.info(sb.getMessage("success_collection_withdraw"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sb.getMessage("error_collection_withdraw"));
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        return "pretty:";
    }

    public boolean isEditable()
    {
        Security security = new Security();
        return security.check(OperationsType.UPDATE, sb.getUser(), collection);
    }

    /**
     * Check that at leat one image is editable
     */
    // public boolean isImageEditable()
    // {
    // for (ThumbnailBean tb : getCurrentPartList())
    // {
    // if (tb.isEditable())
    // {
    // return true;
    // }
    // }
    // return false;
    // }
    //
    // public boolean isImageDeletable()
    // {
    // for (ThumbnailBean tb : getCurrentPartList())
    // {
    // if (tb.isDeletable())
    // {
    // return true;
    // }
    // }
    // return false;
    // }
    public boolean isVisible()
    {
        Security security = new Security();
        return security.check(OperationsType.READ, sb.getUser(), collection);
    }

    public boolean isDeletable()
    {
        Security security = new Security();
        return security.check(OperationsType.DELETE, sb.getUser(), collection);
    }
}
