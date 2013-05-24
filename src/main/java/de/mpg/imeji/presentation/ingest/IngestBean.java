package de.mpg.imeji.presentation.ingest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import de.mpg.imeji.logic.ingest.controller.IngestController;
import de.mpg.imeji.logic.security.Authorization;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.beans.AuthorizationBean;
import de.mpg.imeji.presentation.collection.ViewCollectionBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Java Bean for the ingest
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class IngestBean
{
    private SessionBean session = null;
    private String collectionId;
    private CollectionImeji collection;
    private static Logger logger = Logger.getLogger(IngestBean.class);

    /**
     * Default constructor
     */
    public IngestBean()
    {
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    /**
     * Method reading url to trigger event
     */
    public void status()
    {
        if (UrlHelper.getParameterBoolean("init"))
        {
            loadCollection();
            ((AuthorizationBean)BeanHelper.getSessionBean(AuthorizationBean.class)).init(collection);
        }
        else if ("itemlist".equals(UrlHelper.getParameterValue("start")))
        {
            try
            {
                IngestController ic = new IngestController(session.getUser(), collection);
                ic.ingest(upload(), null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if ("profile".equals(UrlHelper.getParameterValue("start")))
        {
            try
            {
                IngestController ic = new IngestController(session.getUser(), collection);
                ic.ingest(null, upload());
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
                session.getProfileCached().clear();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Upload the files for the ingest
     * 
     * @return
     * @throws Exception
     */
    public File upload() throws Exception
    {
        HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        File f = null;
        if (isMultipart)
        {
            ServletFileUpload upload = new ServletFileUpload();
            // Parse the request
            FileItemIterator iter = upload.getItemIterator(req);
            while (iter.hasNext())
            {
                FileItemStream item = iter.next();
                if (item != null && item.getName() != null)
                {
                    logger.info("Ingesting file  " + item.getName());
                    f = write2File("itemListXml", item.openStream());
                }
            }
        }
        return f;
    }

    /**
     * Load the {@link CollectionImeji} for the ingest
     */
    private void loadCollection()
    {
        if (collectionId != null)
        {
            ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).setId(collectionId);
            ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).init();
            collection = ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getCollection();
        }
        else
        {
            BeanHelper.error(session.getLabel("error") + " No ID in URL");
        }
    }

    /**
     * Write an {@link InputStream} to temp file
     * 
     * @param fileName
     * @param is
     * @return
     * @throws Exception
     */
    private File write2File(String fileName, InputStream is) throws Exception
    {
        File f = new File(System.getProperty("java.io.tmpdir"), fileName);
        try
        {
            OutputStream os = new FileOutputStream(f);
            try
            {
                byte[] buffer = new byte[4096];
                for (int n; (n = is.read(buffer)) != -1;)
                {
                    os.write(buffer, 0, n);
                }
            }
            finally
            {
                os.close();
            }
        }
        finally
        {
            is.close();
        }
        return f;
    }

    /**
     * getter
     * 
     * @return
     */
    public CollectionImeji getCollection()
    {
        return collection;
    }

    /**
     * setter
     * 
     * @param collection
     */
    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getCollectionId()
    {
        return collectionId;
    }

    /**
     * setter
     * 
     * @param collectionId
     */
    public void setCollectionId(String collectionId)
    {
        this.collectionId = collectionId;
    }

    /**
     * Return the size of the current {@link CollectionImeji}
     * 
     * @return
     */
    public int getCollectionSize()
    {
        return getCollection().getImages().size();
    }
}
