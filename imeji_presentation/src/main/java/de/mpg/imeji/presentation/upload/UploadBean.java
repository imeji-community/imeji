/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.upload;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import de.escidoc.core.resources.om.item.Item;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.escidoc.EscidocHelper;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.upload.deposit.DepositController;
import de.mpg.imeji.presentation.upload.helper.ImageHelper;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.LoginHelper;
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
public class UploadBean
{
    private CollectionImeji collection;
    private int collectionSize = 0;
    private SessionBean sessionBean;
    private String id;
    private String escidocContext;
    private String escidocUserHandle;
    private User user;
    private String title;
    private String format;
    private String mimetype;
    private String description;
    private String totalNum;
    private int sNum;
    private int fNum;
    private List<String> sFiles;
    private List<String> fFiles;
    private static Logger logger = Logger.getLogger(Upload.class);

    /**
     * Construct the Bean and initalize the pages
     */
    public UploadBean()
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        try
        {
            escidocContext = PropertyReader.getProperty("escidoc.imeji.context.id");
            logInEscidoc();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method checking the url parameters and triggering then the {@link UploadBean} methods
     */
    public void status()
    {
        if (UrlHelper.getParameterBoolean("init"))
        {
            loadCollection();
            totalNum = "";
            sNum = 0;
            fNum = 0;
            sFiles = new ArrayList<String>();
            fFiles = new ArrayList<String>();
        }
        else if (UrlHelper.getParameterBoolean("start"))
        {
            try
            {
                upload();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (UrlHelper.getParameterBoolean("done"))
        {
            try
            {
                totalNum = UrlHelper.getParameterValue("totalNum");
                loadCollection();
                report();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Start the Upload of the items
     * 
     * @throws Exception
     */
    public void upload() throws Exception
    {
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
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                if (!item.isFormField())
                {
                    title = item.getName();
                    StringTokenizer st = new StringTokenizer(title, ".");
                    while (st.hasMoreTokens())
                    {
                        format = st.nextToken();
                    }
                    mimetype = ImageHelper.getMimeType(format);
                    // TODO remove static image description
                    description = "";
                    try
                    {
                        UserController uc = new UserController(null);
                        User user = uc.retrieve(getUser().getEmail());
                        try
                        {
                            DepositController controller = new DepositController();
                            Item escidocItem = controller.createEscidocItem(stream, title, mimetype);
                            controller.createImejiImage(collection, user, escidocItem.getOriginObjid(), title,
                                    URI.create(EscidocHelper.getOriginalResolution(escidocItem)),
                                    URI.create(EscidocHelper.getThumbnailUrl(escidocItem)),
                                    URI.create(EscidocHelper.getWebResolutionUrl(escidocItem)));
                            sNum += 1;
                            sFiles.add(title);
                        }
                        catch (Exception e)
                        {
                            fNum += 1;
                            fFiles.add(title);
                            logger.error("Error uploading image: ", e);
                        }
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
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
                collectionSize = ic.countImagesInContainer(getCollection().getId(), new SearchQuery());
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

    /**
     * Log in in escidoc with the escidoc user
     * 
     * @throws Exception
     */
    public void logInEscidoc() throws Exception
    {
        String userName = PropertyReader.getProperty("imeji.escidoc.user");
        String password = PropertyReader.getProperty("imeji.escidoc.password");
        escidocUserHandle = LoginHelper.login(userName, password);
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

    public String getEscidocContext()
    {
        return escidocContext;
    }

    public void setEscidocContext(String escidocContext)
    {
        this.escidocContext = escidocContext;
    }

    public String getEscidocUserHandle()
    {
        return escidocUserHandle;
    }

    public void setEscidocUserHandle(String escidocUserHandle)
    {
        this.escidocUserHandle = escidocUserHandle;
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
}
