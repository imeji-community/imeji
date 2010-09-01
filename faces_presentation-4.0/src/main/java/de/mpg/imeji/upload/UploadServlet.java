package de.mpg.imeji.upload;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.escidoc.ItemVO;
import de.mpg.imeji.upload.deposit.DepositController;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.User;

/**
 * @author yu
 */
public class UploadServlet extends HttpServlet
{
    private static Logger logger = Logger.getLogger(UploadServlet.class);
    private static String title;
    private static String description;
    private static String mimetype;
    private static String format;
    private static String userHandle;
    private static String collection;
    private static String context;
    private static String userEmail;
    private static String userPwd;

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        ServletInputStream inputStream = req.getInputStream();
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        title = req.getParameter("name");
        StringTokenizer st = new StringTokenizer(title, ".");
        while (st.hasMoreTokens())
            format = st.nextToken();
        mimetype = "image/" + format;
        userHandle = req.getParameter("userHandle");
        collection = req.getParameter("collection");
        context = req.getParameter("context");
        userEmail = req.getParameter("userEmail");
        userPwd = req.getParameter("userPwd");
        
        // TODO remove static image description
        description = "";
        try
        {
            UserController uc = new UserController(null);
            User user = uc.retrieve(userEmail);
            CollectionController collectionController = new CollectionController(user);
            CollectionImeji coll = new CollectionImeji();
            try
            {
                coll = collectionController.retrieve(URI.create(collection));
                ItemVO item = DepositController.createImejiItem(bufferedImage, title, description, mimetype, format,
                        userHandle, collection, context);
               DepositController.depositImejiItem(item, userHandle, coll, user);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
