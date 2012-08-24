/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.imeji.presentation.util.LoginHelper;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.presentation.util.ProxyHelper;

/**
 * A servlet for retrieving and redirecting the content objects urls.
 * 
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3780 $ $LastChangedDate: 2010-07-23 10:01:12 +0200 (Fri, 23 Jul 2010) $
 */
public class ImageServlet extends HttpServlet
{
    private static final long serialVersionUID = 5502546330318540997L;
    private static Logger logger = Logger.getLogger(ImageServlet.class);
    private String userHandle;
    private String frameworkUrl;
    private static int counter = 0;

    @Override
    public void init()
    {
        try
        {
            frameworkUrl = PropertyReader.getProperty("escidoc.framework_access.framework.url");
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
                method = new GetMethod(imageUrl);
                method.setFollowRedirects(false);
                if (userHandle == null)
                {
                    userHandle = LoginHelper.login(PropertyReader.getProperty("imeji.escidoc.user"),
                            PropertyReader.getProperty("imeji.escidoc.password"));
                }
                method.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
                method.addRequestHeader("Cache-Control", "public");
                method.setRequestHeader("Connection", "close");
                // Execute the method with HttpClient.
                HttpClient client = new HttpClient();
                ProxyHelper.setProxy(client, frameworkUrl);
                client.executeMethod(method);
                // byte[] input;
                InputStream input;
                if (method.getStatusCode() == 302)
                {
                    // try again
                    logger.info("try load image again");
                    method.releaseConnection();
                    userHandle = LoginHelper.login(
                            de.mpg.imeji.presentation.util.PropertyReader.getProperty("imeji.escidoc.user"),
                            PropertyReader.getProperty("imeji.escidoc.password"));
                    method = new GetMethod(imageUrl);
                    method.setFollowRedirects(true);
                    method.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
                    client.executeMethod(method);
                }
                if (method.getStatusCode() != 200)
                {
                    ProxyHelper.setProxy(client, PropertyReader.getProperty("escidoc.imeji.instance.url"));
                    method = new GetMethod(PropertyReader.getProperty("escidoc.imeji.instance.url")
                            + "/resources/icon/defaultThumb.gif");
                    client.executeMethod(method);
                    // out = resp.getOutputStream();
                    if (method.getStatusCode() == 302)
                    {
                        method.releaseConnection();
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
                long numWritten = 0;
                while ((numRead = input.read(buffer)) != -1)
                {
                    out.write(buffer, 0, numRead);
                    // out.flush();
                    numWritten += numRead;
                }
                input.close();
                method.releaseConnection();
                out.flush();
                out.close();
                // ReadableByteChannel inputChannel = Channels.newChannel(input);
                // WritableByteChannel outputChannel = Channels.newChannel(out);
                // fastChannelCopy(inputChannel, outputChannel);
                // inputChannel.close();
                // outputChannel.close();
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            if (method != null)
                method.releaseConnection();
        }
    }

    public static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest)
            throws IOException
    {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1)
        {
            // prepare the buffer to be drained
            buffer.flip();
            // write to the channel, may block
            dest.write(buffer);
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear()
            buffer.compact();
        }
        // EOF will leave buffer in fill state
        buffer.flip();
        // make sure the buffer is fully drained.
        while (buffer.hasRemaining())
        {
            dest.write(buffer);
        }
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
