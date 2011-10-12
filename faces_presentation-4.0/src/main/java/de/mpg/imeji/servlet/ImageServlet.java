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
 * Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */ 

package de.mpg.imeji.servlet;

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

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.util.LoginHelper;

/**
 * A servlet for retrieving and redirecting the content objects urls.
 *     /pubman/item/escidoc:12345 for items
 * and
 *     /pubman/item/escidoc:12345/component/escidoc:23456/name.txt for components.
 *     
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3780 $ $LastChangedDate: 2010-07-23 10:01:12 +0200 (Fri, 23 Jul 2010) $
 *
 */
public class ImageServlet extends HttpServlet
{
	private static Logger logger = Logger.getLogger(ImageServlet.class);
	private String userHandle;
	private String frameworkUrl;
	private static int counter = 0;
	@Override
	public void init()
	{
		try 
		{
			frameworkUrl = ServiceLocator.getFrameworkUrl();
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
			if (imageUrl==null)
			{
				resp.sendError(404, "URL null");
			}
			else
			{
				method = new GetMethod(imageUrl);
				method.setFollowRedirects(false);

				if (userHandle == null)
				{
					userHandle = LoginHelper.login(PropertyReader.getProperty("imeji.escidoc.user"), PropertyReader.getProperty("imeji.escidoc.password"));

				}

				method.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
				method.addRequestHeader("Cache-Control", "public");
				method.setRequestHeader("Connection", "close"); 

				// Execute the method with HttpClient.
				HttpClient client = new HttpClient();
				ProxyHelper.setProxy(client, frameworkUrl);
				client.executeMethod(method);

				//byte[] input;
				InputStream input;
				OutputStream out = resp.getOutputStream();

				if (method.getStatusCode() == 302)
				{
					//try again
					method.releaseConnection();
					userHandle = LoginHelper.login(PropertyReader.getProperty("imeji.escidoc.user"), PropertyReader.getProperty("imeji.escidoc.password"));
					method = new GetMethod(imageUrl);
					method.setFollowRedirects(true);
					method.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
					client.executeMethod(method);

				}
				if (method.getStatusCode() != 200)
				{
					ProxyHelper.setProxy(client, PropertyReader.getProperty("escidoc.faces.instance.url"));
					method = new GetMethod(PropertyReader.getProperty("escidoc.faces.instance.url") + "/resources/icon/defaultThumb.gif");
					client.executeMethod(method);

					out = resp.getOutputStream();

					if (method.getStatusCode() == 302)
					{
						method.releaseConnection();
						throw new RuntimeException("error code " + method.getStatusCode());
					}
					input =  method.getResponseBodyAsStream();
				}
				else
				{
					for (Header header : method.getResponseHeaders())
					{
						resp.setHeader(header.getName(), header.getValue());
					}
					input = method.getResponseBodyAsStream();

				}

				byte[] buffer = new byte[2048];
				int numRead;
				long numWritten = 0;
				while ((numRead = input.read(buffer)) != -1)
				{
					out.write(buffer, 0, numRead);
					//out.flush();
					numWritten += numRead;
				}
				input.close();

				method.releaseConnection();
				out.flush();
				out.close();
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			if (method != null) method.releaseConnection();
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
