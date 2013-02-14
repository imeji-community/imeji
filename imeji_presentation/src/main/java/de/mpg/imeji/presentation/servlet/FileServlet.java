/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.StringHelper;

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

    @Override
    public void init()
    {
        try
        {
            storageController = new StorageController();
            logger.info("ImageServlet initialized");
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
        String imageUrl = req.getParameter("id");
        resp.setContentType(StorageUtils.getMimeType(StringHelper.getFileExtension(imageUrl)));
        storageController.read(imageUrl, resp.getOutputStream());
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
