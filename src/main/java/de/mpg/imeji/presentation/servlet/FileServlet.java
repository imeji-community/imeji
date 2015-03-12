/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import de.mpg.imeji.exceptions.AuthenticationError;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotAllowedError;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.AuthenticationFactory;
import de.mpg.imeji.logic.auth.Authorization;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.impl.ExternalStorage;
import de.mpg.imeji.logic.storage.internal.InternalStorageManager;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.PropertyReader;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * The Servlet to Read files from imeji {@link Storage}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = 5502546330318540997L;
	private static Logger logger = Logger.getLogger(FileServlet.class);
	private StorageController storageController;
	private Authorization authorization;
	private Navigation navivation;
	private String domain;
	private String digilibUrl;
	/**
	 * If property imeji.storage.path= /data/imeji/files/ then,
	 * internalStorageRoot = files
	 */
	private String internalStorageRoot;
	/**
	 * The path for this servlet as defined in the web.xml
	 */
	public static final String SERVLET_PATH = "file";

	@Override
	public void init() {
		try {
			storageController = new StorageController();
			logger.info("File Servlet initialized");
			authorization = new Authorization();
			navivation = new Navigation();
			domain = StringHelper.normalizeURI(navivation.getDomain());
			domain = domain.substring(0, domain.length() - 1);
			digilibUrl = PropertyReader
					.getProperty("digilib.imeji.instance.url");
			if (digilibUrl != null && !digilibUrl.isEmpty())
				digilibUrl = StringHelper.normalizeURI(digilibUrl);
			InternalStorageManager ism = new InternalStorageManager();
			internalStorageRoot = FilenameUtils.getBaseName(FilenameUtils
					.normalizeNoEndSeparator(ism.getStoragePath()));
		} catch (Exception e) {
			throw new RuntimeException("Image servlet not initialized! " + e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String url = req.getParameter("id");
		boolean download = "1".equals(req.getParameter("download"));
		if (url == null) {
			// if the id parameter is null, interpret the whole url as a direct
			// to the file (can only work if the
			// internal storage is used)
			url = domain + req.getRequestURI();
		}
		resp.setContentType(StorageUtils.getMimeType(StringHelper
				.getFileExtension(url)));
		SessionBean session = getSession(req);
		User user = getUser(req, session);
		
		try {
				Item fileItem = getItem(url, user);
				if ("NO_THUMBNAIL_URL".equals(url)) {
					ExternalStorage eStorage = new ExternalStorage();
					eStorage.read("http://localhost:8080/imeji/resources/icon/empty.png",
							resp.getOutputStream(), true);

				} else {
					
					if (download)
						resp.setHeader("Content-disposition", "attachment;");

                    storageController.read(url, resp.getOutputStream(), true);

                    //message to observer if item downloaded
                    if (download)
                        notifyByEmail(user, fileItem, session);


                }
			} 
		catch (Exception e) {
			if (e instanceof NotAllowedError ) {
				resp.sendError(HttpServletResponse.SC_FORBIDDEN,
					"imeji security: You are not allowed to view this file");
			}
			else if (e instanceof AuthenticationError ) {
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "imeji security: You need to be signed-in to view this file.");
			}
			else if (e instanceof NotFoundException ) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND, "The resource you are trying to retrieve does not exist!");
			}
			else
			{
				resp.sendError(422, "Unprocessable entity!");
/*				ExternalStorage eStorage = new ExternalStorage();
				eStorage.read(domain + "/imeji/resources/icon/empty.png",
						resp.getOutputStream(), true);
*/			}
		}
		
}

    /**
     * Send email notifications to all users which checked
     * "Send notification email by item download" feature
     * for collection of item
     *
     * @param user
     * @param fileItem
     * @param session
     * @throws ImejiException
     * @throws IOException
     * @throws URISyntaxException
     */
    private void notifyByEmail(User user, Item fileItem, SessionBean session) throws ImejiException, IOException, URISyntaxException {
        CollectionController cc = new CollectionController();
        final CollectionImeji c = cc.retrieve(fileItem.getCollection(), Imeji.adminUser);

        final UserController uc = new UserController(Imeji.adminUser);
        final EmailClient emailClient = new EmailClient();
        EmailMessages msgs = new EmailMessages();
        for(User u:  uc.searchUsersToBeNotified(user, c)) {
                emailClient.sendMail(u.getEmail(), null,
                        msgs.getEmailOnItemDownload_Subject(fileItem, session),
                        msgs.getEmailOnItemDownload_Body(u, user, fileItem, c, session));
                logger.info("Sent notification email to user: "+ u.getName() + "<" + u.getEmail()
                        + ">"  + " by item " + fileItem.getId() + " download");
        }
    }

    /**
	 * Load a {@link CollectionImeji} from the session if possible, otherwise
	 * from jena
	 * 
	 * @param url
     * @param session
     * @return
	 */
	private CollectionImeji loadCollection(String url, SessionBean session) {
		if (session == null)
			return loadCollection(url);
		URI collectionURI = getCollectionURI(url);
		if (collectionURI == null)
			return null;
		CollectionImeji collection = session.getCollectionCached().get(
				collectionURI);
		if (collection == null) {
			try {
				// important to use lazy load, otherwise high performance issue
				collection = ObjectLoader.loadCollectionLazy(collectionURI,
						Imeji.adminUser);
				session.getCollectionCached().put(collection.getId(),
						collection);
			} catch (Exception e) {
				/* user is not allowed to view this collection */
			}
		}
		return collection;
	}

	/**
	 * Return the {@link User} of the request. Check first is a user is send
	 * with the request. If not, check in the the session.
	 * 
	 * @param req
	 * @return
	 */
	private User getUser(HttpServletRequest req, SessionBean session) {
		User user = AuthenticationFactory.factory(req).doLogin();
		if (user != null)
			return user;
		if (session != null) {
			return session.getUser();
		}
		return null;

	}

	/**
	 * Load a {@link CollectionImeji} when the session is null
	 * 
	 * @param url
	 * @return
	 */
	private CollectionImeji loadCollection(String url) {
		List<String> l = ImejiSPARQL.exec(
				SPARQLQueries.selectCollectionIdOfFile(url), null);
		if (l.size() == 0)
			throw new RuntimeException("File " + url + " couldn't be found");
		CollectionController c = new CollectionController();
		try {
			return c.retrieve(URI.create(l.get(0)), null);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	
	

	/**
	 * Return the uri of the {@link CollectionImeji} of the file with this url
	 * 
	 * @param url
	 * @return
	 */
	private URI getCollectionURI(String url) {
		String id = storageController.getCollectionId(url);
		if (id != null) {
			return ObjectHelper.getURI(CollectionImeji.class, id);
		} else {
			Search s = SearchFactory.create();
			List<String> r = s.searchSimpleForQuery(
					SPARQLQueries.selectCollectionIdOfFile(url)).getResults();
			if (!r.isEmpty())
				return URI.create(r.get(0));
			else
				return null;
		}
	}

	/**
	 * Find the {@link Item} which is owner of the file
	 * 
	 * @param url
	 * @return
	 * @throws Exception 
	 */
	private Item getItem(String url, User user) throws Exception {
		Search s = SearchFactory.create();
		List<String> r = s.searchSimpleForQuery(SPARQLQueries.selectItemIdOfFile(url)).getResults();
		if (!r.isEmpty() && r.get(0) != null) {
			ItemController c = new ItemController();
			return c.retrieve(URI.create(r.get(0)), user);
		}
		else
		{
			throw new NotFoundException("Can not find the resource requested");
		}
	}

	/**
	 * Return the {@link SessionBean} form the {@link HttpSession}
	 * 
	 * @param req
	 * @return
	 */
	private SessionBean getSession(HttpServletRequest req) {
		return (SessionBean) req.getSession(true).getAttribute(
				SessionBean.class.getSimpleName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// No post action
		return;
	}
}
