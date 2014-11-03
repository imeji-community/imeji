/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.admin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Resource;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.UploadResult;
import de.mpg.imeji.logic.storage.administrator.StorageAdministrator;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.j2j.annotations.j2jId;

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
     * Import the files in an external storage (for instance escidoc) into the internal storage
     * 
     * @throws Exception
     */
    public String importToInternalStorage() throws Exception
    {
        StorageController internal = new StorageController("internal");
        StorageController escidoc = new StorageController("escidoc");
        ItemController ic = new ItemController();
        for (Item item : ic.retrieveAll(sb.getUser()))
        {
            File tmp = null;
            try
            {
                // Get escidoc url for all files
                URI escidocUrl = item.getFullImageUrl();
                logger.info("Importing file " + escidocUrl + " for item " + item.getId());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // Read the file in a stream
                escidoc.read(escidocUrl.toString(), out, true);
                // Upload the file in the internal storage
                if (out.toByteArray() != null)
                {
                    tmp = File.createTempFile("import", FilenameUtils.getExtension(item.getFilename()));
                    FileUtils.writeByteArrayToFile(tmp, out.toByteArray());
                    UploadResult result = internal.upload(item.getFilename(), tmp,
                            ObjectHelper.getId(item.getCollection()));
                    FileUtils.deleteQuietly(tmp);
                    item.setChecksum(result.getChecksum());
                    item.setFullImageUrl(URI.create(result.getOrginal()));
                    item.setWebImageUrl(URI.create(result.getWeb()));
                    item.setThumbnailImageUrl(URI.create(result.getThumb()));
                    item.setStorageId(result.getId());
                    item.setFiletype(item.getFiletype());
                    // Update the item with the new values
                    ic.update(item, sb.getUser());
                }
                else
                {
                    logger.error("File not found: " + escidocUrl + " for item " + item.getId());
                }
            }
            catch (Exception e)
            {
                logger.error("Error importing item " + item.getId(), e);
            }
            finally
            {
                FileUtils.deleteQuietly(tmp);
            }
        }
        return "";
    }

    /**
     * Clean the {@link Storage}
     * 
     * @return
     */
    public String cleanStorage()
    {
        StorageController controller = new StorageController();
        controller.getAdministrator().clean();
        return "pretty:";
    }

    /**
     * Return the location of the internal storage
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public String getInternalStorageLocation() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("imeji.storage.path");
    }

    /**
     * Initialize the full text index for all elements
     * 
     * @throws Exception
     */
    public void reIndex() throws Exception
    {
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
     * Clean {@link Statement} which are not bound a {@link MetadataProfile}
     * 
     * @throws Exception
     */
    private void cleanStatement() throws Exception
    {
        logger.info("Searching not bounded statement...");
        Search search = SearchFactory.create();
        List<String> uris = search.searchSimpleForQuery(SPARQLQueries.selectStatementUnbounded()).getResults();
        logger.info("...found " + uris.size());
        removeResources(uris, Imeji.profileModel, new Statement());
    }

    /**
     * Clean grants which are not related to a user
     * 
     * @throws Exception
     */
    private void cleanGrants() throws Exception
    {
        logger.info("Searching not bounded grants...");
        Search search = SearchFactory.create();
        List<String> uris = search.searchSimpleForQuery(SPARQLQueries.selectGrantWithoutUser()).getResults();
        logger.info("...found " + uris.size());
        System.out.println(uris.size());
        removeResources(uris, Imeji.userModel, new Grant());
        logger.info("Searching broken grants...");
        uris = search.searchSimpleForQuery(SPARQLQueries.selectGrantBroken()).getResults();
        logger.info("...found " + uris.size());
        System.out.println(uris.size());
        removeResources(uris, Imeji.userModel, new Grant());
        logger.info("Searching emtpy grants...");
        if (clean)
            ImejiSPARQL.execUpdate(SPARQLQueries.removeGrantEmtpy());
        uris = search.searchSimpleForQuery(SPARQLQueries.selectGrantEmtpy()).getResults();
        logger.info("...found " + uris.size());
        System.out.println(uris.size());
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
        ReaderFacade reader = new ReaderFacade(modelName);
        List<Object> l = new ArrayList<Object>();
        for (String uri : uris)
        {
            try
            {
                logger.info("Resource to be removed: " + uri);
                l.add(reader.read(uri, sb.getUser(), obj.getClass().newInstance()));
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
            WriterFacade writer = new WriterFacade(modelName);
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
        Search search = SearchFactory.create(SearchType.ALBUM);
        return search.searchSimpleForQuery(SPARQLQueries.selectAlbumAll()).getNumberOfRecords();
    }

    /**
     * return count of all {@link CollectionImeji}
     * 
     * @return
     */
    public int getAllCollectionsSize()
    {
        Search search = SearchFactory.create(SearchType.COLLECTION);
        return search.searchSimpleForQuery(SPARQLQueries.selectCollectionAll()).getNumberOfRecords();
    }

    /**
     * return count of all {@link Item}
     * 
     * @return
     */
    public int getAllImagesSize()
    {
        Search search = SearchFactory.create(SearchType.ITEM);
        return search.searchSimpleForQuery(SPARQLQueries.selectItemAll()).getNumberOfRecords();
    }

    /**
     * True if the current {@link Storage} has implemted a {@link StorageAdministrator}
     * 
     * @return
     */
    public boolean isAdministrate()
    {
        StorageController sc = new StorageController();
        return sc.getAdministrator() != null;
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
        UserController uc = new UserController(Imeji.adminUser);
        return (List<User>)uc.retrieveAll("");
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
