/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;

import com.ocpsoft.pretty.PrettyContext;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.UploadResult;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.history.PageURIHelper;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Bean for the upload page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "UploadBean")
@ViewScoped
public class UploadBean implements Serializable
{
    private static final long serialVersionUID = -2731118794797476328L;
    private static Logger logger = Logger.getLogger(UploadBean.class);
    private CollectionImeji collection = new CollectionImeji();
    private StorageController storageController;
    private int collectionSize = 0;
    private String id;
    private String localDirectory = null;
    private String externalUrl;
    @ManagedProperty(value = "#{SessionBean.user}")
    private User user;
    @ManagedProperty(value = "#{UploadSession.formatBlackList}")
    private String formatBlackList = "";
    @ManagedProperty(value = "#{UploadSession.formatWhiteList}")
    private String formatWhiteList = "";
    private boolean recursive;

    /**
     * Construct the Bean and initalize the pages
     * 
     * @throws URISyntaxException
     * @throws IOException
     */
    public UploadBean() throws IOException, URISyntaxException
    {
        storageController = new StorageController();
    }

    /**
     * Method checking the url parameters and triggering then the {@link UploadBean} methods
     */
    @PostConstruct
    public void status()
    {
        readId();
        loadCollection();
        if (UrlHelper.getParameterBoolean("init"))
        {
            ((UploadSession)BeanHelper.getSessionBean(UploadSession.class)).reset();
            externalUrl = null;
            localDirectory = null;
        }
        else if (UrlHelper.getParameterBoolean("start"))
        {
            upload();
        }
        else if (UrlHelper.getParameterBoolean("done"))
        {
            // do nothing
        }
    }

    /**
     * Read the id of the collection from the url
     */
    private void readId()
    {
        URI uri = PageURIHelper.extractId(PrettyContext.getCurrentInstance().getRequestURL().toString());
        if (uri != null)
            this.id = ObjectHelper.getId(uri);
    }

