/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.helper.DateHelper;
import de.mpg.j2j.helper.J2JHelper;

public class AlbumController extends ImejiController
{
    private static ImejiRDF2Bean imejiRDF2Bean = null;
    private static ImejiBean2RDF imejiBean2RDF = null;

    public AlbumController(User user)
    {
        super(user);
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.albumModel);
    }

    /**
     * Creates a new collection. - Add a unique id - Write user properties
     * 
     * @param album
     * @param user
     */
    public void create(Album album) throws Exception
    {
        writeCreateProperties(album, user);
        imejiBean2RDF.create(imejiBean2RDF.toList(album), user);
        user = addCreatorGrant(album.getId(), user);
    }

    /**
     * Updates a collection -Logged in users: --User is collection owner --OR user is collection editor
     * 
     * @param ic
     * @param user
     * @throws Exception
     */
    public void update(Album ic) throws Exception
    {
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
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
    public void updateLazy(Album ic) throws Exception
    {
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
        writeUpdateProperties(ic, user);
        imejiBean2RDF.updateLazy(imejiBean2RDF.toList(ic), user);
    }

    /**
     * Load album and images: can lead to performance issues
     * 
     * @deprecated
     * @param selectedAlbumId
     * @param user
     * @return
     * @throws Exception
     */
    public Album retrieve(URI selectedAlbumId, User user) throws Exception
    {
        return (Album)imejiRDF2Bean.load(selectedAlbumId.toString(), user, new Album());
    }

    public Album retrieveLazy(URI selectedAlbumId, User user) throws Exception
    {
        Album a = (Album)imejiRDF2Bean.loadLazy(selectedAlbumId.toString(), user, new Album());
        // a = (Album)loadContainerItems(a, user);
        return a;
    }

    public void delete(Album album, User user) throws Exception
    {
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
        imejiBean2RDF.delete(imejiBean2RDF.toList(album), user);
        GrantController gc = new GrantController(user);
        gc.removeAllGrantsFor(user, album.getId());
    }

    public void release(Album album) throws Exception
    {
        ItemController ic = new ItemController(user);
        List<String> itemUris = ic.searchImagesInContainer(album.getId(), null, null, -1, 0).getResults();
        if (itemUris.isEmpty())
        {
            throw new RuntimeException("An empty album can not be released!");
        }
        else if (hasImageLocked(itemUris, user))
        {
            throw new RuntimeException("Album has at least one image locked by an other user.");
        }
        else if (hasPendingImage(itemUris))
        {
            throw new RuntimeException(
                    "Album has at least one image with status pending. All images have to be released to release an album");
        }
        else
        {
            writeReleaseProperty(album, user);
            update(album);
        }
    }

    public List<String> addToAlbum(Album album, List<String> uris, User user) throws Exception
    {
        ItemController ic = new ItemController(user);
        List<String> inAlbums = ic.searchImagesInContainer(album.getId(), null, null, -1, 0).getResults();
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
        update(album);
        return notAddedUris;
    }

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
        update(album);
        return inAlbums.size() - album.getImages().size();
    }

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

    public void withdraw(Album album) throws Exception
    {
        album.setStatus(Status.WITHDRAWN);
        album.setVersionDate(DateHelper.getCurrentDate());
        album.getImages().clear();
        update(album);
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

    public int countAllAlbums()
    {
        return ImejiSPARQL.execCount("SELECT count(DISTINCT ?s) WHERE { ?s a <http://imeji.org/terms/album>}",
                ImejiJena.albumModel);
    }
    
    public List<Album> retrieveAll() throws Exception
    {
        List<String> uris =  ImejiSPARQL.exec("SELECT ?s WHERE { ?s a <http://imeji.org/terms/album>}",
                ImejiJena.albumModel);
       return (List<Album>)loadAlbumsLazy(uris, -1, 0);
    }
}
