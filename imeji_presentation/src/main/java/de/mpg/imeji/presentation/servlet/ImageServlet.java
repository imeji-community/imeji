/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import de.mpg.imeji.presentation.util.LoginHelper;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.presentation.util.ProxyHelper;

/**
 * The Servlet to Read images from external systems (to enable authentification and authorization for this systems)
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImageServlet extends HttpServlet
{
    private static final long serialVersionUID = 5502546330318540997L;
    private static Logger logger = Logger.getLogger(ImageServlet.class);
    private String userHandle;
    private String imejiUrl;
    private HttpClient client = null;

    @Override
    public void init()
    {
        try
        {
            imejiUrl = PropertyReader.getProperty("escidoc.imeji.instance.url");
            logger.info("ImageServlet initialized");
            MultiThreadedHttpConnectionManager conn = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams connParams = new HttpConnectionManagerParams();
            connParams.setConnectionTimeout(5000);
            connParams.setDefaultMaxConnectionsPerHost(50);
            conn.setParams(connParams);
            client = new HttpClient(conn);
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
        String imageUrl = req.getParameter("imageUrl");
        GetMethod method = null;
        try
        {
            if (imageUrl == null)
            {
                resp.sendError(404, "URL null");
            }
            else
            {
                userHandle = getEscidocUserHandle();
                method = newGetMethod(imageUrl, false, userHandle);
                // Check if the image url is accessible via a proxy
                ProxyHelper.setProxy(client, imageUrl);
                client.executeMethod(method);
                InputStream input;
                if (method.getStatusCode() == 302)
                {
                    // image not found, try again
                    // release previous connection
                    method.releaseConnection();
                    userHandle = getNewEscidocUserHandle();
                    method = newGetMethod(imageUrl, true, userHandle);
                    client.executeMethod(method);
                }
                if (method.getStatusCode() != 200)
                {
                    // image not found after retry, display default Thumbnail
                    // release previous connection
                    method.releaseConnection();
                    // Check if the imejiUrl is accessible via a proxy
                    ProxyHelper.setProxy(client, imejiUrl);
                    method = newGetMethod(imejiUrl + "/resources/icon/defaultThumb.gif", false, null);
                    client.executeMethod(method);
                    if (method.getStatusCode() == 302)
                    {
                        throw new RuntimeException("error code " + method.getStatusCode());
                    }
                    input = method.getResponseBodyAsStream();
                }
                else
                {
                    for (Header header : method.getResponseHeaders())
                    {
                        resp.setHeader(header.getName(), header.getValue());
                    }
                    input = method.getResponseBodyAsStream();
                }
                OutputStream out = resp.getOutputStream();
                byte[] buffer = new byte[1024];
                int numRead;
                while ((numRead = input.read(buffer)) != -1)
                {
                    out.write(buffer, 0, numRead);
                }
                input.close();
                // out.flush();
                out.close();
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            if (method != null)
            {
                method.releaseConnection();
            }
        }
    }

    /**
     * Log in eSciDoc and return the userHandle
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws Exception
     */
    private String getNewEscidocUserHandle() throws IOException, URISyntaxException, Exception
    {
        return LoginHelper.login(PropertyReader.getProperty("imeji.escidoc.user"),
                PropertyReader.getProperty("imeji.escidoc.password"));
    }

    /**
     * Get the current handle, if null get a new one
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws Exception
     */
    private String getEscidocUserHandle() throws IOException, URISyntaxException, Exception
    {
        if (userHandle == null)
        {
            return getNewEscidocUserHandle();
        }
        return null;
    }

    /**
     * Initialize a http get mehtod
     * 
     * @param url
     * @param followRedirects
     * @param userHandle
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws Exception
     */
    private GetMethod newGetMethod(String url, boolean followRedirects, String userHandle) throws IOException,
            URISyntaxException, Exception
    {
        GetMethod method = new GetMethod(url);
        method.setFollowRedirects(followRedirects);
        if (userHandle != null)
        {
            method.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
        }
        method.addRequestHeader("Cache-Control", "public");
        method.setRequestHeader("Connection", "close");
        return method;
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
