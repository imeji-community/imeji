/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.helper.DateHelper;
import de.mpg.j2j.helper.J2JHelper;

/**
 * Implements CRUD and Search methods for {@link Album}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AlbumController extends ImejiController
{
    private static ImejiRDF2Bean imejiRDF2Bean = null;
    private static ImejiBean2RDF imejiBean2RDF = null;

    /**
     * Construct a new controller for {@link Album}
     */
    public AlbumController()
    {
        super();
        imejiBean2RDF = new ImejiBean2RDF(Imeji.albumModel);
        imejiRDF2Bean = new ImejiRDF2Bean(Imeji.albumModel);
    }

    /**
     * @deprecated
     * @param user
     */
    @Deprecated
    public AlbumController(User user)
    {
        super(user);
        imejiBean2RDF = new ImejiBean2RDF(Imeji.albumModel);
        imejiRDF2Bean = new ImejiRDF2Bean(Imeji.albumModel);
    }

    /**
     * Creates a new collection. - Add a unique id - Write user properties
     * 
     * @param album
     * @param user
     */
    public void create(Album album, User user) throws Exception
    {
        writeCreateProperties(album, user);
        GrantController gc = new GrantController();
        gc.addGrants(user, AuthorizationPredefinedRoles.albumCreator(album.getId().toString()), user);
        imejiBean2RDF.create(imejiBean2RDF.toList(album), user);
    }

    /**
     * Updates a collection -Logged in users: --User is collection owner --OR user is collection editor
     * 
     * @param ic
     * @param user
     * @throws Exception
     */
    public void update(Album ic, User user) throws Exception
    {
        imejiBean2RDF = new ImejiBean2RDF(Imeji.albumModel);
        writeUpdateProperties(ic, user);
        imejiBean2RDF.update(imejiBean2RDF.toList(ic), user);
    }

    /**
     * Updates a collection -Logged in users: --User is collection owner --OR user is collection editor
     * 
     * @param ic
     * @param user
     * @throws Exception
     */
    public void updateLazy(Album ic, User user) throws Exception
    {
        imejiBean2RDF = new ImejiBean2RDF(Imeji.albumModel);
        writeUpdateProperties(ic, user);
        imejiBean2RDF.updateLazy(imejiBean2RDF.toList(ic), user);
    }

    /**
     * Load {@link Album} and {@link Item}: can lead to performance issues
     * 
     * @param selectedAlbumId
     * @param user
     * @return
     * @throws Exception
     */
    public Album retrieve(URI selectedAlbumId, User user) throws Exception
    {
        return (Album)imejiRDF2Bean.load(selectedAlbumId.toString(), user, new Album());
    }

    /**
     * Retrieve an {@link Album} without its {@link Item}
     * 
     * @param uri
     * @param user
     * @return
     * @throws Exception
     */
    public Album retrieveLazy(URI uri, User user) throws Exception
    {
        return (Album)imejiRDF2Bean.loadLazy(uri.toString(), user, new Album());
    }

    /**
     * Delete the {@link Album}
     * 
     * @param album
     * @param user
     * @throws Exception
     */
    public void delete(Album album, User user) throws Exception
    {
        imejiBean2RDF = new ImejiBean2RDF(Imeji.albumModel);
        imejiBean2RDF.delete(imejiBean2RDF.toList(album), user);
    }

    /**
     * Release and {@link Album}. If one {@link Item} of the {@link Album} is not released, then abort.
     * 
     * @param album
     * @throws Exception
     */
    public void release(Album album, User user) throws Exception
    {
        ItemController ic = new ItemController(user);
        List<String> itemUris = ic.search(album.getId(), null, null, null).getResults();
        if (itemUris.isEmpty())
        {
            throw new RuntimeException("An empty album can not be released!");
        }
        else if (hasImageLocked(itemUris, user))
        {
            throw new RuntimeException("Album has at least one item locked by an other user.");
        }
        else if (hasPendingImage(itemUris))
        {
            throw new RuntimeException(
                    "Album has at least one item with status pending. All items have to be released to release an album");
        }
        else
        {
            writeReleaseProperty(album, user);
            update(album, user);
        }
    }

    /**
     * Add a list of {@link Item} (as a {@link List} of {@link URI}) to an {@link Album}. Return {@link List} of
     * {@link URI} which were not added to the album.
     * 
     * @param album
     * @param uris
     * @param user
     * @return
     * @throws Exception
     */
    public List<String> addToAlbum(Album album, List<String> uris, User user) throws Exception
    {
        ItemController ic = new ItemController(user);
        List<String> inAlbums = ic.search(album.getId(), null, null, null).getResults();
        List<String> notAddedUris = new ArrayList<String>();
        for (String uri : uris)
        {
            if (!inAlbums.contains(uri))
            {
                inAlbums.add(uri);
            }
            else
            {
                notAddedUris.add(uri);
            }
        }
        album.getImages().clear();
        for (String uri : inAlbums)
        {
            album.getImages().add(URI.create(uri));
        }
        update(album, user);
        return notAddedUris;
    }

    /**
     * Remove a list of {@link Item} (as a {@link List} of {@link URI}) to an {@link Album}
     * 
     * @param album
     * @param toDelete
     * @param user
     * @return
     * @throws Exception
     */
    public int removeFromAlbum(Album album, List<String> toDelete, User user) throws Exception
    {
        List<URI> inAlbums = new ArrayList<URI>(album.getImages());
        album.getImages().clear();
        for (URI uri : inAlbums)
        {
            if (!toDelete.contains(uri.toString()))
            {
                album.getImages().add(uri);
            }
        }
        update(album, user);
        return inAlbums.size() - album.getImages().size();
    }

    /**
     * True if an {@link Item} of the {@link List} of {@link URI} has the {@link Status} "pending", else false
     * 
     * @param uris
     * @return
     * @throws Exception
     */
    public synchronized boolean hasPendingImage(List<String> uris) throws Exception
    {
        ItemController c = new ItemController(user);
        for (Item item : c.loadItems(uris, -1, 0))
        {
            if (Status.PENDING.equals(item.getStatus()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Withdraw an {@link Album}: Set the {@link Status} as withdraw and remove all {@link Item}
     * 
     * @param album
     * @throws Exception
     */
    public void withdraw(Album album, User user) throws Exception
    {
        album.setStatus(Status.WITHDRAWN);
        album.setVersionDate(DateHelper.getCurrentDate());
        album.getImages().clear();
        update(album, user);
    }

    /**
     * Search for albums - Logged-out user: --Collection must be released -Logged-in users --Collection is released --OR
     * Collection is pending AND user is owner --OR Collection is withdrawn AND user is owner --OR Collection is pending
     * AND user has grant "Container Editor" for it.
     * 
     * @param user
     * @param scList
     * @return
     */
    public SearchResult search(SearchQuery searchQuery, SortCriterion sortCri, int limit, int offset)
    {
        Search search = new Search(SearchType.ALBUM, null);
        return search.search(searchQuery, sortCri, user);
    }

    /**
     * Load the albums without the images
     * 
     * @param uris
     * @param limit
     * @param offset
     * @return
     * @throws Exception
     */
    public Collection<Album> loadAlbumsLazy(List<String> uris, int limit, int offset) throws Exception
    {
        List<Album> albs = new ArrayList<Album>();
        int counter = 0;
        for (String s : uris)
        {
            if (offset <= counter && (counter < (limit + offset) || limit == -1))
            {
                albs.add((Album)J2JHelper.setId(new Album(), URI.create(s)));
            }
            counter++;
        }
        imejiRDF2Bean.loadLazy(J2JHelper.cast2ObjectList(albs), user);
        return albs;
    }

    /**
     * Retrieve all imeji {@link Album}
     * 
     * @return
     * @throws Exception
     */
    public List<Album> retrieveAll() throws Exception
    {
        List<String> uris = ImejiSPARQL.exec(SPARQLQueries.selectAlbumAll(), Imeji.albumModel);
        return (List<Album>)loadAlbumsLazy(uris, -1, 0);
    }
}
