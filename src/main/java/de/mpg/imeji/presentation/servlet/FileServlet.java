/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.internal.InternalStorageManager;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.PropertyReader;

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
    private Navigation navivation;
    private String domain;
    private String digilibUrl;
    /**
     * If property imeji.storage.path= /data/imeji/files/ then, internalStorageRoot = files
     */
    private String internalStorageRoot;
    /**
     * The path for this servlet as defined in the web.xml
     */
    public static final String SERVLET_PATH = "file";

    @Override
    public void init()
    {
        try
        {
            storageController = new StorageController();
            logger.info("ImageServlet initialized");
            security = new Security();
            navivation = new Navigation();
            domain = StringHelper.normalizeURI(navivation.getDomain());
            domain = domain.substring(0, domain.length() - 1);
            digilibUrl = PropertyReader.getProperty("digilib.imeji.instance.url");
            if (digilibUrl != null && !digilibUrl.isEmpty())
                digilibUrl = StringHelper.normalizeURI(digilibUrl);
            InternalStorageManager ism = new InternalStorageManager();
            internalStorageRoot = FilenameUtils
                    .getBaseName(FilenameUtils.normalizeNoEndSeparator(ism.getStoragePath()));
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
        if (url == null)
        {
            // if the id parameter is null, interpret the whole url as a direct to the file (can only work if the
            // internal storage is used)
            url = domain + req.getRequestURI();
        }
        resp.setContentType(StorageUtils.getMimeType(StringHelper.getFileExtension(url)));
        SessionBean session = getSession(req);
        if (security.check(OperationsType.READ, getUser(session), loadCollection(url, session)))
        {
            storageController.read(url, resp.getOutputStream(), true);
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
        URI collectionURI = getCollectionURI(url);
        if (collectionURI == null)
            return null;
        CollectionImeji collection = session.getCollectionCached().get(collectionURI);
        if (collection == null)
        {
            try
            {
                // important to use lazy load, otherwise high performance issue
                collection = ObjectLoader.loadCollectionLazy(collectionURI, session.getUser());
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
     * Return the uri of the {@link CollectionImeji} of the file with this url
     * 
     * @param url
     * @return
     */
    private URI getCollectionURI(String url)
    {
        String id = storageController.getCollectionId(url);
        if (id != null)
        {
            return ObjectHelper.getURI(CollectionImeji.class, id);
        }
        else
        {
            Search s = new Search(SearchType.ALL, null);
            List<String> r = s.searchSimpleForQuery(SPARQLQueries.selectCollectionIdOfFile(url), null);
            if (!r.isEmpty())
                return URI.create(r.get(0));
            else
                return null;
        }
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
        return (SessionBean)req.getSession(false).getAttribute(SessionBean.class.getSimpleName());
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
