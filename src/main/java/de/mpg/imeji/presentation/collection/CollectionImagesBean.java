/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
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
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.AuthorizationBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.facet.FacetsBean;
import de.mpg.imeji.presentation.image.ImagesBean;
import de.mpg.imeji.presentation.search.URLQueryTransformer;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * {@link ImagesBean} to browse {@link Item} of a {@link CollectionImeji}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CollectionImagesBean extends ImagesBean
{
    private String id = null;
    private URI uri;
    private SessionBean sb = null;
    private CollectionImeji collection;
    private Navigation navigation;
    private SearchQuery searchQuery = new SearchQuery();

    /**
     * Initialize the bean
     */
    public CollectionImagesBean()
    {
        super();
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        this.navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }

    /**
     * Initialize the elements of the page
     * 
     * @return
     */
    @Override
    public String getInitPage()
    {
        uri = ObjectHelper.getURI(CollectionImeji.class, id);
        collection = ObjectLoader.loadCollectionLazy(uri, sb.getUser());
        ((AuthorizationBean)BeanHelper.getSessionBean(AuthorizationBean.class)).init(collection);
        browseInit();
        browseContext = getNavigationString() + id;
        return "";
    }

    @Override
    public SearchResult search(SearchQuery searchQuery, SortCriterion sortCriterion)
    {
        ItemController controller = new ItemController(sb.getUser());
        return controller.search(uri, searchQuery, sortCriterion, null);
    }

    @Override
    public void initMenus()
    {
        List<SelectItem> sortMenu = new ArrayList<SelectItem>();
        sortMenu.add(new SelectItem(null, "--"));
        sortMenu.add(new SelectItem(SearchIndex.names.created, sb.getLabel(SearchIndex.names.created.name())));
        sortMenu.add(new SelectItem(SearchIndex.names.modified, sb.getLabel(SearchIndex.names.modified.name())));
        setSortMenu(sortMenu);
    }

    @Override
    public String getNavigationString()
    {
        return "pretty:collectionBrowse";
    }

    @Override
    public String initFacets() throws Exception
    {
        searchQuery = URLQueryTransformer.parseStringQuery(getQuery());
        setFacets(new FacetsBean(collection, searchQuery));
        return "";
    }

    /**
     * return the url of the collection
     */
    @Override
    public String getImageBaseUrl()
    {
        if (collection == null)
        {
            return "";
        }
        return navigation.getApplicationUrl() + "collection/" + this.id;
    }

    /**
     * return the url of the collection
     */
    @Override
    public String getBackUrl()
    {
        return navigation.getBrowseUrl() + "/collection" + "/" + this.id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
        // @Ye set session value to share with CollectionImageBean, another way is via injection
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("CollectionImagesBean.id", id);
    }

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    /**
     * Release the current {@link CollectionImeji}
     * 
     * @return
     */
    public String release()
    {
        CollectionController cc = new CollectionController();
        try
        {
            cc.release(collection, sb.getUser());
            BeanHelper.info(sb.getMessage("success_collection_release"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sb.getMessage("error_collection_release"));
            BeanHelper.error(e.getMessage());
            logger.error("Error releasing collection", e);
        }
        return "pretty:";
    }

    /**
     * Delete the current {@link CollectionImeji}
     * 
     * @return
     */
    public String delete()
    {
        CollectionController cc = new CollectionController();
        try
        {
            cc.delete(collection, sb.getUser());
            BeanHelper.info(sb.getMessage("success_collection_delete"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sb.getMessage("success_collection_delete"));
            BeanHelper.error(e.getMessage());
            logger.error("Error deleting collection", e);
        }
        return "pretty:collections";
    }

    /**
     * Withdraw the current {@link CollectionImeji}
     * 
     * @return
     * @throws Exception
     */
    public String withdraw() throws Exception
    {
        CollectionController cc = new CollectionController();
        try
        {
            cc.withdraw(collection, sb.getUser());
            BeanHelper.info(sb.getMessage("success_collection_withdraw"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sb.getMessage("error_collection_withdraw"));
            BeanHelper.error(e.getMessage());
            logger.error("Error discarding collection", e);
        }
        return "pretty:";
    }

    /**
     * True if the {@link CollectionImeji} is updatable for this {@link User}
     */
    @Override
    public boolean isEditable()
    {
        Security security = new Security();
        return security.check(OperationsType.UPDATE, sb.getUser(), collection);
    }

    /**
     * Check that at least one image is editable and if the profile is not empty
     */
    @Override
    public boolean isImageEditable()
    {
        return super.isImageDeletable()
                && ObjectCachedLoader.loadProfile(collection.getProfile()).getStatements().size() > 0;
    }

    @Override
    public boolean isVisible()
    {
        Security security = new Security();
        return security.check(OperationsType.READ, sb.getUser(), collection);
    }

    @Override
    public boolean isDeletable()
    {
        Security security = new Security();
        return security.check(OperationsType.DELETE, sb.getUser(), collection);
    }
}
