/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.AuthenticationError;
import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.j2j.helper.J2JHelper;
import static com.google.common.base.Strings.isNullOrEmpty;

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
	public URI create(CollectionImeji c, MetadataProfile p, User user)
			throws ImejiException {  

		if (p == null) {
			p = new MetadataProfile();
			p.setDescription(c.getMetadata().getDescription());
			p.setTitle(c.getMetadata().getTitle());
			ProfileController pc = new ProfileController();
			p = pc.create(p, user);
		}
		
		validateCollection(c, user);
		
		writeCreateProperties(c, user);
		c.setProfile(p.getId());
		writer.create(WriterFacade.toList(c), user);
		// Prepare grants
		GrantController gc = new GrantController();
		gc.addGrants(user, AuthorizationPredefinedRoles.admin(c.getId().toString(), p.getId().toString()), user);
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
	 * Retrieve the {@link CollectionImeji} without its {@link Item}
	 * 
	 * @param uri
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public CollectionImeji retrieveLazy(URI uri, User user) throws ImejiException {
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
		int counter = 0;
		for (String s : uris) {
			if (offset <= counter
					&& (counter < (limit + offset) || limit == -1)) {
				try {
					cols.add((CollectionImeji) J2JHelper.setId(
							new CollectionImeji(), URI.create(s)));
				} catch (Exception e) {
					logger.error("Error loading collection " + s, e);
				}
			}
			counter++;
		}
		reader.readLazy(J2JHelper.cast2ObjectList(cols), user);
		return cols;
	}

	/**
	 * Update a {@link CollectionImeji} (inclusive its {@link Item}: slow for
	 * huge {@link CollectionImeji})
	 * 
	 * @param ic
	 * @param user
	 * @throws ImejiException
	 */
	public void update(CollectionImeji ic, User user) throws ImejiException {
		validateCollection(ic, user);
		writeUpdateProperties(ic, user);
		writer.update(WriterFacade.toList(ic), user);
	}

	/**
	 * Update the {@link CollectionImeji} but not iths {@link Item}
	 * 
	 * @param ic
	 * @param user
	 * @throws ImejiException
	 */
	public void updateLazy(CollectionImeji ic, User user) throws ImejiException {
		writeUpdateProperties(ic, user);
		writer.updateLazy(WriterFacade.toList(ic), user);
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
		List<String> itemUris = itemController.search(collection.getId(), null,
				null, null, user).getResults();
		if (hasImageLocked(itemUris, user)) {
			throw new RuntimeException(
					((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
							.getMessage("collection_locked"));
		} else {
			if (collection.getStatus() != Status.PENDING && !user.isAdmin()) {
				throw new UnprocessableError("Collection is not pending and can not be released!");
			}
			// Delete images
			List<Item> items = (List<Item>) itemController.retrieve(itemUris,
					-1, 0, user);
			itemController.delete(items, user);
			// Delete profile
			ProfileController pc = new ProfileController();
			pc.delete(pc.retrieve(collection.getProfile(), user), user);
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
		
		if (user == null ) {
			throw new AuthenticationError("User must be signed-in");
		}
		
		if (collection == null ) {
			throw new NotFoundException("collection object does not exists");
		}
		
		List<String> itemUris = itemController.search(collection.getId(), null,
				null, null, user).getResults();
	
		if (hasImageLocked(itemUris, user)) {
			throw new UnprocessableError(
					((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
							.getMessage("collection_locked"));
		} else if (itemUris.isEmpty()) {
			throw new UnprocessableError(
					"An empty collection can not be released!");
		} else if(collection.getStatus().equals(Status.RELEASED)){
			throw new UnprocessableError("The status of collection is " + collection.getStatus() + " and can not be released again!");
		}
		else {
			writeReleaseProperty(collection, user);
			List<Item> items = (List<Item>) itemController.retrieve(itemUris,
					-1, 0, user);
			itemController.release(items, user);
			update(collection, user);
			ProfileController pc = new ProfileController();
			pc.release(pc.retrieve(collection.getProfile(), user), user);
		}
	}

	/**
	 * Withdraw a {@link CollectionImeji} and all its {@link Item}
	 * 
	 * @param collection
	 * @throws ImejiException
	 */
	public void withdraw(CollectionImeji collection, User user)
			throws ImejiException {
		ItemController itemController = new ItemController();
		
		if (user == null ) {
			throw new AuthenticationError("User must be signed-in");
		}
		
		if (collection == null) {
			throw new NotFoundException("Collection does not exists");
		}
		
		List<String> itemUris = itemController.search(collection.getId(), null,
				null, null, user).getResults();
		if (hasImageLocked(itemUris, user)) {
			throw new UnprocessableError (
					((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
							.getMessage("collection_locked"));
		} else if (!Status.RELEASED.equals(collection.getStatus())) {

			throw new UnprocessableError (
					"Withdraw collection: Collection must be released");
		} else {
			List<Item> items = (List<Item>) itemController.retrieve(itemUris,
					-1, 0, user);
			itemController
					.withdraw(items, collection.getDiscardComment(), user);
			writeWithdrawProperties(collection, null);
			update(collection, user);
			// Withdraw profile
			ProfileController pc = new ProfileController();
			pc.withdraw(pc.retrieve(collection.getProfile(), user), user);
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
			int limit, int offset, User user) {
		Search search = SearchFactory.create(SearchType.COLLECTION);
		return search.search(searchQuery, sortCri, user);
	}
	
	/**
	 * Validate the collection information provided, before it has been submitted to the writer
	 
	 * @param collection
	 * @param u
	 * @throws ImejiException
	 */
	public void validateCollection (CollectionImeji collection, User u) throws ImejiException {
		//Copied from Collection Bean in presentation  
		if ( isNullOrEmpty (collection.getMetadata().getTitle())) {
			throw new BadRequestException("error_collection_need_title");
		}

		List<Person> pers = new ArrayList<Person>();
		
		for (Person c : collection.getMetadata().getPersons()) {
			List<Organization> orgs = new ArrayList<Organization>();
			for (Organization o : c.getOrganizations()) {
				if (!isNullOrEmpty(o.getName())) {
					orgs.add(o);
				}
				else
				{
					throw new BadRequestException("error_organization_need_name");
				}
			}
			
			
			if (! isNullOrEmpty(c.getFamilyName())) {
				if (orgs.size() > 0) {
					pers.add(c);
				} else {
					throw new BadRequestException("error_author_need_one_organization");
				}
			} else {
				throw new BadRequestException("error_author_need_one_family_name");
			}
		}

		if (pers.size() == 0 || pers == null || pers.isEmpty()) {
			throw new BadRequestException("error_collection_need_one_author");
		}
	
//TODO Update needed, it doesn't work by creating new collection withour using profile template
		/*
		//Check the collection Profile
		ProfileController pc = new ProfileController();
		if (!isNullOrEmpty(collection.getProfile().toString())) {
		try {
			pc.retrieve(collection.getProfile(), u);
		} catch (ImejiException e) {
			throw new UnprocessableError("error_provided_metadata_profile_does_not_exist");
		}
		}
		*/
		//if (collection.getProfile()
	
	}

}
