/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.helper.J2JHelper;

/**
 * Implements CRUD and Search methods for {@link Item}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ItemController extends ImejiController
{
    private static Logger logger = Logger.getLogger(ItemController.class);
    private static ImejiRDF2Bean imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.imageModel);
    private static ImejiBean2RDF imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);

    /**
     * Controller constructor
     */
    public ItemController()
    {
        super();
    }

    public ItemController(User user)
    {
        super(user);
    }

    /**
     * Create an {@link Item} in a {@link CollectionImeji}
     * 
     * @param img
     * @param coll
     * @throws Exception
     */
    public void create(Item item, URI coll) throws Exception
    {
        Collection<Item> l = new ArrayList<Item>();
        l.add(item);
        create(l, coll);
    }

    /**
     * Create a {@link List} of {@link Item} in a {@link CollectionImeji}. This method is faster than using create(Item
     * item, URI coll) when creating many items
     * 
     * @param items
     * @param coll
     * @throws Exception
     */
    public void create(Collection<Item> items, URI coll) throws Exception
    {
        CollectionController cc = new CollectionController(user);
        CollectionImeji ic = cc.retrieve(coll);
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
        for (Item img : items)
        {
            writeCreateProperties(img, user);
            if (Status.PENDING.equals(ic.getStatus()))
            {
                img.setVisibility(Visibility.PRIVATE);
            }
            else
            {
                img.setVisibility(Visibility.PUBLIC);
            }
            img.setCollection(coll);
            img.getMetadataSet().setProfile(ic.getProfile());
            ic.getImages().add(img.getId());
        }
        imejiBean2RDF.create(J2JHelper.cast2ObjectList(new ArrayList<Item>(items)), user);
        cc.update(ic);
    }

    /**
     * Update an {@link Item} in the database
     * 
     * @param item
     * @throws Exception
     */
    public void update(Item item) throws Exception
    {
        Collection<Item> l = new ArrayList<Item>();
        l.add(item);
        update(l);
    }

    /**
     * Update a {@link Collection} of {@link Item}
     * 
     * @param items
     * @throws Exception
     */
    public void update(Collection<Item> items) throws Exception
    {
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
        List<Object> imBeans = new ArrayList<Object>();
        for (Item item : items)
        {
            writeUpdateProperties(item, user);
            imBeans.add(createFulltextForMetadata(item));
        }
        imejiBean2RDF.update(imBeans, user);
    }

    /**
     * Initialize the fulltext search value for all {@link Metadata} of an {@link Item}
     * 
     * @param item
     * @return
     */
    private Item createFulltextForMetadata(Item item)
    {
        for (MetadataSet mds : item.getMetadataSets())
        {
            for (Metadata md : mds.getMetadata())
            {
                md.asFulltext();
            }
        }
        return item;
    }

    /**
     * User ObjectLoader to load image
     * 
     * @param imgUri
     * @return
     * @throws Exception
     */
    public Item retrieve(URI imgUri) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.imageModel);
        return (Item)imejiRDF2Bean.load(imgUri.toString(), user, new Item());
    }

    /**
     * Retrieve all {@link Item} (all status, all users) in imeji
     * 
     * @return
     */
    public Collection<Item> retrieveAll()
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.imageModel);
        List<String> uris = ImejiSPARQL.exec(SPARQLQueries.selectItemAll(), ImejiJena.imageModel);
        return loadItems(uris, -1, 0);
    }

    /**
     * Search
     * 
     * @param containerUri
     * @param searchQuery
     * @param sortCri
     * @return
     */
    public SearchResult searchItemInContainer(URI containerUri, SearchQuery searchQuery, SortCriterion sortCri)
    {
        Search search = new Search(SearchType.ITEM, containerUri.toString());
        return search.search(searchQuery, sortCri, simplifyUser(containerUri));
    }

    /**
     * Search {@link Item}
     * 
     * @param containerUri - if the search is done within a {@link Container}
     * @param searchQuery - the {@link SearchQuery}
     * @param sortCri - the {@link SortCriterion}
     * @param uris - The {@link List} of uri to restrict the search
     * @return
     */
    public SearchResult search(URI containerUri, SearchQuery searchQuery, SortCriterion sortCri, List<String> uris)
    {
        String uriString = null;
        if (containerUri != null)
            uriString = containerUri.toString();
        Search search = new Search(SearchType.ITEM, uriString);
        return search.search(uris, searchQuery, sortCri, simplifyUser(containerUri));
    }

    /**
     * Load the {@link List} of {@link Item}
     * 
     * @param uris
     * @param limit
     * @param offset
     * @return
     */
    public Collection<Item> loadItems(List<String> uris, int limit, int offset)
    {
        int counter = 0;
        List<Item> items = new ArrayList<Item>();
        for (String s : uris)
        {
            if (offset <= counter && (counter < (limit + offset) || limit == -1))
            {
                items.add((Item)J2JHelper.setId(new Item(), URI.create(s)));
            }
            counter++;
        }
        try
        {
            imejiRDF2Bean.load(J2JHelper.cast2ObjectList(items), user);
            return items;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error loading images:", e);
        }
    }

    /**
     * Delete a {@link List} of {@link Item} inclusive all files stored in the {@link Storage}
     * 
     * @param items
     * @param user
     * @return
     * @throws Exception
     */
    public int delete(List<Item> items, User user) throws Exception
    {
        int count = 0;
        Map<String, URI> cMap = new HashMap<String, URI>();
        List<Object> toDelete = new ArrayList<Object>();
        for (Item item : items)
        {
            if (item != null)
            {
                removeFileFromStorage(item.getStorageId());
                toDelete.add(item);
                count++;
                cMap.put(item.getCollection().toString(), item.getCollection());
            }
        }
        imejiBean2RDF.delete(toDelete, user);
        // Remove items from their collections
        for (URI uri : cMap.values())
        {
            CollectionController cc = new CollectionController(user);
            CollectionImeji c = cc.retrieveLazy(uri);
            c = (CollectionImeji)loadContainerItems(c, user, -1, 0);
            cc.update(c);
        }
        return count;
    }

    /**
     * Set the status of a {@link List} of {@link Item} to released
     * 
     * @param l
     * @param user
     * @throws Exception
     */
    public void release(List<Item> l, User user) throws Exception
    {
        for (Item item : l)
        {
            if (Status.PENDING.equals(item.getStatus()))
            {
                writeReleaseProperty(item, user);
                item.setVisibility(Visibility.PUBLIC);
            }
        }
        update(l);
    }

    /**
     * Set the status of a {@link List} of {@link Item} to withdraw and delete its files from the {@link Storage}
     * 
     * @param items
     * @param comment
     * @throws Exception
     */
    public void withdraw(List<Item> items, String comment) throws Exception
    {
        Map<String, URI> cMap = new HashMap<String, URI>();
        for (Item item : items)
        {
            if (!item.getStatus().equals(Status.RELEASED))
            {
                throw new RuntimeException("Error discard " + item.getId() + " must be release (found: "
                        + item.getStatus() + ")");
            }
            else
            {
                writeWithdrawProperties(item, comment);
                item.setVisibility(Visibility.PUBLIC);
                if (item.getEscidocId() != null)
                {
                    removeFileFromStorage(item.getStorageId());
                    item.setEscidocId(null);
                }
            }
        }
        update(items);
        // Remove items from their collections
        for (URI uri : cMap.values())
        {
            CollectionController cc = new CollectionController(user);
            CollectionImeji c = cc.retrieveLazy(uri);
            c = (CollectionImeji)loadContainerItems(c, user, -1, 0);
            cc.update(c);
        }
    }

    /**
     * Remove a file from the current {@link Storage}
     * 
     * @param id
     */
    private void removeFileFromStorage(String id)
    {
        StorageController storageController = new StorageController();
        storageController.delete(id);
    }

    /**
     * Increase performance by restricting grants to the only grants needed
     * 
     * @param user
     * @return
     */
    private User simplifyUser(URI containerUri)
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
            else if (containerUri != null && containerUri.toString().contains("collection")
                    && containerUri.toString().equals(g.getGrantFor().toString()))
            {
                simplifiedUser.getGrants().add(g);
            }
            else if (containerUri != null && containerUri.toString().contains("album")
                    && g.getGrantFor().toString().contains("collection"))
            {
                simplifiedUser.getGrants().add(g);
            }
            else if (containerUri == null && g.getGrantFor() != null
                    && g.getGrantFor().toString().contains("collection"))
            {
                simplifiedUser.getGrants().add(g);
            }
            else
            {
                // simplifiedUser.getGrants().add(g);
            }
        }
        return simplifiedUser;
    }
}
