/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Resource;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.helper.SortHelper;

/**
 * Bean for the administration page. Methods working on data
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AdminBean
{
    private SessionBean sb;
    private static Logger logger = Logger.getLogger(AdminBean.class);
    private boolean clean = false;

    public AdminBean()
    {
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    /**
     * Initialize the full text index for all elements
     * 
     * @throws Exception
     */
    public void reIndex() throws Exception
    {
        List<Object> toReindex = new ArrayList<Object>();
        ImejiBean2RDF imejiBean2RDF;
        // load items
        ItemController ic = new ItemController();
        for (Item item : ic.retrieveAll())
        {
            item.indexFulltext();
            toReindex.add(item);
        }
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
        imejiBean2RDF.updateLazy(toReindex, sb.getUser());
        // Load collections
        toReindex = new ArrayList<Object>();
        CollectionController cc = new CollectionController();
        for (CollectionImeji c : cc.retrieveAllCollections())
        {
            c.indexFulltext();
            toReindex.add(c);
        }
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
        imejiBean2RDF.updateLazy(toReindex, sb.getUser());
        // Load albums
        toReindex = new ArrayList<Object>();
        AlbumController ac = new AlbumController();
        for (Album a : ac.retrieveAll())
        {
            a.indexFulltext();
            toReindex.add(a);
        }
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
        imejiBean2RDF.updateLazy(toReindex, sb.getUser());
    }

    /**
     * Make the same as clean, but doesn't remove the resources
     * 
     * @throws Exception
     */
    public void status() throws Exception
    {
        clean = false;
        invokeCleanMethods();
    }

    /**
     * Here are called all methods related to data cleaning
     * 
     * @throws Exception
     */
    public void clean() throws Exception
    {
        clean = true;
        invokeCleanMethods();
        reIndex();
    }

    /**
     * Invoke all clean methods available
     * 
     * @throws Exception
     */
    private void invokeCleanMethods() throws Exception
    {
        cleanStatement();
        /*
         * TODO Clean Metadata not working: the metadata is not completely removed. All element in the metadata are
         * removed, but the metadata it self not. Since a metadata is a abstract class, j2j can not instance a new
         * metadata since it doesn't know the type
         */
        // cleanMetadata();
        cleanGrants();
    }

    /**
     * Clean {@link Metadata} which are not attached to a {@link Statement} .NOT WORKING SO FAR
     * 
     * @throws Exception
     */
    private void cleanMetadata() throws Exception
    {
        logger.info("Searching not bounded metadata...");
        // Don't use search to be abble to get the type of the metadata defined as sort0 in the query
        List<String> uris = ImejiSPARQL.exec(SPARQLQueries.selectMetadataUnbounded(), null);
        for (String uri : uris)
        {
            // Split the metadata uri and the metatata type defined as sort parameter
            String[] s = SortHelper.SORT_VALUE_PATTERN.split(uri);
            List<String> l = new ArrayList<String>();
            l.add(s[0]);
            removeResources(l, ImejiJena.imageModel, MetadataFactory.createMetadata(s[1]));
        }
        logger.info("...found " + uris.size());
    }

    /**
     * Clean {@link Statement} which are not bound a {@link MetadataProfile}
     * 
     * @throws Exception
     */
    private void cleanStatement() throws Exception
    {
        logger.info("Searching not bounded statement...");
        Search search = new Search(SearchType.ALL, null);
        List<String> uris = search.searchSimpleForQuery(SPARQLQueries.selectStatementUnbounded(), null);
        logger.info("...found " + uris.size());
        removeResources(uris, ImejiJena.profileModel, new Statement());
    }

    /**
     * Clean grants which are not related to a user
     * 
     * @throws Exception
     */
    private void cleanGrants() throws Exception
    {
        logger.info("Searching not bounded grants...");
        Search search = new Search(SearchType.ALL, null);
        List<String> uris = search.searchSimpleForQuery(SPARQLQueries.selectGrantUnbounded(), null);
        logger.info("...found " + uris.size());
        removeResources(uris, ImejiJena.userModel, new Grant());
    }

    /**
     * Remove Exception a {@link List} of {@link Resource}
     * 
     * @param uris
     * @param modelName
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws Exception
     */
    private synchronized void removeResources(List<String> uris, String modelName, Object obj)
            throws InstantiationException, IllegalAccessException, Exception
    {
        removeObjects(loadResourcesAsObjects(uris, modelName, obj), modelName);
    }

    /**
     * Load the {@link Resource} as {@link Object}
     * 
     * @param uris
     * @param modelName
     * @param obj
     * @return
     */
    private List<Object> loadResourcesAsObjects(List<String> uris, String modelName, Object obj)
    {
        ImejiRDF2Bean reader = new ImejiRDF2Bean(modelName);
        List<Object> l = new ArrayList<Object>();
        for (String uri : uris)
        {
            try
            {
                logger.info("Resource to be removed: " + uri);
                l.add(reader.load(uri, sb.getUser(), obj.getClass().newInstance()));
            }
            catch (Exception e)
            {
                logger.error("ERROR LOADING RESOURCE " + uri + " !!!!!", e);
            }
        }
        return l;
    }

    /**
     * Remove an {@link Object}, it must have a {@link j2jId}
     * 
     * @param l
     * @param modelName
     * @throws Exception
     */
    private void removeObjects(List<Object> l, String modelName) throws Exception
    {
        if (clean)
        {
            ImejiBean2RDF writer = new ImejiBean2RDF(modelName);
            writer.delete(l, sb.getUser());
        }
    }

    /**
     * return count of all {@link Album}
     * 
     * @return
     */
    public int getAllAlbumsSize()
    {
        Search search = new Search(SearchType.ALBUM, null);
        return search.searchSimpleForQuery(SPARQLQueries.selectAlbumAll(), null).size();
    }

    /**
     * return count of all {@link CollectionImeji}
     * 
     * @return
     */
    public int getAllCollectionsSize()
    {
        Search search = new Search(SearchType.COLLECTION, null);
        return search.searchSimpleForQuery(SPARQLQueries.selectCollectionAll(), null).size();
    }

    /**
     * return count of all {@link Item}
     * 
     * @return
     */
    public int getAllImagesSize()
    {
        Search search = new Search(SearchType.ITEM, null);
        return search.searchSimpleForQuery(SPARQLQueries.selectItemAll(), null).size();
    }

    /**
     * Return the number of files stored in the {@link Storage}
     * 
     * @return
     */
    public int getNumberOfFiles()
    {
        StorageController sc = new StorageController();
        return (int)sc.getAdministrator().getNumberOfFiles();
    }

    /**
     * Return the free space in the {@link Storage} in bytes
     * 
     * @return
     */
    public long getStorageFreeSpace()
    {
        StorageController sc = new StorageController();
        return sc.getAdministrator().getFreeSpace();
    }

    /**
     * Return the Size in bytes of all files in the {@link Storage}
     * 
     * @return
     */
    public long getSizeOfFiles()
    {
        StorageController sc = new StorageController();
        return sc.getAdministrator().getSizeOfFiles();
    }

    /**
     * Return all {@link User}
     * 
     * @return
     */
    public List<User> getAllUsers()
    {
        UserController uc = new UserController();
        return (List<User>)uc.retrieveAll();
    }

    /**
     * return count of all {@link User}
     * 
     * @return
     */
    public int getAllUsersSize()
    {
        try
        {
            return this.getAllUsers().size();
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}
