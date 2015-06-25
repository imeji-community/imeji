/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

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
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.ImejiTriple;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.search.query.URLQueryTransformer;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.validation.Validator.Method;
import de.mpg.imeji.logic.validation.ValidatorFactory;
import de.mpg.imeji.logic.validation.impl.CollectionValidator;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.writer.WriterFacade;
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
	private static final ReaderFacade reader = new ReaderFacade(
			Imeji.collectionModel);
	private static final WriterFacade writer = new WriterFacade(
			Imeji.collectionModel);
	private static Logger logger = Logger.getLogger(CollectionController.class);

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
	 *            : if the profile is null, then create an empty one
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public URI create(CollectionImeji c, MetadataProfile p, User user,
			String spaceId) throws ImejiException {
		return create(c, p, user, MetadataProfileCreationMethod.COPY, spaceId);
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
			MetadataProfileCreationMethod method, String spaceId)
			throws ImejiException {
		// First validate the collection, to avoid to create a zombie profile...
		CollectionValidator validator = new CollectionValidator(Method.CREATE);
		validator.validate(c);
		ProfileController pc = new ProfileController();
		String metadataProfileName = " (Metadata profile)";
		if (p == null || method.equals(MetadataProfileCreationMethod.NEW)) {
			p = new MetadataProfile();
			p.setDescription(c.getMetadata().getDescription());
			p.setTitle(c.getMetadata().getTitle() + metadataProfileName);
			p = pc.create(p, user);
		} else if (p != null
				&& !method.equals(MetadataProfileCreationMethod.REFERENCE)) {
			if (method.equals(MetadataProfileCreationMethod.COPY)) {
				p.setTitle(c.getMetadata().getTitle() + metadataProfileName);
			}
			p = pc.create(p.clone(), user);
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
			sp.addCollection(spaceId, c.getIdString(), user);
		}
		return c.getId();
	}

	/**
	 * Retrieve a complete {@link CollectionImeji} (inclusive its {@link Item}:
	 * slow for huge {@link CollectionImeji})
	 * 
	 * @param uri
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public CollectionImeji retrieve(URI uri, User user) throws ImejiException {
		return (CollectionImeji) reader.read(uri.toString(), user,
				new CollectionImeji());
	}

	/**
	 * Retrieve a complete {@link CollectionImeji} (inclusive its {@link Item}:
	 * slow for huge {@link CollectionImeji})
	 *
	 * @param id
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public CollectionImeji retrieve(String id, User user) throws ImejiException {
		return retrieve(ObjectHelper.getURI(CollectionImeji.class, id), user);
	}

	/**
	 * Retrieve all items of the collection
	 *
	 * @param id
	 * @param user
	 * @param q
	 * @return
	 * @throws ImejiException
	 */
	// TODO
	public List<Item> retrieveItems(String id, User user, String q)
			throws ImejiException {
		ItemController ic = new ItemController();
		List<Item> itemList;
		try {
			List<String> results = ic
					.search(ObjectHelper.getURI(CollectionImeji.class, id),
							!isNullOrEmptyTrim(q) ? URLQueryTransformer
									.parseStringQuery(q) : null, null, null,
							user, null).getResults();
			itemList = (List<Item>) ic.retrieve(results,
					getMin(results.size(), 500), 0, user);
		} catch (Exception e) {
			throw new UnprocessableError("Cannot retrieve items:", e);

		}
		return itemList;
	}

	/**
	 * Retrieve all collections user can see
	 *
	 * @param user
	 * @param q
	 * @return
	 * @throws ImejiException
	 */
	public List<CollectionImeji> retrieveCollections(User user, String q,
			String spaceId) throws ImejiException {

		List<CollectionImeji> cList = new ArrayList<CollectionImeji>();
		try {
			List<String> results = search(
					!isNullOrEmptyTrim(q) ? URLQueryTransformer.parseStringQuery(q)
							: null, null, 500, 0, user, spaceId).getResults();
			cList = (List<CollectionImeji>) retrieveLazy(results,
					getMin(results.size(), 500), 0, user);
		} catch (Exception e) {
			throw new UnprocessableError("Cannot retrieve collections:", e);
		}
		return cList;
	}

	/**
	 * Retrieve the {@link CollectionImeji} without its {@link Item}
	 * 
	 * @param uri
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public CollectionImeji retrieveLazy(URI uri, User user)
			throws ImejiException {
		return (CollectionImeji) reader.readLazy(uri.toString(), user,
				new CollectionImeji());
	}

	/**
	 * Load {@link CollectionImeji} defined in a {@link List} of uris. Don't
	 * load the {@link Item} contained in the {@link CollectionImeji}
	 * 
	 * @param uris
	 * @param limit
	 * @param offset
	 * @return
	 * @throws ImejiException
	 */
	public Collection<CollectionImeji> retrieveLazy(List<String> uris,
			int limit, int offset, User user) throws ImejiException {

		List<CollectionImeji> cols = new ArrayList<CollectionImeji>();

		List<String> retrieveUris;
		if (limit < 0) {
			retrieveUris = uris;
		} else {
			retrieveUris = uris.size() > 0 && limit > 0 ? uris.subList(offset,
					getMin(offset + limit, uris.size()))
					: new ArrayList<String>();
		}

		for (String s : retrieveUris) {

			cols.add((CollectionImeji) J2JHelper.setId(new CollectionImeji(),
					URI.create(s)));
		}

		try {
			reader.readLazy(J2JHelper.cast2ObjectList(cols), user);
			return cols;
		} catch (ImejiException e) {
			logger.error("Error loading collections: " + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Update a {@link CollectionImeji} (inclusive its {@link Item}: slow for
	 * huge {@link CollectionImeji})
	 * 
	 * @param ic
	 * @param user
	 * @throws ImejiException
	 */
	public CollectionImeji update(CollectionImeji ic, User user)
			throws ImejiException {
		writeUpdateProperties(ic, user);
		writer.update(WriterFacade.toList(ic), null, user, true);
		return retrieve(ic.getId(), user);
	}

	/**
	 * Update a {@link CollectionImeji} (inclusive its {@link Item}: slow for
	 * huge {@link CollectionImeji})
	 * 
	 * @param ic
	 * @param user
	 * @throws ImejiException
	 */
	public CollectionImeji updateWithProfile(CollectionImeji ic,
			MetadataProfile mp, User user, MetadataProfileCreationMethod method)
			throws ImejiException {
		updateCollectionProfile(ic, mp, user, method);
		writeUpdateProperties(ic, user);
		writer.update(WriterFacade.toList(ic), null, user, true);
		return retrieve(ic.getId(), user);
	}

	/**
	 * Update a {@link CollectionImeji} (with its Logo)
	 * 
	 * @param ic
	 * @param user
	 * @throws ImejiException
	 */
	public void updateCollectionLogo(CollectionImeji ic, File f, User u)
			throws ImejiException, IOException, URISyntaxException {

		ic = (CollectionImeji) updateFile(ic, f, u);
		if (f != null && f.exists()) {

			// Update the collection as a patch only with collection Logo Triple
			List<ImejiTriple> triples = getContainerLogoTriples(ic.getId()
					.toString(), ic, ic.getLogoUrl().toString());
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
	public CollectionImeji updateLazy(CollectionImeji ic, User user)
			throws ImejiException {
		writeUpdateProperties(ic, user);
		writer.updateLazy(WriterFacade.toList(ic), null, user);
		return retrieveLazy(ic.getId(), user);
	}

	public CollectionImeji updateCollectionProfile(CollectionImeji ic,
			MetadataProfile mp, User user, MetadataProfileCreationMethod method)
			throws ImejiException {
		if (mp == null)
			return ic;
		// check if there had been change in the metadata profile of this
		// collection
		// update of the profile will be performed only when the metadata
		// profile is different from the metadata profile of the collection
		// and only if the old profile does not have any statements
		ProfileController pc = new ProfileController();
		MetadataProfile originalMP = pc.retrieve(ic.getProfile(), user);
		if (!originalMP.getId().equals(mp.getId())
				&& originalMP.getStatements().size() == 0) {
			if (method.equals(MetadataProfileCreationMethod.REFERENCE)) {
				// if it is a reference, only change the reference to the new
				// metadata profile, and do not forget to delete old metadata
				// profile
				ic.setProfile(mp.getId());
				// delete the old profile of the Collection (procedure checks if
				// there are no other relations, should not be if profile is
				// empty)
				pc.delete(originalMP, user, ic.getId().toString());
				// here update of the newly referenced profile for all Items (if
				// there are any, patch is applied)
				updateCollectionItemsProfile(ic, mp.getId(), user);
			} else {
				// copy all statements from the template profile to the original
				// metadata profile
				MetadataProfile newMP = mp.clone();
				// Title format as CollectionName (Metadata profile) (copy of
				// <copied metadata profile name>))
				// newMP.setTitle(ic.getMetadata().getTitle()+"(Metadata profile)"+newMP.getTitle());
				originalMP.setStatements(newMP.getStatements());
				pc.update(originalMP, user);
			}
		}
		return ic;
	}

	/**
	 * Update the {@link CollectionImeji} but not iths {@link Item}
	 * 
	 * @param ic
	 * @param user
	 * @throws ImejiException
	 */
	public CollectionImeji updateLazyWithProfile(CollectionImeji ic,
			MetadataProfile mp, User user, MetadataProfileCreationMethod method)
			throws ImejiException {
		updateCollectionProfile(ic, mp, user, method);
		writeUpdateProperties(ic, user);
		writer.updateLazy(WriterFacade.toList(ic), null, user);
		return retrieveLazy(ic.getId(), user);
	}

	/**
	 * Patch an collection. !!! Use with Care !!!
	 * 
	 * @param triples
	 * @param user
	 * @throws ImejiException
	 */
	public void patch(List<ImejiTriple> triples, User user,
			boolean checkSecurity) throws ImejiException {
		writer.patch(triples, user, checkSecurity);
	}

	/**
	 * Delete a {@link CollectionImeji} and all its {@link Item}
	 * 
	 * @param collection
	 * @param user
	 * @throws ImejiException
	 */
	public void delete(CollectionImeji collection, User user)
			throws ImejiException {
		ItemController itemController = new ItemController();
		List<String> itemUris = itemController.search(collection.getId(), null,
				null, null, user, null).getResults();
		if (hasImageLocked(itemUris, user)) {
			throw new RuntimeException(
					((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
							.getMessage("collection_locked"));
		} else {
			if (collection.getStatus() != Status.PENDING && !user.isAdmin()) {
				throw new UnprocessableError(
						"Collection is not pending and can not be deleted!");
			}
			// Delete images
			List<Item> items = (List<Item>) itemController.retrieve(itemUris,
					-1, 0, user);
			itemController.delete(items, user);
			// Delete profile
			ProfileController pc = new ProfileController();
			try {

				MetadataProfile collectionMdp = pc.retrieve(
						collection.getProfile(), user);
				if ((pc.isReferencedByOtherResources(collectionMdp.getId()
						.toString(), collection.getId().toString()))
						|| (!AuthUtil.staticAuth().delete(user,
								collectionMdp.getId().toString()))
						|| collectionMdp.getDefault()) {
					logger.info("Metadata profile related to this collection is referenced elsewhere, or user does not have permission to delete this profile."
							+ "Profile <"
							+ collectionMdp.getId().toString()
							+ "> will not be deleted!");
				} else {
					logger.info("Metadata profile <"
							+ collectionMdp.getId().toString()
							+ "> is not referenced elsewhere, will be deleted!");
					pc.delete(collectionMdp, user, collection.getId()
							.toString());
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
	public void release(CollectionImeji collection, User user)
			throws ImejiException {
		ItemController itemController = new ItemController();

		if (user == null) {
			throw new AuthenticationError("User must be signed-in");
		}

		if (collection == null) {
			throw new NotFoundException("collection object does not exists");
		}

		List<String> itemUris = itemController.search(collection.getId(), null,
				null, null, user, null).getResults();

		if (hasImageLocked(itemUris, user)) {
			throw new UnprocessableError(
					((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
							.getMessage("collection_locked"));
		} else if (itemUris.isEmpty()) {
			throw new UnprocessableError(
					"An empty collection can not be released!");
		} else if (collection.getStatus().equals(Status.RELEASED)) {
			throw new UnprocessableError("The status of collection is "
					+ collection.getStatus()
					+ " and can not be released again!");
		} else {
			writeReleaseProperty(collection, user);
			List<Item> items = (List<Item>) itemController.retrieve(itemUris,
					-1, 0, user);
			itemController.release(items, user);
			update(collection, user);
			if (AuthUtil.staticAuth().administrate(user,
					collection.getProfile().toString())) {
				ProfileController pc = new ProfileController();
				pc.release(pc.retrieve(collection.getProfile(), user), user);
			}
		}
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

		List<String> itemUris = itemController.search(coll.getId(), null, null,
				null, user, null).getResults();
		if (hasImageLocked(itemUris, user)) {
			throw new UnprocessableError(
					((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
							.getMessage("collection_locked"));
		} else if (!Status.RELEASED.equals(coll.getStatus())) {

			throw new UnprocessableError(
					"Withdraw collection: Collection must be released");
		} else {
			List<Item> items = (List<Item>) itemController.retrieve(itemUris,
					-1, 0, user);
			itemController.withdraw(items, coll.getDiscardComment(), user);
			writeWithdrawProperties(coll, null);
			update(coll, user);
			if (!coll.getProfile().equals(Imeji.defaultMetadataProfile.getId())
					&& AuthUtil.staticAuth().administrate(user,
							coll.getProfile().toString())) {
				// Withdraw profile
				ProfileController pc = new ProfileController();
				if (!pc.isReferencedByOtherResources(coll.getProfile()
						.toString(), coll.getId().toString()))
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
	public SearchResult search(SearchQuery searchQuery, SortCriterion sortCri,
			int limit, int offset, User user, String spaceId) {
		Search search = SearchFactory.create(SearchType.COLLECTION);
		return search.search(searchQuery, sortCri, user, spaceId);
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

	public void updateCollectionItemsProfile(CollectionImeji ic,
			URI newProfileUri, User user) throws ImejiException {
		ItemController itemController = new ItemController();
		List<String> itemUris = itemController.search(ic.getId(), null, null,
				null, user, null).getResults();

		List<Item> items = (List<Item>) itemController.retrieve(itemUris, -1,
				0, user);
		itemController
				.updateItemsProfile(items, user, newProfileUri.toString());
	}

	public List<CollectionImeji> retrieveCollectionsNotInSpace(final User u) {
		return Lists.transform(ImejiSPARQL.exec(
				SPARQLQueries.selectCollectionsNotInSpace(),
				Imeji.collectionModel),
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
}
