/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import de.mpg.imeji.exceptions.AuthenticationError;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotAllowedError;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.ImejiTriple;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchObjectTypes;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchFactory.SEARCH_IMPLEMENTATIONS;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.jenasearch.JenaCustomQueries;
import de.mpg.imeji.logic.search.model.SearchQuery;
import de.mpg.imeji.logic.search.model.SortCriterion;
import de.mpg.imeji.logic.validation.Validator.Method;
import de.mpg.imeji.logic.validation.impl.CollectionValidator;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.imeji.presentation.beans.ConfigurationBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.j2j.helper.J2JHelper;

/**
 * CRUD controller for {@link CollectionImeji}, plus search mehtods related to
 * {@link CollectionImeji}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CollectionController extends ImejiController {
  private static ReaderFacade reader = new ReaderFacade(Imeji.collectionModel);
  private static WriterFacade writer = new WriterFacade(Imeji.collectionModel);
  private static Logger logger = Logger.getLogger(CollectionController.class);
  private Search search =
      SearchFactory.create(SearchObjectTypes.COLLECTION, SEARCH_IMPLEMENTATIONS.ELASTIC);

  public static enum MetadataProfileCreationMethod {
    COPY, REFERENCE, NEW;
  }

  /**
   * Default constructor
   */
  public CollectionController() {
    super();
  }

  /**
   * Creates a new collection. - Add a unique id - Write user properties
   * 
   * @param c
   * @param p
   * @param user
   * @param method
   * @return
   * @throws ImejiException
   */
  public URI create(CollectionImeji c, MetadataProfile p, User user,
      MetadataProfileCreationMethod method, String spaceId) throws ImejiException {
    // First validate the collection, to avoid to create a zombie profile...
    CollectionValidator validator = new CollectionValidator(Method.CREATE);
    validator.validate(c);
    ProfileController pc = new ProfileController();
    String metadataProfileName = " (Metadata profile)";
    if (p == null || method == MetadataProfileCreationMethod.NEW) {
      p = new MetadataProfile();
      p.setDescription(c.getMetadata().getDescription());
      p.setTitle(c.getMetadata().getTitle() + metadataProfileName);
      p = pc.create(p, user);
    } else if (p != null && method == MetadataProfileCreationMethod.COPY) {
      p.setTitle(c.getMetadata().getTitle() + metadataProfileName);
      p = pc.create(p.clone(), user);
    } else if (p != null && method == MetadataProfileCreationMethod.REFERENCE
        && !AuthUtil.staticAuth().administrate(user, p) && p.getStatus() != Status.RELEASED) {
      // User must be allowed to release the profile: otherwise, he might not be allowed to release
      // his collection (if profile is already released, no problems)
      throw new NotAllowedError("Not allowed to reference this profile");
    }
    c.setProfile(p.getId());
    writeCreateProperties(c, user);
    c.setProfile(p.getId());
    writer.create(WriterFacade.toList(c), p, user);
    // Prepare grants
    ShareController shareController = new ShareController();
    user = shareController.shareToCreator(user, c.getId().toString());
    // check the space
    // Just read SessionBean for SpaceId
    if (!isNullOrEmpty(spaceId)) {
      SpaceController sp = new SpaceController();
      sp.addCollection(spaceId, c.getId().toString(), user);
    }
    return c.getId();
  }

  /**
   * Retrieve a complete {@link CollectionImeji} (inclusive its {@link Item}: slow for huge
   * {@link CollectionImeji})
   * 
   * @param uri
   * @param user
   * @return
   * @throws ImejiException
   */
  public CollectionImeji retrieve(URI uri, User user) throws ImejiException {
    CollectionImeji c = (CollectionImeji) reader.read(uri.toString(), user, new CollectionImeji());
    return c;
  }

  /**
   * Retrieve the {@link CollectionImeji} without its {@link Item}
   * 
   * @param uri
   * @param user
   * @return
   * @throws ImejiException
   */
  public CollectionImeji retrieveLazy(URI uri, User user) throws ImejiException {
    return (CollectionImeji) reader.readLazy(uri.toString(), user, new CollectionImeji());
  }

  /**
   * Load {@link CollectionImeji} defined in a {@link List} of uris. Don't load the {@link Item}
   * contained in the {@link CollectionImeji}
   * 
   * @param uris
   * @param limit
   * @param offset
   * @return
   * @throws ImejiException
   */
  public Collection<CollectionImeji> retrieveBatchLazy(List<String> uris, int limit, int offset,
      User user) throws ImejiException {
    List<CollectionImeji> cols = new ArrayList<CollectionImeji>();
    List<String> retrieveUris;
    if (limit < 0) {
      retrieveUris = uris;
    } else {
      retrieveUris = uris.size() > 0 && limit > 0
          ? uris.subList(offset, getMin(offset + limit, uris.size())) : new ArrayList<String>();
    }
    for (String s : retrieveUris) {
      cols.add((CollectionImeji) J2JHelper.setId(new CollectionImeji(), URI.create(s)));
    }
    reader.readLazy(J2JHelper.cast2ObjectList(cols), user);
    return cols;

  }

  /**
   * Retrieve all {@link CollectionImeji} (all status, all users) in imeji
   * 
   * @return
   * @throws ImejiException
   */
  public Collection<CollectionImeji> retrieveAll(User user) throws ImejiException {
    List<String> uris =
        ImejiSPARQL.exec(JenaCustomQueries.selectCollectionAll(), Imeji.collectionModel);
    return retrieveBatchLazy(uris, -1, 0, user);
  }

  /**
   * Update a {@link CollectionImeji} (inclusive its {@link Item}: slow for huge
   * {@link CollectionImeji})
   * 
   * @param ic
   * @param user
   * @throws ImejiException
   */
  public CollectionImeji update(CollectionImeji ic, User user) throws ImejiException {
    writeUpdateProperties(ic, user);
    writer.update(WriterFacade.toList(ic), null, user, true);
    return retrieve(ic.getId(), user);
  }

  /**
   * Update a {@link CollectionImeji} (inclusive its {@link Item}: slow for huge
   * {@link CollectionImeji}) TODO remove if possible
   * 
   * @param ic
   * @param user
   * @throws ImejiException
   */
  public CollectionImeji updateWithProfile(CollectionImeji ic, MetadataProfile mp, User user,
      MetadataProfileCreationMethod method) throws ImejiException {
    updateCollectionProfile(ic, mp, user, method);
    writeUpdateProperties(ic, user);
    return update(ic, user);
  }

  /**
   * Update a {@link CollectionImeji} (with its Logo)
   * 
   * @param ic
   * @param hasgrant
   * @throws ImejiException
   */
  public void updateLogo(CollectionImeji ic, File f, User u)
      throws ImejiException, IOException, URISyntaxException {

    ic = (CollectionImeji) updateFile(ic, f, u);
    if (f != null && f.exists()) {

      // Update the collection as a patch only with collection Logo Triple
      List<ImejiTriple> triples =
          getContainerLogoTriples(ic.getId().toString(), ic, ic.getLogoUrl().toString());
      patch(triples, u, true);

    }
  }


  /**
   * Update the {@link CollectionImeji} but not iths {@link Item}
   * 
   * @param ic
   * @param user
   * @throws ImejiException
   */
  public CollectionImeji updateLazy(CollectionImeji ic, User user) throws ImejiException {
    writeUpdateProperties(ic, user);
    writer.updateLazy(WriterFacade.toList(ic), null, user);
    return retrieveLazy(ic.getId(), user);
  }

  // TODO Move this method to profilecontroller
  public CollectionImeji updateCollectionProfile(CollectionImeji ic, MetadataProfile mp, User user,
      MetadataProfileCreationMethod method) throws ImejiException {
    if (mp == null) {
      return ic;
    }
    ProfileController pc = new ProfileController();
    if (method.equals(MetadataProfileCreationMethod.REFERENCE)) {
      // if it is a reference, only change the reference to the new
      // metadata profile, and do not forget to delete old metadata
      // profile
      ic.setProfile(mp.getId());
      // Update the collection with the new profile
      ic = update(ic, user);
      // here update of the newly referenced profile for all Items (if
      // there are any, patch is applied)
      updateCollectionItemsProfile(ic, mp.getId(), user);
    } else {
      // Clone the profile
      MetadataProfile copy = mp.clone();
      copy.setTitle(mp.getTitle() + " - COPY");
      // Create the cloned profile
      copy = pc.create(copy, user);
      // Set the cloned profile to the collection
      ic.setProfile(copy.getId());
      // Update the collection with the cloned profile
      ic = update(ic, user);
      // here update of the newly referenced profile for all Items (if
      // there are any, patch is applied)
      updateCollectionItemsProfile(ic, copy.getId(), user);
    }
    return ic;
  }

  /**
   * Update the {@link CollectionImeji} but not iths {@link Item} TODO : remove if possible
   * 
   * @param ic
   * @param user
   * @throws ImejiException
   */
  public CollectionImeji updateLazyWithProfile(CollectionImeji ic, MetadataProfile mp, User user,
      MetadataProfileCreationMethod method) throws ImejiException {
    updateCollectionProfile(ic, mp, user, method);
    writeUpdateProperties(ic, user);
    return updateLazy(ic, user);
  }


  /**
   * Delete a {@link CollectionImeji} and all its {@link Item}
   * 
   * @param collection
   * @param user
   * @throws ImejiException
   */
  public void delete(CollectionImeji collection, User user) throws ImejiException {
    ItemController itemController = new ItemController();
    List<String> itemUris =
        itemController.search(collection.getId(), null, null, user, null, -1, 0).getResults();
    if (hasImageLocked(itemUris, user)) {
      throw new RuntimeException(((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
          .getMessage("collection_locked"));
    } else {
      if (collection.getStatus() != Status.PENDING && !user.isAdmin()) {
        throw new UnprocessableError("Collection is not pending and can not be deleted!");
      }
      // Delete images
      List<Item> items = (List<Item>) itemController.retrieveBatch(itemUris, -1, 0, user);

      itemController.delete(items, user);

      // Delete profile
      ProfileController pc = new ProfileController();
      try {

        MetadataProfile collectionMdp = pc.retrieve(collection.getProfile(), user);
        if ((pc.isReferencedByOtherResources(collectionMdp.getId().toString(),
            collection.getId().toString()))
            || (!AuthUtil.staticAuth().delete(user, collectionMdp.getId().toString()))
            || collectionMdp.getDefault()) {
          logger.info(
              "Metadata profile related to this collection is referenced elsewhere, or user does not have permission to delete this profile."
                  + "Profile <" + collectionMdp.getId().toString() + "> will not be deleted!");
        } else {
          logger.info("Metadata profile <" + collectionMdp.getId().toString()
              + "> is not referenced elsewhere, will be deleted!");
          pc.delete(collectionMdp, user, collection.getId().toString());
        }
      } catch (NotFoundException e) {
        logger.info("Collection profile does not exist, could not be deleted.");
      }

      writer.delete(WriterFacade.toList(collection), user);
    }
  }

  /**
   * Release a {@link CollectionImeji} and all its {@link Item}
   * 
   * @param collection
   * @param user
   * @throws ImejiException
   */
  public void release(CollectionImeji collection, User user) throws ImejiException {
    ItemController itemController = new ItemController();

    if (user == null) {
      throw new AuthenticationError("User must be signed-in");
    }

    if (collection == null) {
      throw new NotFoundException("collection object does not exists");
    }

    List<String> itemUris =
        itemController.search(collection.getId(), null, null, user, null, -1, 0).getResults();


    if (hasImageLocked(itemUris, user)) {
      throw new UnprocessableError(((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
          .getMessage("collection_locked"));
    } else if (itemUris.isEmpty()) {
      throw new UnprocessableError("An empty collection can not be released!");
    } else if (collection.getStatus().equals(Status.RELEASED)) {
      throw new UnprocessableError("The status of collection is " + collection.getStatus()
          + " and can not be released again!");
    } else {
      writeReleaseProperty(collection, user);
      List<Item> items = (List<Item>) itemController.retrieveBatch(itemUris, -1, 0, user);
      itemController.release(items, user);
      update(collection, user);
      if (AuthUtil.staticAuth().administrate(user, collection.getProfile().toString())) {
        ProfileController pc = new ProfileController();
        pc.release(pc.retrieve(collection.getProfile(), user), user);
      }
    }
  }

  public void createDOI(CollectionImeji coll, User user) throws ImejiException {
    if (user == null) {
      throw new AuthenticationError("User must be signed-in");
    }

    if (coll == null) {
      throw new NotFoundException("Collection does not exists");
    }

    if (!Status.RELEASED.equals(coll.getStatus())) {
      throw new ImejiException("Collection has to be released to create a DOI");
    }

    DoiController doicontr = new DoiController();

    String doiServiceUrl = ConfigurationBean.getDoiServiceUrlStatic();
    String doiUser = ConfigurationBean.getDoiUserStatic();
    String doiPassword = ConfigurationBean.getDoiPasswordStatic();

    String doi = doicontr.getNewDoi(coll, doiServiceUrl, doiUser, doiPassword);
    coll.setDoi(doi);
    update(coll, user);
  }

  public void createDOIManually(String doi, CollectionImeji collection, User user)
      throws ImejiException {
    if (user == null) {
      throw new AuthenticationError("User must be signed-in");
    }

    if (collection == null) {
      throw new NotFoundException("Collection does not exists");
    }

    if (!Status.RELEASED.equals(collection.getStatus())) {
      throw new ImejiException("Collection has to be released to create a DOI");
    }

    collection.setDoi(doi);
    update(collection, user);

  }

  /**
   * Withdraw a {@link CollectionImeji} and all its {@link Item}
   * 
   * @param coll
   * @throws ImejiException
   */
  public void withdraw(CollectionImeji coll, User user) throws ImejiException {
    ItemController itemController = new ItemController();

    if (user == null) {
      throw new AuthenticationError("User must be signed-in");
    }

    if (coll == null) {
      throw new NotFoundException("Collection does not exists");
    }

    List<String> itemUris =
        itemController.search(coll.getId(), null, null, user, null, -1, 0).getResults();
    if (hasImageLocked(itemUris, user)) {
      throw new UnprocessableError(((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
          .getMessage("collection_locked"));
    } else if (!Status.RELEASED.equals(coll.getStatus())) {
      throw new UnprocessableError("Withdraw collection: Collection must be released");
    } else {
      List<Item> items = (List<Item>) itemController.retrieveBatch(itemUris, -1, 0, user);
      itemController.withdraw(items, coll.getDiscardComment(), user);
      writeWithdrawProperties(coll, null);
      update(coll, user);
      if (!coll.getProfile().equals(Imeji.defaultMetadataProfile.getId())
          && AuthUtil.staticAuth().administrate(user, coll.getProfile().toString())) {
        // Withdraw profile
        ProfileController pc = new ProfileController();
        if (!pc.isReferencedByOtherResources(coll.getProfile().toString(), coll.getId().toString()))
          pc.withdraw(pc.retrieve(coll.getProfile(), user), user);
      }
    }
  }

  /**
   * Search for {@link Collection}
   * 
   * @param searchQuery
   * @param sortCri
   * @param limit
   * @param offset
   * @return
   */
  public SearchResult search(SearchQuery searchQuery, SortCriterion sortCri, int limit, int offset,
      User user, String spaceId) {
    // Search search = SearchFactory.create(SearchObjectTypes.COLLECTION,
    // SEARCH_IMPLEMENTATIONS.JENA);
    return search.search(searchQuery, sortCri, user, null, spaceId, 0, -1);
  }

  /**
   * Search and Retrieve Collections
   * 
   * @param searchQuery
   * @param sortCri
   * @param limit
   * @param offset
   * @param user
   * @param spaceId
   * @return
   * @throws ImejiException
   */
  public List<CollectionImeji> searchAndRetrieve(SearchQuery searchQuery, SortCriterion sortCri,
      User user, String spaceId, int offset, int size) throws ImejiException {
    SearchResult result = search.search(searchQuery, sortCri, user, null, spaceId, offset, size);
    return (List<CollectionImeji>) retrieveBatchLazy(result.getResults(), -1, 0, user);
  }

  public MetadataProfileCreationMethod getProfileCreationMethod(String method) {
    if ("reference".equalsIgnoreCase(method)) {
      return MetadataProfileCreationMethod.REFERENCE;
    } else if ("copy".equalsIgnoreCase(method)) {
      return MetadataProfileCreationMethod.COPY;
    } else {
      return MetadataProfileCreationMethod.NEW;
    }
  }

  private void updateCollectionItemsProfile(CollectionImeji ic, URI newProfileUri, User user)
      throws ImejiException {
    ItemController itemController = new ItemController();
    List<String> itemUris =
        itemController.search(ic.getId(), null, null, user, null, -1, 0).getResults();

    List<Item> items = (List<Item>) itemController.retrieveBatch(itemUris, -1, 0, user);
    itemController.updateItemsProfile(items, user, newProfileUri.toString());
  }

  // TODO Remove and replace with normal search method
  public List<CollectionImeji> retrieveCollectionsNotInSpace(final User u) {
    return Lists.transform(
        ImejiSPARQL.exec(JenaCustomQueries.selectCollectionsNotInSpace(), Imeji.collectionModel),
        new Function<String, CollectionImeji>() {
          @Override
          public CollectionImeji apply(String id) {
            try {
              return retrieve(URI.create(id), u);
            } catch (ImejiException e) {
              logger.info("Cannot retrieve collection: " + id);
            }
            return null;
          }
        });
  }

  // TODO Remove and replace with normal search method
  public List<String> retrieveAllCollectionIdsInSpace(URI spaceId) {
    return ImejiSPARQL.exec(JenaCustomQueries.selectCollectionImejiOfSpace(spaceId.toString()),
        Imeji.collectionModel);
  }

  // TODO Remove and replace with method checking the cache, related to ElasticIndexer.java (see
  // comment there as well)
  public String retrieveSpaceOfCollection(URI collectionId) {
    List<String> collectionSpace =
        ImejiSPARQL.exec(JenaCustomQueries.selectSpaceOfCollection(collectionId), null);
    if (collectionSpace.isEmpty()) {
      return null;
    } else {
      return collectionSpace.get(0);
    }
  }

  /**
   * Patch a collection. !!! Use with Care !!! TODO make private
   * 
   * @param triples
   * @param user
   * @throws ImejiException
   */
  public void patch(List<ImejiTriple> triples, User user, boolean checkSecurity)
      throws ImejiException {
    writer.patch(triples, user, checkSecurity);
  }

}
