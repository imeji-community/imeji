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
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.j2j.helper.J2JHelper;

/**
 * CRUD controller for {@link CollectionImeji}, plus search mehtods related to {@link CollectionImeji}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CollectionController extends ImejiController
{
    private static ImejiRDF2Bean imejiRDF2Bean = null;
    private static ImejiBean2RDF imejiBean2RDF = null;
    private static Logger logger = Logger.getLogger(CollectionController.class);

    /**
     * Default constructor
     */
    public CollectionController()
    {
        super();
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
    }

    /**
     * @deprecated
     * @param user
     */
    @Deprecated
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
    public URI create(CollectionImeji ic, URI profile, User user) throws Exception
    {
        writeCreateProperties(ic, user);
        ic.setProfile(profile);
        imejiBean2RDF.create(imejiBean2RDF.toList(ic), user);
        addCreatorGrant(ic.getId(), user);
        return ic.getId();
    }

    /**
     * Update a {@link CollectionImeji} (inclusive its {@link Item}: slow for huge {@link CollectionImeji})
     * 
     * @param ic
     * @param user
     * @deprecated
     */
    @Deprecated
    public void update(CollectionImeji ic) throws Exception
    {
        writeUpdateProperties(ic, user);
        imejiBean2RDF.update(imejiBean2RDF.toList(ic), user);
    }

    /**
     * Update a {@link CollectionImeji} (inclusive its {@link Item}: slow for huge {@link CollectionImeji})
     * 
     * @param ic
     * @param user
     * @throws Exception
     */
    public void update(CollectionImeji ic, User user) throws Exception
    {
        writeUpdateProperties(ic, user);
        imejiBean2RDF.update(imejiBean2RDF.toList(ic), user);
    }

    /**
     * Update the {@link CollectionImeji} but not iths {@link Item}
     * 
     * @param ic
     * @param user
     * @throws Exception
     */
    public void updateLazy(CollectionImeji ic, User user) throws Exception
    {
        writeUpdateProperties(ic, user);
        imejiBean2RDF.updateLazy(imejiBean2RDF.toList(ic), user);
    }

    /**
     * Delete a {@link CollectionImeji} and all its {@link Item}
     * 
     * @param collection
     * @param user
     * @throws Exception
     */
    public void delete(CollectionImeji collection, User user) throws Exception
    {
        ItemController itemController = new ItemController(user);
        List<String> itemUris = itemController.search(collection.getId(), null, null, null).getResults();
        if (hasImageLocked(itemUris, user))
        {
            throw new RuntimeException(
                    ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("collection_locked"));
        }
        else
        {
            // Delete images
            List<Item> items = (List<Item>)itemController.loadItems(itemUris, -1, 0);
            itemController.delete(items, user);
            // Delete profile
            ProfileController pc = new ProfileController();
            pc.delete(pc.retrieve(collection.getProfile(), user), user);
            imejiBean2RDF.delete(imejiBean2RDF.toList(collection), user);
            GrantController gc = new GrantController(user);
            gc.removeAllGrantsFor(user, collection.getId());
        }
    }

    /**
     * Release a {@link CollectionImeji} and all its {@link Item}
     * 
     * @param collection
     * @param user
     * @throws Exception
     */
    public void release(CollectionImeji collection, User user) throws Exception
    {
        ItemController itemController = new ItemController(user);
        List<String> itemUris = itemController.search(collection.getId(), null, null, null).getResults();
        if (hasImageLocked(itemUris, user))
        {
            throw new RuntimeException(
                    ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("collection_locked"));
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
            update(collection, user);
            ProfileController pc = new ProfileController();
            pc.release(pc.retrieve(collection.getProfile(), user), user);
        }
    }

    /**
     * Withdraw a {@link CollectionImeji} and all its {@link Item}
     * 
     * @param collection
     * @throws Exception
     */
    public void withdraw(CollectionImeji collection, User user) throws Exception
    {
        ItemController itemController = new ItemController(user);
        List<String> itemUris = itemController.search(collection.getId(), null, null, null).getResults();
        if (hasImageLocked(itemUris, user))
        {
            throw new RuntimeException(
                    ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("collection_locked"));
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
            update(collection, user);
            // Withdraw profile
            ProfileController pc = new ProfileController();
            pc.withdraw(pc.retrieve(collection.getProfile(), user), user);
        }
    }

    /**
     * Retrieve a complete {@link CollectionImeji} (inclusive its {@link Item}: slow for huge {@link CollectionImeji})
     * 
     * @param uri
     * @return
     * @throws Exception
     */
    public CollectionImeji retrieve(URI uri) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
        return (CollectionImeji)imejiRDF2Bean.load(uri.toString(), user, new CollectionImeji());
    }

    /**
     * Retrieve a complete {@link CollectionImeji} (inclusive its {@link Item}: slow for huge {@link CollectionImeji})
     * 
     * @param uri
     * @param user
     * @return
     * @throws Exception
     */
    public CollectionImeji retrieve(URI uri, User user) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
        return (CollectionImeji)imejiRDF2Bean.load(uri.toString(), user, new CollectionImeji());
    }

    /**
     * Retrieve the {@link CollectionImeji} without its {@link Item}
     * 
     * @param uri
     * @return
     * @throws Exception
     * @{@link Deprecated}
     */
    public CollectionImeji retrieveLazy(URI uri) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
        return (CollectionImeji)imejiRDF2Bean.loadLazy(uri.toString(), user, new CollectionImeji());
    }

    /**
     * Retrieve the {@link CollectionImeji} without its {@link Item}
     * 
     * @param uri
     * @param user
     * @return
     * @throws Exception
     */
    public CollectionImeji retrieveLazy(URI uri, User user) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
        return (CollectionImeji)imejiRDF2Bean.loadLazy(uri.toString(), user, new CollectionImeji());
    }

    /**
     * Retieve all {@link CollectionImeji} in imeji
     * 
     * @return
     * @throws Exception
     */
    public List<CollectionImeji> retrieveAllCollections() throws Exception
    {
        List<String> uris = ImejiSPARQL.exec(SPARQLQueries.selectCollectionAll(), ImejiJena.collectionModel);
        return (List<CollectionImeji>)loadCollectionsLazy(uris, -1, 0);
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
        simplifiedUser.setId(user.getId());
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

    /**
     * Load {@link CollectionImeji} defined in a {@link List} of uris. Don't load the {@link Item} contained in the
     * {@link CollectionImeji}
     * 
     * @param uris
     * @param limit
     * @param offset
     * @return
     * @throws Exception
     */
    public Collection<CollectionImeji> loadCollectionsLazy(List<String> uris, int limit, int offset) throws Exception
    {
        List<CollectionImeji> cols = new ArrayList<CollectionImeji>();
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
                    logger.error("Error loading collection " + s, e);
                }
            }
            counter++;
        }
        imejiRDF2Bean.loadLazy(J2JHelper.cast2ObjectList(cols), user);
        return cols;
    }
}
