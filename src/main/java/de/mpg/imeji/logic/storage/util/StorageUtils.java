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
import java.net.URI;
import java.net.URL;

import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import com.hp.hpl.jena.util.FileUtils;

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
     * Return the Mime Type of a file according to its format (i.e. file extension)
     * 
     * @param format
     * @return
     */
    public static String getMimeType(String format)
    {
        format = format.toLowerCase();
        if ("tif".equals(format))
        {
            return "image/"+"tiff";
        }
        else if ("jpg".equals(format))
        {
            return "image/jpeg";
        }
        else if ("jpeg".equals(format))
        {
            return "image/jpeg";
        }
        else if ("png".equals(format))
        {
            return "image/png";
        }
        else if ("gif".equals(format))
        {
            return "image/gif";
        }
        else if ("mov".equals(format))
        {
            return "video/quicktime";
        }
        else if ("avi".equals(format))
        {
            return "video/x-msvideo";
        }
        else if ("3gp".equals(format))
        {
            return "video/3gpp";
        }
        else if ("ts".equals(format))
        {
            return "video/MP2T";
        }
        else if ("mpeg".equals(format))
        {
            return "video/mpeg";
        }
        else if ("mp4".equals(format))
        {
            return "video/mp4";
        }
        else if ("wmv".equals(format))
        {
            return "video/x-ms-wmv";
        }
        else if ("webm".equals(format))
        {
            return "video/webm";
        }
        else if ("ogg".equals(format))
        {
            return "video/ogg";
        }
        else if ("flv".equals(format))
        {
            //still not support directly played in browser
        	return "video/x-flv";
        }else if ("pdf".equals(format))
        {
            return "application/pdf";
        }
        return "video/" + format;
    }

	public static byte[] getBytes(URL url) throws FileNotFoundException {
		return StorageUtils.toBytes(new FileInputStream(new File(url.getFile())));		
	}
    
}