    /**
     * Start the Upload of the items
     * 
     * @throws Exception
     */
    public void upload()
    {
        HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (isMultipart)
        {
            List<Item> itemList = new ArrayList<Item>();
            ServletFileUpload upload = new ServletFileUpload();
            // Parse the request
            try
            {
                FileItemIterator iter = upload.getItemIterator(req);
                while (iter.hasNext())
                {
                    FileItemStream fis = iter.next();
                    InputStream stream = fis.openStream();
                    if (!fis.isFormField())
                    {
                        File tmp = createTmpFile(fis.getName());
                        try
                        {
                            writeInTmpFile(tmp, stream);
                            Item item = uploadFile(tmp, fis.getName());
                            if (item != null)
                                itemList.add(item);
                        }
                        finally
                        {
                            stream.close();
                            FileUtils.deleteQuietly(tmp);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            createOrUpdateItem(itemList);
        }
    }

    /**
     * Upload all Files from a directory
     * 
     * @param path
     * @throws Exception
     */
    public void uploadFromLocalDirectory() throws Exception
    {
        System.out.println(recursive);
        try
        {
            File dir = new File(localDirectory);
            List<Item> itemList = new ArrayList<>();
            if (dir.isDirectory())
            {
                for (File f : FileUtils.listFiles(dir, null, recursive))
                {
                    Item item = uploadFile(f, f.getName());
                    if (item != null)
                        itemList.add(item);
                }
                createOrUpdateItem(itemList);
            }
            BeanHelper.info(itemList.size() + " files uploaded from " + localDirectory);
        }
        finally
        {
            reloadToDonePage();
        }
    }

    /**
     * Upload a file from the web
     * 
     * @return
     * @throws Exception
     */
    public void uploadFromLink() throws Exception
    {
        URL url = new URL(URLDecoder.decode(externalUrl, "UTF-8"));
        try
        {
            File tmp = createTmpFile(findFileName(url));
            try
            {
                StorageController externalController = new StorageController("external");
                FileOutputStream fos = new FileOutputStream(tmp);
                externalController.read(url.toString(), fos, true);
                Item item = uploadFile(tmp, findFileName(url));
                if (item != null)
                {
                    List<Item> itemList = new ArrayList<>();
                    itemList.add(item);
                    createOrUpdateItem(itemList);
                }
                externalUrl = null;
            }
            catch (Exception e)
            {
                logger.error("Error uploading file from link: " + externalUrl, e);
                getfFiles().add(e.getMessage() + ": " + findFileName(url));
            }
            finally
            {
                FileUtils.deleteQuietly(tmp);
            }
        }
        finally
        {
            reloadToDonePage();
        }
    }

    /**
     * According to choosed options, create or update the item
     * 
     * @param itemList
     */
    private void createOrUpdateItem(List<Item> itemList)
    {
        if (isImportImageToFile() || isUploadFileToItem())
            updateItemForFiles(itemList);
        else
            createItemForFiles(itemList);
    }

    /**
     * Reload the page with the done status
     * 
     * @throws IOException
     */
    private void reloadToDonePage() throws IOException
    {
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        FacesContext.getCurrentInstance().getExternalContext()
                .redirect(navigation.getCollectionUrl() + getId() + "/" + navigation.getUploadPath() + "?done=1");
    }

    /**
     * Find in the url the filename
     * 
     * @param url
     * @return
     */
    private String findFileName(URL url)
    {
        String name = FilenameUtils.getName(url.getPath());
        if (isWellFormedFileName(name))
            return name;
        name = FilenameUtils.getName(url.toString());
        if (isWellFormedFileName(name))
            return name;
        return FilenameUtils.getName(url.getPath());
    }

    /**
     * true if the filename is well formed, i.e. has an extension
     * 
     * @param filename
     * @return
     */
    private boolean isWellFormedFileName(String filename)
    {
        return FilenameUtils.wildcardMatch(filename, "*.???") || FilenameUtils.wildcardMatch(filename, "*.??")
                || FilenameUtils.wildcardMatch(filename, "*.?");
    }

    /**
     * Create a tmp file with the uploaded file
     * 
     * @param fio
     * @return
     */
    private File createTmpFile(String title)
    {
        try
        {
            return File.createTempFile("upload", "." + FilenameUtils.getExtension(title));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error creating a temp file", e);
        }
    }

    /**
     * Write an {@link InputStream} in a {@link File}
     * 
     * @param tmp
     * @param fis
     * @return
     * @throws IOException
     */
    private File writeInTmpFile(File tmp, InputStream fis) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(tmp);
        try
        {
            StorageUtils.writeInOut(fis, fos, true);
            return tmp;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error writing uploaded File in temp file", e);
        }
        finally
        {
            fos.close();
            fis.close();
        }
    }

    /**
     * Throws an {@link Exception} if the file ca be upload. Works only if the file has an extension (therefore, for
     * file without extension, the validation will only occur when the file has been stored locally)
     */
    private void validateName(String title)
    {
        if (StorageUtils.hasExtension(title))
        {
            if (isCheckNameUnique())
            {
                // if the checkNameUnique is checked, check that two files with the same name is not possible
                if (!((isImportImageToFile() || isUploadFileToItem())) && filenameExistsInCollection(title))
                    throw new RuntimeException("There is already at least one item with the filename "
                            + FilenameUtils.getBaseName(title));
            }
            if (!isAllowedFormat(FilenameUtils.getExtension(title)))
            {
                SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
                throw new RuntimeException(sessionBean.getMessage("upload_format_not_allowed") + " ("
                        + FilenameUtils.getExtension(title) + ")");
            }
        }
    }

    /**
     * Upload one File and create the {@link de.mpg.imeji.logic.vo.Item}
     * 
     * @param bytes
     * @throws Exception
     */
    private Item uploadFile(File file, String title)
    {
        try
        {
            if (!StorageUtils.hasExtension(title))
                title += StorageUtils.guessExtension(file);
            validateName(title);
            storageController = new StorageController();
            Item item = null;
            if (isImportImageToFile())
            {
                item = replaceWebResolutionAndThumbnailOfItem(findItemByFileName(title), file);
            }
            else if (isUploadFileToItem())
            {
                item = replaceFileOfItem(findItemByFileName(title), file);
            }
            else
            {
                MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
                String mimeType = mimeTypesMap.getContentType(file);
                UploadResult uploadResult = storageController.upload(title, file, id);
                item = ImejiFactory.newItem(collection, user, uploadResult.getId(), title,
                        URI.create(uploadResult.getOrginal()), URI.create(uploadResult.getThumb()),
                        URI.create(uploadResult.getWeb()), mimeType);
                item.setChecksum(uploadResult.getChecksum());
            }
            getsFiles().add(item);
            return item;
        }
        catch (Exception e)
        {
            getfFiles().add(" File " + title + " not uploaded: " + e.getMessage());
            logger.error("Error uploading item: ", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Replace the web resolution and thumbnail files with the one the the upload result
     * 
     * @param item
     * @param uploadResult
     * @return
     * @throws Exception
     */
    private Item replaceWebResolutionAndThumbnailOfItem(Item item, File file) throws Exception
    {
        storageController.update(item.getWebImageUrl().toString(), file);
        storageController.update(item.getThumbnailImageUrl().toString(), file);
        return item;
    }

    /**
     * Replace the File of an Item
     * 
     * @param item
     * @param uploadResult
     * @return
     * @throws Exception
     */
    private Item replaceFileOfItem(Item item, File file) throws Exception
    {
        storageController.update(item.getFullImageUrl().toString(), file);
        item.setChecksum(storageController.calculateChecksum(file));
        replaceWebResolutionAndThumbnailOfItem(item, file);
        return item;
    }

    /**
     * Search for an item in the current collection with the same filename. The filename must be unique!
     * 
     * @param filename
     * @return
     */
    private Item findItemByFileName(String filename)
    {
        Search s = new Search(SearchType.ITEM, null);
        List<String> sr = s.searchSimpleForQuery(
                SPARQLQueries.selectContainerItemByFilename(collection.getId(), FilenameUtils.getBaseName(filename)),
                null);
        if (sr.size() == 0)
            throw new RuntimeException("No item found with the filename " + FilenameUtils.getBaseName(filename));
        if (sr.size() > 1)
            throw new RuntimeException("Filename " + FilenameUtils.getBaseName(filename) + " not unique (" + sr.size()
                    + " found).");
        return ObjectLoader.loadItem(URI.create(sr.get(0)), user);
    }

    /**
     * True if the filename is already used by an {@link Item} in this {@link CollectionImeji}
     * 
     * @param filename
     * @return
     */
    private boolean filenameExistsInCollection(String filename)
    {
        Search s = new Search(SearchType.ITEM, null);
        return s.searchSimpleForQuery(
                SPARQLQueries.selectContainerItemByFilename(collection.getId(), FilenameUtils.getBaseName(filename)),
                null).size() > 0;
    }

    /**
     * Create the {@link Item} for the files which have been uploaded
     */
    private void createItemForFiles(Collection<Item> itemList)
    {
        ItemController ic = new ItemController(user);
        try
        {
            ic.create(itemList, collection.getId());
        }
        catch (Exception e)
        {
            logger.error("Error creating files for upload", e);
        }
    }

    /**
     * Update the {@link Item} for the files which have been uploaded
     */
    private void updateItemForFiles(Collection<Item> itemList)
    {
        ItemController ic = new ItemController(user);
        try
        {
            ic.update(itemList, user);
        }
        catch (Exception e)
        {
            logger.error("Error creating files for upload", e);
        }
    }

    /**
     * Load the collection
     */
    public void loadCollection()
    {
        if (id != null)
        {
            collection = ObjectLoader.loadCollectionLazy(ObjectHelper.getURI(CollectionImeji.class, id), user);
            if (collection != null && getCollection().getId() != null)
            {
                ItemController ic = new ItemController(user);
                collectionSize = ic.countContainerSize(collection);
            }
        }
        else
        {
            SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
            BeanHelper.error(sessionBean.getLabel("error") + "No ID in URL");
        }
    }

    /**
     * True if the file format related to the passed extension can be download
     * 
     * @param extension
     * @return
     */
    private boolean isAllowedFormat(String extension)
    {
        // If no extension, not possible to recognized the format
        if ("".equals(extension.trim()))
            return false;
        // check in white list, if found then allowed
        for (String s : formatWhiteList.split(","))
            if (StorageUtils.compareExtension(extension, s.trim()))
                return true;
        // check black list, if found then forbidden
        for (String s : formatBlackList.split(","))
            if (StorageUtils.compareExtension(extension, s.trim()))
                return false;
        // Not found in both list: if white list is empty, allowed
        return "".equals(formatWhiteList.trim());
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public int getCollectionSize()
    {
        return collectionSize;
    }

    public void setCollectionSize(int collectionSize)
    {
        this.collectionSize = collectionSize;
    }

    public String getExternalUrl()
    {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl)
    {
        this.externalUrl = externalUrl;
    }

    /**
     * @return the formatBlackList
     */
    public String getFormatBlackList()
    {
        return formatBlackList;
    }

    /**
     * @param formatBlackList the formatBlackList to set
     */
    public void setFormatBlackList(String formatBlackList)
    {
        this.formatBlackList = formatBlackList;
    }

    /**
     * @return the formatWhiteList
     */
    public String getFormatWhiteList()
    {
        return formatWhiteList;
    }

    /**
     * @param formatWhiteList the formatWhiteList to set
     */
    public void setFormatWhiteList(String formatWhiteList)
    {
        this.formatWhiteList = formatWhiteList;
    }

    private List<String> getfFiles()
    {
        return ((UploadSession)BeanHelper.getSessionBean(UploadSession.class)).getfFiles();
    }

    private List<Item> getsFiles()
    {
        return ((UploadSession)BeanHelper.getSessionBean(UploadSession.class)).getsFiles();
    }

    private boolean isCheckNameUnique()
    {
        return ((UploadSession)BeanHelper.getSessionBean(UploadSession.class)).isCheckNameUnique();
    }

    private boolean isImportImageToFile()
    {
        return ((UploadSession)BeanHelper.getSessionBean(UploadSession.class)).isImportImageToFile();
    }

    private boolean isUploadFileToItem()
    {
        return ((UploadSession)BeanHelper.getSessionBean(UploadSession.class)).isUploadFileToItem();
    }

    // public void uploadFileToItemListener()
    // {
    // this.importImageToFile = BooleanUtils.negate(importImageToFile);
    // }
    //
    // public void importImageToFileListener()
    // {
    // this.uploadFileToItem = BooleanUtils.negate(uploadFileToItem);
    // }
    //
    // public void checkNameUniqueListener()
    // {
    // this.checkNameUnique = BooleanUtils.negate(checkNameUnique);
    // }
    //
    // /**
    // * @return the importImageToFile
    // */
    // public boolean isImportImageToFile()
    // {
    // return importImageToFile;
    // }
    //
    // /**
    // * @param importImageToFile the importImageToFile to set
    // */
    // public void setImportImageToFile(boolean importImageToFile)
    // {
    // this.importImageToFile = importImageToFile;
    // }
    //
    // /**
    // * @return the uploadFileToItem
    // */
    // public boolean isUploadFileToItem()
    // {
    // return uploadFileToItem;
    // }
    //
    // /**
    // * @param uploadFileToItem the uploadFileToItem to set
    // */
    // public void setUploadFileToItem(boolean uploadFileToItem)
    // {
    // this.uploadFileToItem = uploadFileToItem;
    // }
    //
    // /**
    // * @return the checkNameUnique
    // */
    // public boolean isCheckNameUnique()
    // {
    // return checkNameUnique;
    // }
    //
    // /**
    // * @param checkNameUnique the checkNameUnique to set
    // */
    // public void setCheckNameUnique(boolean checkNameUnique)
    // {
    // this.checkNameUnique = checkNameUnique;
    // }
    //
    // public List<Item> getsFiles()
    // {
    // return sFiles;
    // }
    //
    // public void setsFiles(List<Item> sFiles)
    // {
    // this.sFiles = sFiles;
    // }
    //
    // public List<String> getfFiles()
    // {
    // return fFiles;
    // }
    //
    // public void setfFiles(List<String> fFiles)
    // {
    // this.fFiles = fFiles;
    // }
    public String getDiscardComment()
    {
        return collection.getDiscardComment();
    }

    public void setDiscardComment(String comment)
    {
        collection.setDiscardComment(comment);
    }

    /**
     * @return the localDirectory
     */
    public String getLocalDirectory()
    {
        return localDirectory;
    }

    /**
     * @param localDirectory the localDirectory to set
     */
    public void setLocalDirectory(String localDirectory)
    {
        this.localDirectory = localDirectory;
    }

    /**
     * @return the recursive
     */
    public boolean isRecursive()
    {
        return recursive;
    }

    /**
     * @param recursive the recursive to set
     */
    public void setRecursive(boolean recursive)
    {
        this.recursive = recursive;
    }
}
