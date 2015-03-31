/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import de.mpg.imeji.exceptions.*;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.search.query.URLQueryTransformer;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.j2j.helper.DateHelper;
import de.mpg.j2j.helper.J2JHelper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

/**
 * Implements CRUD and Search methods for {@link Album}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AlbumController extends ImejiController {
	private static final ReaderFacade reader = new ReaderFacade(
			Imeji.albumModel);
	private static final WriterFacade writer = new WriterFacade(
			Imeji.albumModel);

	/**
	 * Construct a new controller for {@link Album}
	 */
	public AlbumController() {
		super();
	}

	/**
	 * Creates a new album. - Add a unique id - Write user properties
	 * 
	 * @param album
	 * @param user
	 */
	public URI create(Album album, User user)
			throws ImejiException {  
		return createAskValidate(album, user, true);
	}
	public URI createNoValidate(Album album, User user)
			throws ImejiException {  
		return createAskValidate(album, user, false);
	}
	
	public URI createAskValidate(Album album, User user, boolean validate) throws ImejiException {
		if(validate){
			validateAlbum(album, user);
		}
		writeCreateProperties(album, user);
		GrantController gc = new GrantController();
		gc.addGrants(user, AuthorizationPredefinedRoles.admin(album.getId()
				.toString(), null), user);
		writer.create(WriterFacade.toList(album), user);
		return album.getId();
	}

	/**
	 * Updates an album -Logged in users: --User is album owner --OR
	 * user is album editor
	 * 
	 * @param ic
	 * @param user
	 * @throws ImejiException
	 */
	public Album update(Album ic, User user) throws ImejiException {
		writeUpdateProperties(ic, user);
		writer.update(WriterFacade.toList(ic), user);
		return retrieve(ic.getId(), user);
	}

	/**
	 * Updates an album -Logged in users: --User is album owner --OR
	 * user is album editor
	 * 
	 * @param ic
	 * @param user
	 * @throws ImejiException
	 */
	public void updateLazy(Album ic, User user) throws ImejiException {
		writeUpdateProperties(ic, user);
		writer.updateLazy(WriterFacade.toList(ic), user);
	}

	/**
	 * Load {@link Album} and {@link Item}: can lead to performance issues
	 * 
	 * @param selectedAlbumId
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public Album retrieve(URI selectedAlbumId, User user) throws ImejiException {
		return (Album) reader.read(selectedAlbumId.toString(), user,
				new Album());
	}

	/**
	 * Retrieve albums filtered by query
	 *
	 * @param user
	 * @param q
	 * @return
	 * @throws ImejiException
	 */
	public List<Album> retrieve(User user, String q) throws ImejiException {
		List<Album> aList = new ArrayList<>();
		try {
			SearchQuery sq= URLQueryTransformer.parseStringQuery(q);
			for (String albId: search(!isNullOrEmptyTrim(q) ? URLQueryTransformer.parseStringQuery(q) : null, user, null, 0, 0).getResults()) {
				aList.add(retrieve(URI.create(albId), user));
			}
		} catch (Exception e) {
			throw new UnprocessableError("Cannot retrieve albums:", e);

		}
		return aList;
	}


	/**
	 * Retrieve an {@link Album} without its {@link Item}
	 * 
	 * @param uri
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public Album retrieveLazy(URI uri, User user) throws ImejiException {
		return (Album) reader.readLazy(uri.toString(), user, new Album());
	}

	/**
	 * Delete the {@link Album}
	 * 
	 * @param album
	 * @param user
	 * @throws ImejiException
	 */
	public void delete(Album album, User user) throws ImejiException {
		writer.delete(WriterFacade.toList(album), user);
	}

	/**
	 * Release and {@link Album}. If one {@link Item} of the {@link Album} is
	 * not released, then abort.
	 * 
	 * @param album
	 * @throws ImejiException
	 */
	public void release(Album album, User user) throws ImejiException {
		ItemController ic = new ItemController();
		album = (Album) ic.searchAndSetContainerItems(album, user, -1, 0);
		if (album.getImages().isEmpty()) {
			throw new UnprocessableError("An empty album can not be released!");
		} else if(album.getStatus().equals(Status.RELEASED)){
			throw new UnprocessableError("The status of album is " + album.getStatus() + " and can not be released again!");
		}else {
			writeReleaseProperty(album, user);
			update(album, user);
		}
	}

	/**
	 * Add a list of {@link Item} (as a {@link List} of {@link URI}) to an
	 * {@link Album}. Return {@link List} of {@link URI} {@link Item} of the album.
	 * 
	 * @param album
	 * @param uris
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public List<URI> addToAlbum(Album album, List<String> uris, User user) throws ImejiException {
		ItemController ic = new ItemController();
		List<String> inAlbums = ic.search(album.getId(), null, null, null, user).getResults();
		List<String> notAddedUris = new ArrayList<String>();
		for (String uri : uris) {
			try {
				Item item = ic.retrieve(new URI(uri), user);			
				if(item != null){
					if (!inAlbums.contains(uri)) {
						inAlbums.add(uri);
					} else {
						notAddedUris.add(uri);
					}
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}	
		}
		album.getImages().clear();
		for (String uri : inAlbums) {
			album.getImages().add(URI.create(uri));
		}
		// Force admin user since th user might not have right to edit the album
		update(album, user);
		return new ArrayList(album.getImages());
	}

	/**
	 * Remove a list of {@link Item} (as a {@link List} of {@link URI}) to an
	 * {@link Album}
	 * 
	 * @param album
	 * @param toDelete
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public int removeFromAlbum(Album album, List<String> toDelete, User user)
			throws ImejiException {
		List<URI> inAlbums = new ArrayList<URI>(album.getImages());
		album.getImages().clear();
		for (URI uri : inAlbums) {
			if (!toDelete.contains(uri.toString())) {
				album.getImages().add(uri);
			}
		}
		update(album, user);
		return inAlbums.size() - album.getImages().size();
	}
	
	/**
	 * Remove a list of {@link Item} (as a {@link List} of {@link URI}) to an
	 * {@link Album}
	 * 
	 * @param album
	 * @param toDelete
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public boolean clearAlbumItems(Album album, User user)
			throws ImejiException {

		album.getImages().clear();
		update(album, user);
		return true;
	}

	/**
	 * Withdraw an {@link Album}: Set the {@link Status} as withdraw and remove
	 * all {@link Item}
	 * 
	 * @param album
	 * @throws ImejiException
	 */
	public void withdraw(Album album, User user) throws ImejiException {
		if (user == null ) {
			throw new AuthenticationError("User must be signed-in");
		}
		if (album == null) {
			throw new NotFoundException("Album does not exists");
		}
		if (!Status.RELEASED.equals(album.getStatus())) {
			throw new UnprocessableError ("Withdraw album: Album must be released");
		}
		album.setStatus(Status.WITHDRAWN);
		album.setVersionDate(DateHelper.getCurrentDate());
		album.getImages().clear();
		update(album, user);
	}

	/**
	 * Search for albums - Logged-out user: --Collection must be released
	 * -Logged-in users --Collection is released --OR Collection is pending AND
	 * user is owner --OR Collection is withdrawn AND user is owner --OR
	 * Collection is pending AND user has grant "Container Editor" for it.
	 * 
	 * @param user
	 * @param scList
	 * @return
	 */
	public SearchResult search(SearchQuery searchQuery, User user,
			SortCriterion sortCri, int limit, int offset) {
		Search search = SearchFactory.create(SearchType.ALBUM);
		return search.search(searchQuery, sortCri, user);
	}

	/**
	 * Load the albums without the images
	 * 
	 * @param uris
	 * @param limit
	 * @param offset
	 * @return
	 * @throws ImejiException
	 */
	public Collection<Album> loadAlbumsLazy(List<String> uris, User user,
			int limit, int offset) throws ImejiException {
		List<Album> albs = new ArrayList<Album>();
		int counter = 0;
		for (String s : uris) {
			if (offset <= counter
					&& (counter < (limit + offset) || limit == -1)) {
				albs.add((Album) J2JHelper.setId(new Album(), URI.create(s)));
			}
			counter++;
		}
		reader.readLazy(J2JHelper.cast2ObjectList(albs), user);
		return albs;
	}

	/**
	 * Retrieve all imeji {@link Album}
	 * 
	 * @return
	 * @throws ImejiException
	 */
	public List<Album> retrieveAll(User user) throws ImejiException {
		List<String> uris = ImejiSPARQL.exec(SPARQLQueries.selectAlbumAll(),
				Imeji.albumModel);
		return (List<Album>) loadAlbumsLazy(uris, user, -1, 0);
	}
	
	public void validateAlbum (Album album, User u) throws ImejiException {
		//Copied from Collection Bean in presentation  
		if ( isNullOrEmpty (album.getMetadata().getTitle().trim())) {
			throw new BadRequestException("error_album_need_title");
		}

		List<Person> pers = new ArrayList<Person>();
		
		for (Person c : album.getMetadata().getPersons()) {
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
			throw new BadRequestException("error_album_need_one_author");
		}
	}
}
