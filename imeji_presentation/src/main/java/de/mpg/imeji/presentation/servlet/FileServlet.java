/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.IOException;
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
        if (security.check(OperationsType.READ, getUser(req), getCollection(url)))
        {
            storageController.read(url, resp.getOutputStream());
        }
        else
        {
            resp.sendError(403, "imeji security: You are not allowed to view this file");
        }
    }

    private CollectionImeji getCollection(String url)
    {
        CollectionImeji col = new CollectionImeji();
        col.setId(ObjectHelper.getURI(CollectionImeji.class, storageController.getCollectionId(url)));
        return col;
    }

    /**
     * Read the user in the session
     * 
     * @param req
     * @return
     */
    private User getUser(HttpServletRequest req)
    {
        SessionBean sessionBean = (SessionBean)req.getSession().getAttribute(SessionBean.class.getSimpleName());
        if (sessionBean != null)
        {
            return sessionBean.getUser();
        }
        return null;
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
