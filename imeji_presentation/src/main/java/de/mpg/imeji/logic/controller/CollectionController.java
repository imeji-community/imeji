/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collection;
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
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;
import de.mpg.j2j.helper.DateHelper;
import de.mpg.j2j.helper.J2JHelper;

public class CollectionController extends ImejiController
{
    private static ImejiRDF2Bean imejiRDF2Bean = null;
    private static ImejiBean2RDF imejiBean2RDF = null;
    private static Logger logger = Logger.getLogger(CollectionController.class);

    public CollectionController(User user)
    {
        super(user);
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
    }

    /**
     * Creates a new collection. - Add a unique id - Write user properties
     * 
     * @param ic
     * @param user
     */
    public URI create(CollectionImeji ic, URI profile) throws Exception
    {
        ProfileController pc = new ProfileController(user);
        pc.retrieve(profile);
        writeCreateProperties(ic, user);
        ic.setStatus(Status.PENDING);
        ic.setId(ObjectHelper.getURI(CollectionImeji.class, Integer.toString(getUniqueId())));
        ic.setProfile(profile);
        imejiBean2RDF.create(imejiBean2RDF.toList(ic), user);
        user = addCreatorGrant(ic.getId(), user);
        return ic.getId();
    }

    /**
     * Update a collection
     * 
     * @param ic
     * @param user
     */
    public void update(CollectionImeji ic) throws Exception
    {
        writeUpdateProperties(ic, user);
        imejiBean2RDF.update(imejiBean2RDF.toList(ic), user);
    }

    public void delete(CollectionImeji collection, User user) throws Exception
    {
        ItemController itemController = new ItemController(user);
        List<String> itemUris = itemController.searchImagesInContainer(collection.getId(), null, null, -1, 0)
                .getResults();
        if (hasImageLocked(itemUris, user))
        {
            throw new RuntimeException("Collection has at least one image locked by another user.");
        }
        else
        {
            // Delete images
            List<Item> items = (List<Item>)itemController.loadItems(itemUris, -1, 0);
            itemController.delete(items, user);
            // Delete profile
            ProfileController pc = new ProfileController(user);
            pc.delete(pc.retrieve(collection.getProfile()), user);
            imejiBean2RDF.delete(imejiBean2RDF.toList(collection), user);
            GrantController gc = new GrantController(user);
            gc.removeAllGrantsFor(user, collection.getId());
        }
    }

    public void release(CollectionImeji collection, User user) throws Exception
    {
        ItemController itemController = new ItemController(user);
        List<String> itemUris = itemController.searchImagesInContainer(collection.getId(), null, null, -1, 0)
                .getResults();
        if (hasImageLocked(itemUris, user))
        {
            throw new RuntimeException("Collection has at least one image locked by another user.");
        }
        else if (itemUris.isEmpty())
        {
            throw new RuntimeException("An empty collection can not be released!");
        }
        else
        {
            writeReleaseProperty(collection, user);
            List<Item> items = (List<Item>)itemController.loadItems(itemUris, -1, 0);
            itemController.release(items, user);
            update(collection);
            ProfileController pc = new ProfileController(user);
            pc.retrieve(collection.getProfile());
            pc.release(pc.retrieve(collection.getProfile()));
        }
    }

    public void withdraw(CollectionImeji collection) throws Exception
    {
        ItemController itemController = new ItemController(user);
        List<String> itemUris = itemController.searchImagesInContainer(collection.getId(), null, null, -1, 0)
                .getResults();
        if (hasImageLocked(itemUris, user))
        {
            throw new RuntimeException("Collection has at least one image locked by another user.");
        }
        else if (!Status.RELEASED.equals(collection.getStatus()))
        {
            throw new RuntimeException("Withdraw collection: Collection must be released");
        }
        else
        {
            List<Item> items = (List<Item>)itemController.loadItems(itemUris, -1, 0);
            itemController.withdraw(items, collection.getDiscardComment());
            writeWithdrawProperties(collection, null);
            update(collection);
            // Withdraw profile
            ProfileController pc = new ProfileController(user);
            pc.retrieve(collection.getProfile());
            pc.withdraw(pc.retrieve(collection.getProfile()), user);
        }
    }

    public CollectionImeji retrieve(URI uri) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
        return (CollectionImeji)imejiRDF2Bean.load(uri.toString(), user, new CollectionImeji());
    }

    public CollectionImeji retrieveLazy(URI uri) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
        return (CollectionImeji)imejiRDF2Bean.loadLazy(uri.toString(), user, new CollectionImeji());
    }

    public int countAllCollections()
    {
        return ImejiSPARQL.execCount("SELECT count(DISTINCT ?s) WHERE { ?s a <http://imeji.org/terms/collection>}",
                ImejiJena.collectionModel);
    }

    public SearchResult search(SearchQuery searchQuery, SortCriterion sortCri, int limit, int offset)
    {
        Search search = new Search(SearchType.COLLECTION, null);
        return search.search(searchQuery, sortCri, simplifyUser());
    }

    /**
     * Increase performance by restricting grants to the only grants needed
     * 
     * @param user
     * @return
     */
    public User simplifyUser()
    {
        if (user == null)
        {
            return null;
        }
        User simplifiedUser = new User();
        for (Grant g : user.getGrants())
        {
            if (GrantType.SYSADMIN.equals(g.asGrantType()))
            {
                simplifiedUser.getGrants().add(g);
            }
            else if (g.getGrantFor() != null && g.getGrantFor().toString().contains("collection"))
            {
                simplifiedUser.getGrants().add(g);
            }
        }
        return simplifiedUser;
    }

    public Collection<CollectionImeji> loadLazy(List<String> uris, int limit, int offset) throws Exception
    {
        LinkedList<CollectionImeji> cols = new LinkedList<CollectionImeji>();
        ImejiRDF2Bean reader = new ImejiRDF2Bean(ImejiJena.collectionModel);
        int counter = 0;
        for (String s : uris)
        {
            if (offset <= counter && (counter < (limit + offset) || limit == -1))
            {
                try
                {
                    cols.add((CollectionImeji)J2JHelper.setId(new CollectionImeji(), URI.create(s)));
                }
                catch (Exception e)
                {
                    logger.error("Error loading image " + s, e);
                }
            }
            counter++;
        }
        reader.loadLazy(J2JHelper.cast2ObjectList(cols), user);
        return cols;
    }

    @Override
    protected String getSpecificQuery() throws Exception
    {
        return " . ?s <http://imeji.org/terms/properties> ?props . ?props <http://imeji.org/terms/createdBy> ?createdBy . ?props <http://imeji.org/terms/status> ?status";
    }

    @Override
    protected String getSpecificFilter() throws Exception
    {
        // Add filters for user management
        String filter = "(";
        Security security = new Security();
        if (user == null)
        {
            filter += "?status = <" + Status.RELEASED.getUri() + ">";
        }
        else if (security.isSysAdmin(user))
        {
            filter += "?status = <" + Status.RELEASED.getUri() + "> || ?status = <" + Status.RELEASED.getUri() + ">";
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
                        break;
                    default:
                        filter += " || ?s=<" + grant.getGrantFor().toString() + ">";
                        break;
                }
            }
        }
        filter += ")";
        return filter;
    }
}
