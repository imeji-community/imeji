/**
 * 
 */
package de.mpg.escidoc.faces.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * @author saquet
 */
public class ImageServlet extends HttpServlet
{
    private static Logger logger = Logger.getLogger(ImageServlet.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected synchronized void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {

        String href = req.getParameter("href");
        //String mimeType = req.getParameter("mimetype");
        String url = null;
        try
        {
            url = ServiceLocator.getFrameworkUrl() + href;
        }
        catch (Exception e)
        {
            throw new ServletException("Error getting framework url", e);
        }
        
        // test new method
        //String contentType = mimeType; // For dialog, try
        
        //resp.setContentType(contentType);
        byte[] buffer = null;

        GetMethod method = new GetMethod(url);
        method.setFollowRedirects(false);
        
        SessionBean sessionBean = (SessionBean) req.getSession().getAttribute("SessionBean");
        if (sessionBean.getUserHandle() != null)
        {
            method.addRequestHeader("Cookie", "escidocCookie=" + sessionBean.getUserHandle());
        }
        
        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        
        InputStream input;
        OutputStream out = resp.getOutputStream();
        
        if (method.getStatusCode() != 200)
        {
            throw new RuntimeException("error code " + method.getStatusCode());
        }
        else
        {
            for (Header header : method.getResponseHeaders())
            {
                resp.setHeader(header.getName(), header.getValue());
            }
        
            input = method.getResponseBodyAsStream();
        }
        try
        {
            buffer = new byte[2048];
            int numRead;
            long numWritten = 0;
            while ((numRead = input.read(buffer)) != -1)
            {
                out.write(buffer, 0, numRead);
                out.flush();
                numWritten += numRead;
            }

        }
        catch (IOException e1)
        {
            logger.debug("Download IO Error: " + e1.toString());
        }
        input.close();
        //out.close();

    }
}
