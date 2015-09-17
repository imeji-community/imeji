/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import static de.mpg.imeji.logic.notification.CommonMessages.getSuccessCollectionDeleteMessage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.SearchQueryParser;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.model.SearchIndex;
import de.mpg.imeji.logic.search.model.SearchQuery;
import de.mpg.imeji.logic.search.model.SortCriterion;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.facet.FacetsBean;
import de.mpg.imeji.presentation.image.ItemsBean;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * {@link ItemsBean} to browse {@link Item} of a {@link CollectionImeji}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CollectionItemsBean extends ItemsBean {
  private String id = null;
  private URI uri;
  private SessionBean sb = null;
  private CollectionImeji collection;
  private MetadataProfile profile;
  private Navigation navigation;
  private SearchQuery searchQuery = new SearchQuery();

  /**
   * Initialize the bean
   */
  public CollectionItemsBean() {
    super();
    sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    this.navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
  }

  /**
   * Initialize the elements of the page
   * 
   * @return
   * @throws Exception
   */
  @Override
  public String getInitPage() throws Exception {
    uri = ObjectHelper.getURI(CollectionImeji.class, id);
    collection = ObjectLoader.loadCollectionLazy(uri, sb.getUser());
    this.profile = ObjectLoader.loadProfile(collection.getProfile(), sb.getUser());
    // Initialize the metadata labels
    ((MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class)).init(profile);
    // browse context must be initialized before browseinit(), since the browseinit() will check if
    // the selected
    // items must be removed
    browseContext = getNavigationString() + id;
    browseInit();
    return "";
  }

  @Override
  public SearchResult search(SearchQuery searchQuery, SortCriterion sortCriterion, int offset,
      int limit) {
    ItemController controller = new ItemController();
    return controller.search(uri, searchQuery, sortCriterion, sb.getUser(), null, limit, offset);
  }

  @Override
  public void initMenus() {
    List<SelectItem> sortMenu = new ArrayList<SelectItem>();
    sortMenu.add(new SelectItem(null, "--"));
    sortMenu.add(new SelectItem(SearchIndex.SearchFields.created, sb
        .getLabel("sort_img_date_created")));
    sortMenu.add(new SelectItem(SearchIndex.SearchFields.modified, sb.getLabel("sort_date_mod")));
    sortMenu
        .add(new SelectItem(SearchIndex.SearchFields.filename, sb.getLabel("sort_img_filename")));
    setSortMenu(sortMenu);
  }

  @Override
  public String getNavigationString() {
    return sb.getPrettySpacePage("pretty:collectionBrowse");
  }

  @Override
  public void initFacets() {
    try {
      searchQuery = SearchQueryParser.parseStringQuery(getQuery());
      setFacets(new FacetsBean(collection, searchQuery));
      ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
      executor.submit(getFacets());
      executor.shutdown();
    } catch (Exception e) {
      logger.error("Error initialising the facets", e);
    }
  }

  /**
   * return the url of the collection
   */
  @Override
  public String getImageBaseUrl() {
    if (collection == null) {
      return "";
    }
    return navigation.getApplicationSpaceUrl() + "collection/" + this.id + "/";
  }

  /**
   * return the url of the collection
   */
  @Override
  public String getBackUrl() {
    return navigation.getBrowseUrl() + "/collection" + "/" + this.id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
    // @Ye set session value to share with CollectionItemsBean, another way is via injection
    FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
        .put("CollectionItemsBean.id", id);
  }

  public void setCollection(CollectionImeji collection) {
    this.collection = collection;
  }

  public CollectionImeji getCollection() {
    return collection;
  }

  /**
   * Release the current {@link CollectionImeji}
   * 
   * @return
   */
  public String release() {
    CollectionController cc = new CollectionController();
    try {
      cc.release(collection, sb.getUser());
      BeanHelper.info(sb.getMessage("success_collection_release"));
    } catch (Exception e) {
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
  public String delete() {
    CollectionController cc = new CollectionController();
    try {
      cc.delete(collection, sb.getUser());
      BeanHelper.info(getSuccessCollectionDeleteMessage(collection.getMetadata().getTitle(), sb));
    } catch (Exception e) {
      BeanHelper.error(getSuccessCollectionDeleteMessage(collection.getMetadata().getTitle(), sb));
      BeanHelper.error(e.getMessage());
      logger.error("Error deleting collection", e);
    }
    return sb.getPrettySpacePage("pretty:collections");
  }

  /**
   * Withdraw the current {@link CollectionImeji}
   * 
   * @return
   * @throws Exception
   */
  public String withdraw() throws Exception {
    CollectionController cc = new CollectionController();
    try {
      collection.setDiscardComment(getDiscardComment());
      cc.withdraw(collection, sb.getUser());
      BeanHelper.info(sb.getMessage("success_collection_withdraw"));
    } catch (Exception e) {
      BeanHelper.error(sb.getMessage("error_collection_withdraw"));
      BeanHelper.error(e.getMessage());
      logger.error("Error discarding collection", e);
    }
    return "pretty:";
  }

  /**
   * @return the profile
   */
  public MetadataProfile getProfile() {
    return profile;
  }

  /**
   * @param profile the profile to set
   */
  public void setProfile(MetadataProfile profile) {
    this.profile = profile;
  }

  @Override
  public String getType() {
    return PAGINATOR_TYPE.COLLECTION_ITEMS.name();

  }
}
