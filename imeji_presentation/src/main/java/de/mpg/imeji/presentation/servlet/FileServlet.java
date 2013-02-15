/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * The Servlet to Read files from imeji {@link Storage}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class FileServlet extends HttpServlet
{
    private static final long serialVersionUID = 5502546330318540997L;
    private static Logger logger = Logger.getLogger(FileServlet.class);
    private StorageController storageController;
    private Security security;

    @Override
    public void init()
    {
        try
        {
            storageController = new StorageController();
            logger.info("ImageServlet initialized");
            security = new Security();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Image servlet not initialized! " + e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String url = req.getParameter("id");
        resp.setContentType(StorageUtils.getMimeType(StringHelper.getFileExtension(url)));
        SessionBean session = getSession(req);
        if (security.check(OperationsType.READ, getUser(session), loadCollection(url, session)))
        {
            storageController.read(url, resp.getOutputStream());
        }
        else
        {
            resp.sendError(403, "imeji security: You are not allowed to view this file");
        }
    }

    /**
     * Load a {@link CollectionImeji} from the session if possible, otherwise from jena
     * 
     * @param uri
     * @param user
     * @return
     */
    private CollectionImeji loadCollection(String url, SessionBean session)
    {
        URI collectionURI = ObjectHelper.getURI(CollectionImeji.class, storageController.getCollectionId(url));
        CollectionImeji collection = session.getCollectionCached().get(collectionURI);
        if (collection == null)
        {
            try
            {
                collection = ObjectLoader.loadCollection(collectionURI, session.getUser());
                session.getCollectionCached().put(collection.getId(), collection);
            }
            catch (Exception e)
            {
                /* user is not allowed to view this collection */
            }
        }
        return collection;
    }

    /**
     * Read the user in the session
     * 
     * @param req
     * @return
     */
    private User getUser(SessionBean sessionBean)
    {
        if (sessionBean != null)
        {
            return sessionBean.getUser();
        }
        return null;
    }

    /**
     * Return the {@link SessionBean} form the {@link HttpSession}
     * 
     * @param req
     * @return
     */
    private SessionBean getSession(HttpServletRequest req)
    {
        return (SessionBean)req.getSession().getAttribute(SessionBean.class.getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // No post action
        return;
    }
}
