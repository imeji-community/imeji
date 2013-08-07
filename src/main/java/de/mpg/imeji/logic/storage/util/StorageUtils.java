/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.logic.storage.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.io.FilenameUtils;

import de.mpg.imeji.presentation.util.ProxyHelper;

/**
 * Util class fore the storage package
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class StorageUtils
{
    /**
     * Transform an {@link InputStream} to a {@link Byte} array
     * 
     * @param stream
     * @return
     */
    public static byte[] toBytes(InputStream stream)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        try
        {
            while ((b = stream.read()) != -1)
            {
                bos.write(b);
            }
            byte[] ba = bos.toByteArray();
            bos.flush();
            bos.close();
            return ba;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error transforming inputstream to bytearryoutputstream", e);
        }
    }

    public static byte[] toBytes(ImageOutputStream stream)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        try
        {
            while ((b = stream.read()) != -1)
            {
                bos.write(b);
            }
            byte[] ba = bos.toByteArray();
            bos.flush();
            bos.close();
            return ba;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error transforming inputstream to bytearryoutputstream", e);
        }
    }

    /**
     * Write an {@link InputStream} to an {@link OutputStream}
     * 
     * @param out
     * @param input
     * @throws IOException
     */
    public static void writeInOut(InputStream in, OutputStream out, boolean close)
    {
        byte[] buffer = new byte[1024];
        int numRead;
        try
        {
            while ((numRead = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, numRead);
            }
            in.close();
            out.flush();
            if (close)
            {
                out.close();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error writing inputstream in outputstream: ", e);
        }
    }

    /**
     * Return a {@link HttpClient} to be used in {@link Get}
     * 
     * @return
     */
    public static HttpClient getHttpClient()
    {
        MultiThreadedHttpConnectionManager conn = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams connParams = new HttpConnectionManagerParams();
        connParams.setConnectionTimeout(5000);
        connParams.setDefaultMaxConnectionsPerHost(50);
        conn.setParams(connParams);
        return new HttpClient(conn);
    }

    public static GetMethod newGetMethod(HttpClient client, String url)
    {
        GetMethod method = new GetMethod(url);
        method.addRequestHeader("Cache-Control", "public");
        method.setRequestHeader("Connection", "close");
        ProxyHelper.setProxy(client, url);
        return method;
    }

    /**
     * True if 2 filename extension are the same (jpeg = jpeg = JPG, etc.)
     * 
     * @param ext1
     * @param ext2
     * @return
     */
    public static boolean compareExtension(String ext1, String ext2)
    {
        return getMimeType(ext1).equals(getMimeType(ext2));
    }

    /**
     * Return the Mime Type of a file according to its format (i.e. file extension). <br/>
     * The File extension can be found via {@link FilenameUtils}
     * 
     * @param extension
     * @return
     */
    public static String getMimeType(String extension)
    {
        extension = extension.toLowerCase();
        if ("tif".equals(extension))
        {
            return "image/tiff";
        }
        else if ("jpg".equals(extension) || "jpeg".equals(extension))
        {
            return "image/jpeg";
        }
        else if ("png".equals(extension))
        {
            return "image/png";
        }
        else if ("bmp".equals(extension))
        {
            return "image/bmp";
        }
        else if ("gif".equals(extension))
        {
            return "image/gif";
        }
        else if ("pdn".equals(extension))
        {
            return "image/x-paintnet";
        }
        else if ("mov".equals(extension))
        {
            return "video/quicktime";
        }
        else if ("avi".equals(extension))
        {
            return "video/x-msvideo";
        }
        else if ("3gp".equals(extension))
        {
            return "video/3gpp";
        }
        else if ("ts".equals(extension))
        {
            return "video/MP2T";
        }
        else if ("mpg".equals(extension))
        {
            return "video/mpeg";
        }
        else if ("mp4".equals(extension))
        {
            return "video/mp4";
        }
        else if ("wmv".equals(extension))
        {
            return "video/x-ms-wmv";
        }
        else if ("webm".equals(extension))
        {
            return "video/webm";
        }
        else if ("ogg".equals(extension))
        {
            return "video/ogg";
        }
        else if ("flv".equals(extension))
        {
            // still not support directly played in browser
            return "video/x-flv";
        }
        else if ("pdf".equals(extension))
        {
            return "application/pdf";
        }
        else if ("fit".equals(extension))
        {
            return "application/fits";
        }
        else if ("mp3".equals(extension) || "mpeg".equals(extension))
        {
            return "audio/mpeg";
        }
        else if ("wav".equals(extension))
        {
            return "audio/x-wav";
        }
        else if ("wma".equals(extension))
        {
            return "audio/x-ms-wma";
        }
        return "application/octet-stream";
    }

    public static byte[] getBytes(URL url) throws FileNotFoundException
    {
        return StorageUtils.toBytes(new FileInputStream(new File(url.getFile())));
    }
}
