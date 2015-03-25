/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import de.mpg.imeji.exceptions.*;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.query.URLQueryTransformer;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.j2j.helper.J2JHelper;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

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
	
	public static enum MetadataProfileCreationMethod
	    {
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
	public URI create(CollectionImeji c, MetadataProfile p, User user)
			throws ImejiException {  
		return createAskValidate(c, p, user, true, MetadataProfileCreationMethod.COPY);
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
	public URI create(CollectionImeji c, MetadataProfile p, User user, MetadataProfileCreationMethod method)
			throws ImejiException {  
		return createAskValidate(c, p, user, true, method);
	}


	public URI createNoValidate(CollectionImeji c, MetadataProfile p, User user, MetadataProfileCreationMethod method)
			throws ImejiException {  
		return createAskValidate(c, p, user, false, method);
	}
	
	private URI createAskValidate(CollectionImeji c, MetadataProfile p, User user, boolean validate, MetadataProfileCreationMethod method)
			throws ImejiException { 
		  
		    ProfileController pc = new ProfileController();
		    String metadataProfileName=" (Metadata profile)";
			if (p == null || method.equals(MetadataProfileCreationMethod.NEW)) {
				p = new MetadataProfile();
				p.setDescription(c.getMetadata().getDescription());
				p.setTitle(c.getMetadata().getTitle()+metadataProfileName);
				p = pc.create(p, user);
			}
			else if ( p != null && !method.equals(MetadataProfileCreationMethod.REFERENCE)) {
				if (method.equals(MetadataProfileCreationMethod.COPY)) {
					p.setTitle(c.getMetadata().getTitle()+metadataProfileName);
				}
				
				
				p = pc.create(p.cloneWithTitle(), user);
			}
			
			c.setProfile(p.getId());
			
			if (validate)
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
	public List<Item> retrieveItems(String id, User user, String q) throws ImejiException {
        ItemController ic = new ItemController();
        List<Item> itemList = new ArrayList();
        try {
            for (String itemId: ic.search(ObjectHelper.getURI(CollectionImeji.class, id),
                    !isNullOrEmptyTrim(q) ? URLQueryTransformer.parseStringQuery(q) : null,
                    null, null, user).getResults()) {
                itemList.add(ic.retrieve(URI.create(itemId), user));
            }
        } catch (Exception e) {
            throw new UnprocessableError("Cannot retrieve items:", e);

        }
        return itemList;
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
	public CollectionImeji update(CollectionImeji ic, User user) throws ImejiException {
		validateCollection(ic, user);
		writeUpdateProperties(ic, user);
		writer.update(WriterFacade.toList(ic), user);
        return retrieve(ic.getId(), user);
	}

	/**
	 * Update the {@link CollectionImeji} but not iths {@link Item}
	 * 
	 * @param ic
	 * @param user
	 * @throws ImejiException
	 */
	public CollectionImeji updateLazy(CollectionImeji ic, User user) throws ImejiException {
        validateCollection(ic, user);
        writeUpdateProperties(ic, user);
		writer.updateLazy(WriterFacade.toList(ic), user);
        return retrieveLazy(ic.getId(), user);
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
			try {
				MetadataProfile collectionMdp= pc.retrieve(collection.getProfile(), user);
				if (collectionMdp != null) {
					pc.delete(collectionMdp, user);
					
				}
			}
			catch (NotFoundException e){
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
		if ( isNullOrEmpty (collection.getMetadata().getTitle().trim())) {
			throw new BadRequestException("error_collection_need_title");
		}

		List<Person> pers = new ArrayList<Person>();
		
		for (Person c : collection.getMetadata().getPersons()) {
			List<Organization> orgs = new ArrayList<Organization>();
			for (Organization o : c.getOrganizations()) {
				if (!isNullOrEmpty(o.getName().trim())) {
					orgs.add(o);
				}
				else
				{
					throw new BadRequestException("error_organization_need_name");
				}
			}
			
			
			if (! isNullOrEmpty(c.getFamilyName().trim())) {
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
		
		}
	
	public MetadataProfileCreationMethod getProfileCreationMethod (String method) {
		if  ( "reference".equalsIgnoreCase(method)) {
			return MetadataProfileCreationMethod.REFERENCE;
		}
		else if ("copy".equalsIgnoreCase(method) )
		{
			return MetadataProfileCreationMethod.COPY;
		}
		else {
			return MetadataProfileCreationMethod.NEW; 
		}
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
