/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
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
import de.mpg.imeji.presentation.collection.CollectionBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Bean for the upload page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UploadBean extends CollectionBean
{
    private CollectionImeji collection;
    private int collectionSize = 0;
    private String id;
    private User user;
    private String title;
    private String totalNum;
    private int sNum;
    private int fNum;
    private List<Item> sFiles;
    private List<String> fFiles;
    private String externalUrl;
    private StorageController storageController;
    private Collection<Item> itemList;
    private static Logger logger = Logger.getLogger(Upload.class);
    private String formatBlackList = "";
    private String formatWhiteList = "";
    private boolean importImageToFile = false;
    private boolean uploadFileToItem = false;
    private boolean checkNameUnique = true;
    private boolean uploadFinished = false;

    /**
     * Construct the Bean and initalize the pages
     * 
     * @throws URISyntaxException
     * @throws IOException
     */
    public UploadBean() throws IOException, URISyntaxException
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        storageController = new StorageController("internal");
        formatBlackList = PropertyReader.getProperty("imeji.upload.blacklist");
        formatWhiteList = PropertyReader.getProperty("imeji.upload.whitelist");
    }

    /**
     * Method checking the url parameters and triggering then the {@link UploadBean} methods
     */
    public void status()
    {
        if (uploadFinished)
        {
            uploadFinished = false;
            removeFiles();
            totalNum = "";
            sNum = 0;
            fNum = 0;
            sFiles = new ArrayList<Item>();
            fFiles = new ArrayList<String>();
        }
        if (UrlHelper.getParameterBoolean("init"))
        {
            importImageToFile = false;
            uploadFileToItem = false;
            checkNameUnique = true;
            removeFiles();
            loadCollection();
            totalNum = "";
            sNum = 0;
            fNum = 0;
            sFiles = new ArrayList<Item>();
            fFiles = new ArrayList<String>();
            externalUrl = "";
        }
        else if (UrlHelper.getParameterBoolean("start"))
        {
            try
            {
                upload();
            }
            catch (Exception e)
            {
                logger.error("Error upload", e);
            }
        }
        else if (UrlHelper.getParameterBoolean("done"))
        {
            try
            {
                if (importImageToFile || uploadFileToItem)
                    updateItemForFiles();
                else
                    createItemForFiles();
                totalNum = UrlHelper.getParameterValue("totalNum");
                loadCollection();
                report();
            }
            catch (Exception e)
            {
                logger.error("Error upload", e);
                e.printStackTrace();
            }
        }
        super.setCollection(collection);
    }

    /**
     * Start the Upload of the items
     * 
     * @throws Exception
     */
    public void upload() throws Exception
    {
        UserController uc = new UserController(sessionBean.getUser());
        user = uc.retrieve(getUser().getEmail());
        HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (isMultipart)
        {
            ServletFileUpload upload = new ServletFileUpload();
            // Parse the request
            FileItemIterator iter = upload.getItemIterator(req);
            while (iter.hasNext())
            {
                FileItemStream fis = iter.next();
                InputStream stream = fis.openStream();
                if (!fis.isFormField())
                {
                    title = fis.getName();
                    File tmp = createTmpFile();
                    try
                    {
                        writeInTmpFile(tmp, stream);
                        Item item = uploadFile(tmp);
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
    }

    /**
     * Upload a file from the web
     * 
     * @return
     */
    public String uploadFromLink()
    {
        try
        {
            externalUrl = URLDecoder.decode(externalUrl, "UTF-8");
            URL url = new URL(externalUrl);
            title = findFileName(url);
            File tmp = createTmpFile();
            try
            {
                StorageController externalController = new StorageController("external");
                FileOutputStream fos = new FileOutputStream(tmp);
                externalController.read(url.toString(), fos, true);
                Item item = uploadFile(tmp);
                if (item != null)
                {
                    UserController uc = new UserController(sessionBean.getUser());
                    user = uc.retrieve(getUser().getEmail());
                    ItemController ic = new ItemController(user);
                    ic.create(item, collection.getId());
                }
                externalUrl = "";
            }
            finally
            {
                FileUtils.deleteQuietly(tmp);
            }
        }
        catch (Exception e)
        {
            logger.error("Error uploading file from link: " + externalUrl, e);
            fFiles.add(e.getMessage() + ": " + title);
        }
        return "";
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
    private File createTmpFile()
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
    private void validateName()
    {
        if (StorageUtils.hasExtension(title))
        {
            if (checkNameUnique)
            {
                // if the checkNameUnique is checked, check that two files with the same name is not possible
                if ((importImageToFile || uploadFileToItem) && filenameExistsInCurrentUpload())
                    throw new RuntimeException("There is already at least one item with the filename ");
                else if (!((importImageToFile || uploadFileToItem)) && filenameExistsInCollection(title)
                        || filenameExistsInCurrentUpload())
                    throw new RuntimeException("There is already at least one item with the filename "
                            + FilenameUtils.getBaseName(title));
            }
            if (!isAllowedFormat(FilenameUtils.getExtension(title)))
                throw new RuntimeException(sessionBean.getMessage("upload_format_not_allowed") + " ("
                        + FilenameUtils.getExtension(title) + ")");
        }
    }

    /**
     * Upload one File and create the {@link de.mpg.imeji.logic.vo.Item}
     * 
     * @param bytes
     * @throws Exception
     */
    private Item uploadFile(File file)
    {
        try
        {
            if (!StorageUtils.hasExtension(title))
                title += StorageUtils.guessExtension(file);
            validateName();
            storageController = new StorageController();
            Item item = null;
            if (importImageToFile)
            {
                item = replaceWebResolutionAndThumbnailOfItem(findItemByFileName(title), file);
            }
            else if (uploadFileToItem)
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
            sNum += 1;
            sFiles.add(item);
            return item;
        }
        catch (Exception e)
        {
            fNum += 1;
            fFiles.add(" File " + title + " not uploaded: " + e.getMessage());
            logger.error("Error uploading item: ", e);
            e.printStackTrace();
        }
        return null;
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
     * True if 2 files in the current upload have the same name.
     * 
     * @return
     */
    private boolean filenameExistsInCurrentUpload()
    {
        for (Item item : itemList)
            if (item.getFilename().equals(title))
                return true;
        return false;
    }

    /**
     * Create the {@link Item} for the files which have been uploaded
     */
    private void createItemForFiles()
    {
        ItemController ic = new ItemController(user);
        try
        {
            ic.create(itemList, collection.getId());
            itemList = new ArrayList<Item>();
        }
        catch (Exception e)
        {
            logger.error("Error creating files for upload", e);
        }
    }

    /**
     * Update the {@link Item} for the files which have been uploaded
     */
    private void updateItemForFiles()
    {
        ItemController ic = new ItemController(user);
        try
        {
            ic.update(itemList, user);
            itemList = new ArrayList<Item>();
        }
        catch (Exception e)
        {
            logger.error("Error creating files for upload", e);
        }
    }

    /**
     * Rmove the files which don't have been created with an {@link Item}. Ca happen if the upload is interrupted
     */
    private void removeFiles()
    {
        if (itemList != null)
        {
            for (Item item : itemList)
            {
                storageController.delete(item.getStorageId());
            }
        }
        itemList = new ArrayList<Item>();
    }

    /**
     * Load the collection
     */
    public void loadCollection()
    {
        if (id != null)
        {
            collection = ObjectLoader.loadCollectionLazy(ObjectHelper.getURI(CollectionImeji.class, id),
                    sessionBean.getUser());
            if (collection != null && getCollection().getId() != null)
            {
                ItemController ic = new ItemController(sessionBean.getUser());
                collectionSize = ic.countContainerSize(collection);
            }
        }
        else
        {
            BeanHelper.error(sessionBean.getLabel("error") + "No ID in URL");
        }
    }

    /**
     * Write the report about the upload results
     * 
     * @return
     * @throws Exception
     */
    public String report() throws Exception
    {
        setTotalNum(totalNum);
        setsNum(sNum);
        setsFiles(sFiles);
        setfNum(fNum);
        setfFiles(fFiles);
        setUploadFinished(true);
        return "";
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

    public String getTotalNum()
    {
        return totalNum;
    }

    public void setTotalNum(String totalNum)
    {
        this.totalNum = totalNum;
    }

    public int getsNum()
    {
        return sNum;
    }

    public void setsNum(int sNum)
    {
        this.sNum = sNum;
    }

    public int getfNum()
    {
        return fNum;
    }

    public void setfNum(int fNum)
    {
        this.fNum = fNum;
    }

    public List<Item> getsFiles()
    {
        return sFiles;
    }

    public void setsFiles(List<Item> sFiles)
    {
        this.sFiles = sFiles;
    }

    public List<String> getfFiles()
    {
        return fFiles;
    }

    public void setfFiles(List<String> fFiles)
    {
        this.fFiles = fFiles;
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
        return sessionBean.getUser();
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
     * @return the importImageToFile
     */
    public boolean isImportImageToFile()
    {
        return importImageToFile;
    }

    /**
     * @param importImageToFile the importImageToFile to set
     */
    public void setImportImageToFile(boolean importImageToFile)
    {
        this.importImageToFile = importImageToFile;
    }

    /**
     * @return the uploadFileToItem
     */
    public boolean isUploadFileToItem()
    {
        return uploadFileToItem;
    }

    /**
     * @param uploadFileToItem the uploadFileToItem to set
     */
    public void setUploadFileToItem(boolean uploadFileToItem)
    {
        this.uploadFileToItem = uploadFileToItem;
    }

    public void uploadFileToItemListener()
    {
        importImageToFile = false;
    }

    public void importImageToFileListener()
    {
        uploadFileToItem = false;
    }

    /**
     * @return the checkNameUnique
     */
    public boolean isCheckNameUnique()
    {
        return checkNameUnique;
    }

    /**
     * @param checkNameUnique the checkNameUnique to set
     */
    public void setCheckNameUnique(boolean checkNameUnique)
    {
        this.checkNameUnique = checkNameUnique;
    }

    public String getDiscardComment()
    {
        return collection.getDiscardComment();
    }

    public void setDiscardComment(String comment)
    {
        collection.setDiscardComment(comment);
    }

    /**
     * @return the uploadFinished
     */
    public boolean isUploadFinished()
    {
        return uploadFinished;
    }

    /**
     * @param uploadFinished the uploadFinished to set
     */
    public void setUploadFinished(boolean uploadFinished)
    {
        this.uploadFinished = uploadFinished;
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.presentation.beans.ContainerBean#getNavigationString()
     */
    @Override
    protected String getNavigationString()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
