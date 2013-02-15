/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.upload;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.UploadResult;
import de.mpg.imeji.logic.storage.impl.InternalStorage;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
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
public class UploadBean
{
    private CollectionImeji collection;
    private int collectionSize = 0;
    private SessionBean sessionBean;
    private String id;
    private User user;
    private String title;
    private String totalNum;
    private int sNum;
    private int fNum;
    private List<String> sFiles;
    private List<String> fFiles;
    private String externalUrl;
    private StorageController storageController;
    private Collection<Item> itemList;
    private static Logger logger = Logger.getLogger(Upload.class);

    /**
     * Construct the Bean and initalize the pages
     */
    public UploadBean()
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        storageController = new StorageController("internal");
    }

    /**
     * Method checking the url parameters and triggering then the {@link UploadBean} methods
     */
    public void status()
    {
        if (UrlHelper.getParameterBoolean("init"))
        {
            removeFiles();
            loadCollection();
            totalNum = "";
            sNum = 0;
            fNum = 0;
            sFiles = new ArrayList<String>();
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
                createItemForFiles();
                totalNum = UrlHelper.getParameterValue("totalNum");
                loadCollection();
                report();
            }
            catch (Exception e)
            {
                logger.error("Error upload", e);
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
            StorageController externalController = new StorageController("external");
            externalUrl = URLDecoder.decode(externalUrl, "UTF-8");
            URL url = new URL(externalUrl);
            title = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            externalController.read(url.toString(), baos);
            Item item = uploadFile(baos.toByteArray());
            ItemController ic = new ItemController(user);
            ic.create(item, collection.getId());
            externalUrl = "";
        }
        catch (Exception e)
        {
            logger.error("Error uploading file from link: " + externalUrl, e);
            BeanHelper.error("Error uploading file from link: " + externalUrl);
        }
        return "";
    }

    /**
     * Start the Upload of the items
     * 
     * @throws Exception
     */
    public void upload() throws Exception
    {
        UserController uc = new UserController(null);
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
                    Item item = uploadFile(StorageUtils.toBytes(stream));
                    if (item != null)
                        itemList.add(item);
                }
            }
        }
    }

    /**
     * Upload one File and create the {@link de.mpg.imeji.logic.vo.Item}
     * 
     * @param bytes
     * @throws Exception
     */
    private Item uploadFile(byte[] bytes)
    {
        try
        {
            storageController = new StorageController();
            UploadResult uploadResult = storageController.upload(title, bytes, id);
            Item item = ImejiFactory.newItem(collection, user, uploadResult.getId(), title,
                    URI.create(uploadResult.getOrginal()), URI.create(uploadResult.getThumb()),
                    URI.create(uploadResult.getWeb()));
            sNum += 1;
            sFiles.add(title);
            return item;
        }
        catch (Exception e)
        {
            fNum += 1;
            fFiles.add(title);
            logger.error("Error uploading image: ", e);
        }
        return null;
    }

    /**
     * Create the {@link Item} fot the files which have been uploaded
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
     * Rmove the files which don't have been created with an {@link Item}. Ca happen if the upload is interrupted
     */
    private void removeFiles()
    {
        if (itemList != null)
        {
            for (Item item : itemList)
            {
                System.out.println("remove file " + item.getStorageId());
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
                collectionSize = ic.search(getCollection().getId(), null, null, null).getNumberOfRecords();
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
        return "";
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

    public List<String> getsFiles()
    {
        return sFiles;
    }

    public void setsFiles(List<String> sFiles)
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
}
