/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ItemHandlerClient;
import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.ingest.vo.Items;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.LoginHelper;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.j2j.helper.J2JHelper;

public class ItemController extends ImejiController
{
    private static Logger logger = null;
    private static ImejiRDF2Bean imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.imageModel);
    private static ImejiBean2RDF imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);

    public ItemController(User user)
    {
        super(user);
        logger = Logger.getLogger(ItemController.class);
    }

    public void create(Item img, URI coll) throws Exception
    {
        CollectionController cc = new CollectionController(user);
        CollectionImeji ic = cc.retrieve(coll);
        writeCreateProperties(img, user);
        if (Status.PENDING.equals(ic.getStatus()))
            img.setVisibility(Visibility.PRIVATE);
        else
            img.setVisibility(Visibility.PUBLIC);
        img.setCollection(coll);
        img.getMetadataSet().setProfile(ic.getProfile());
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
        imejiBean2RDF.create(imejiBean2RDF.toList(img), user);
        ic.getImages().add(img.getId());
        cc.update(ic);
    }

    public void create(Collection<Item> items, URI coll) throws Exception
    {
        CollectionController cc = new CollectionController(user);
        CollectionImeji ic = cc.retrieve(coll);
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
        for (Item img : items)
        {
            writeCreateProperties(img, user);
            if (Status.PENDING.equals(ic.getStatus()))
                img.setVisibility(Visibility.PRIVATE);
            else
                img.setVisibility(Visibility.PUBLIC);
            img.setCollection(coll);
            img.getMetadataSet().setProfile(ic.getProfile());
            // imejiBean2RDF.create(imejiBean2RDF.toList(img), user);
            ic.getImages().add(img.getId());
        }
        imejiBean2RDF.create(J2JHelper.cast2ObjectList((List<?>)items), user);
        cc.update(ic);
    }

    public void update(Item img) throws Exception
    {
        Collection<Item> im = new ArrayList<Item>();
        im.add(img);
        update(im);
    }

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

    public Collection<Item> retrieveAll()
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.imageModel);
        List<String> uris = ImejiSPARQL.exec("SELECT ?s WHERE { ?s a <http://imeji.org/terms/item>}",
                ImejiJena.imageModel);
        return loadItems(uris, -1, 0);
    }

    /**
     * Get the number of all images
     * 
     * @return
     */
    public int allImagesSize()
    {
        return ImejiSPARQL.execCount("SELECT count(DISTINCT ?s) WHERE { ?s a <http://imeji.org/terms/item>}",
                ImejiJena.imageModel);
    }

    public SearchResult searchImages(SearchQuery searchQuery, SortCriterion sortCri)
    {
        Search search = new Search(SearchType.ITEM, null);
        return search.search(searchQuery, sortCri, simplifyUser(null));
    }

    public SearchResult searchImagesInContainer(URI containerUri, SearchQuery searchQuery, SortCriterion sortCri,
            int limit, int offset)
    {
        Search search = new Search(SearchType.ITEM, containerUri.toString());
        return search.search(searchQuery, sortCri, simplifyUser(containerUri));
    }

    public int countImages(SearchQuery searchQuery)
    {
        Search search = new Search(SearchType.ITEM, null);
        return search.search(searchQuery, null, simplifyUser(null)).getNumberOfRecords();
    }

    public int countImages(SearchQuery searchQuery, List<String> allImages)
    {
        Search search = new Search(SearchType.ITEM, null);
        return search.search(allImages, searchQuery, null, simplifyUser(null)).getNumberOfRecords();
    }

    public int countImagesInContainer(URI containerUri, SearchQuery searchQuery)
    {
        Search search = new Search(SearchType.ITEM, containerUri.toString());
        int size = 0;
        if (searchQuery.isEmpty())
        {
            size = search.searchSimpleForQuery(
                    "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT ?s WHERE { ?s <http://imeji.org/terms/collection> <"
                            + containerUri + "> . ?s <http://imeji.org/terms/status> ?status   .FILTER(?status!=<"
                            + Status.WITHDRAWN.getUri() + ">) }", new SortCriterion()).size();
        }
        else
        {
            size = search.search(searchQuery, new SortCriterion(), simplifyUser(containerUri)).getNumberOfRecords();
        }
        return size;
    }

    public int countImagesInContainer(URI containerUri, SearchQuery searchQuery, List<String> containerImages)
    {
        Search search = new Search(SearchType.ITEM, containerUri.toString());
        return search.search(containerImages, searchQuery, new SortCriterion(), simplifyUser(containerUri))
                .getNumberOfRecords();
    }

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
     * Increase performance by restricting grants to the only grants needed
     * 
     * @param user
     * @return
     */
    public User simplifyUser(URI containerUri)
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

    public int delete(List<Item> items, User user) throws Exception
    {
        int count = 0;
        Map<String, URI> cMap = new HashMap<String, URI>();
        List<Object> toDelete = new ArrayList<Object>();
        for (Item item : items)
        {
            if (item != null)
            {
                removeImageFromEscidoc(item.getEscidocId());
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
                    removeImageFromEscidoc(item.getEscidocId());
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

    public void removeImageFromEscidoc(String id)
    {
        try
        {
            String username = PropertyReader.getProperty("imeji.escidoc.user");
            String password = PropertyReader.getProperty("imeji.escidoc.password");
            Authentication auth = new Authentication(new URL(
                    PropertyReader.getProperty("escidoc.framework_access.framework.url")), username, password);
            ItemHandlerClient handler = new ItemHandlerClient(auth.getServiceAddress());
            handler.setHandle(auth.getHandle());
            handler.delete(id);
        }
        catch (Exception e)
        {
            logger.error("Error removing image from eSciDoc (" + id + ")", e);
            throw new RuntimeException("Error removing image from eSciDoc (" + id + ")", e);
        }
    }

}
