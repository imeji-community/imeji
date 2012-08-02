/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.search.ImejiSPARQL;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.j2j.exceptions.NotFoundException;
import de.mpg.j2j.helper.DateHelper;

public class AlbumController extends ImejiController
{
    private static ImejiRDF2Bean imejiRDF2Bean = null;
    private static ImejiBean2RDF imejiBean2RDF = null;
    private static Logger logger = Logger.getLogger(CollectionController.class);

    public AlbumController(User user)
    {
        super(user);
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.albumModel);
    }

    /**
     * Creates a new collection. - Add a unique id - Write user properties
     * 
     * @param ic
     * @param user
     */
    public void create(Album ic) throws Exception
    {
        writeCreateProperties(ic, user);
        ic.setStatus(Status.PENDING);
        ic.setId(new URI("http://imeji.org/terms/album/" + getUniqueId()));
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
        imejiBean2RDF.create(imejiBean2RDF.toList(ic), user);
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.albumModel);
        // ic = (Album) imejiRDF2Bean.load(ic.getId().toString(), user);
        user = addCreatorGrant(ic.getId(), user);
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

    public Album retrieve(URI selectedAlbumId) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.albumModel);
        return (Album)imejiRDF2Bean.load(selectedAlbumId.toString(), user, new Album());
    }

    public Album retrieveLazy(URI selectedAlbumId) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.albumModel);
        return (Album)imejiRDF2Bean.loadLazy(selectedAlbumId.toString(), user, new Album());
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

    public Collection<Album> load(List<String> uris, int limit, int offset)
    {
        LinkedList<Album> albs = new LinkedList<Album>();
        ImejiRDF2Bean reader = new ImejiRDF2Bean(ImejiJena.albumModel);
        int counter = 0;
        for (String s : uris)
        {
            if (offset <= counter && (counter < (limit + offset) || limit == -1))
            {
                try
                {
                    albs.add((Album)reader.load(s, user, new Album()));
                }
                catch (Exception e)
                {
                    logger.error("Error loading image " + s);
                }
            }
            counter++;
        }
        return albs;
    }

    public int countAllAlbums()
    {
        return ImejiSPARQL.execCount("SELECT count(DISTINCT ?s) WHERE { ?s a <http://imeji.org/terms/album>}",
                ImejiJena.albumModel);
    }

    @Override
    @Deprecated
    protected String getSpecificQuery() throws Exception
    {
        return " . ?s <http://imeji.org/terms/properties> ?props . ?props <http://imeji.org/terms/createdBy> ?createdBy . ?props <http://imeji.org/terms/status> ?status";
    }

    @Override
    protected String getSpecificFilter() throws Exception
    {
        // Add filters for user management
        String filter = "(";
        if (user == null)
        {
            filter += "?status = <" + Status.RELEASED.getUri() + ">";
        }
        else
        {
            String userUri = "http://xmlns.com/foaf/0.1/Person/" + URLEncoder.encode(user.getEmail(), "UTF-8");
            filter += "?status = <" + Status.RELEASED.getUri() + "> || ?createdBy=<" + userUri + ">";
            for (Grant grant : user.getGrants())
            {
                switch (grant.asGrantType())
                {
                    case CONTAINER_ADMIN: // Add specifics here
                    default:
                        filter += " || ?s=<" + grant.getGrantFor().toString() + ">";
                }
            }
        }
        filter += ")";
        return filter;
    }
}
