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
package de.mpg.imeji.logic.storage.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.resources.om.item.Item;
import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.UploadResult;
import de.mpg.imeji.logic.storage.administrator.StorageAdministrator;
import de.mpg.imeji.logic.storage.escidoc.EscidocUtils;
import de.mpg.imeji.logic.storage.util.ImageUtils;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * eSciDoc Storage implementation. Implements {@link Storage} with eSciDoc Methods
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class EscidocStorage implements Storage
{
    private final String name = "escidoc";
    private EscidocUtils util;
    private Authentication auth;
    private Item item;
    private HttpClient client;
    private static Logger logger = Logger.getLogger(EscidocStorage.class);

    /**
     * Constructor for {@link EscidocStorage}
     */
    public EscidocStorage()
    {
        util = new EscidocUtils();
        login();
        client = StorageUtils.getHttpClient();
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#upload(byte[])
     */
    @Override
    public UploadResult upload(String filename, byte[] bytes, String collectionId)
    {
        String mimeType = StorageUtils.getMimeType(StringHelper.getFileExtension(filename));
        try
        {
            // Construct the Item
            item = util.itemFactory(PropertyReader.getProperty("escidoc.imeji.content-model.id"),
                    PropertyReader.getProperty("escidoc.imeji.context.id"));
            // Upload the Files for the 3 resolution
            uploadFileAndAdditToItem(bytes, FileResolution.ORIGINAL, mimeType, filename);
            uploadFileAndAdditToItem(bytes, FileResolution.WEB, mimeType, filename);
            uploadFileAndAdditToItem(bytes, FileResolution.THUMBNAIL, mimeType, filename);
            // Create the item
            item = util.createItemInEscidoc(item, auth);
            // Create the Upload result
            UploadResult result = new UploadResult(item.getObjid(), EscidocUtils.getOriginalResolution(item),
                    EscidocUtils.getWebResolutionUrl(item), EscidocUtils.getThumbnailUrl(item));
            return result;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Uploading image in eSciDoc: ", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#read(java.lang.String)
     */
    @Override
    public void read(String url, OutputStream out)
    {
        GetMethod get = StorageUtils.newGetMethod(client, url);
        get.addRequestHeader("Cookie", getEscidocCookie());
        try
        {
            client.executeMethod(get);
            if (get.getStatusCode() == 302)
            {
                // Login in escidoc is not valid anymore, log in again an read again
                get.releaseConnection();
                login();
                get = StorageUtils.newGetMethod(client, url);
                get.addRequestHeader("Cookie", getEscidocCookie());
                client.executeMethod(get);
            }
            StorageUtils.writeInOut(get.getResponseBodyAsStream(), out);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading " + url + " from escidoc: ", e);
        }
        finally
        {
            get.releaseConnection();
        }
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#delete(java.lang.String)
     */
    @Override
    public void delete(String id)
    {
        ItemHandlerClient handler = new ItemHandlerClient(auth.getServiceAddress());
        handler.setHandle(auth.getHandle());
        try
        {
            handler.delete(id);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error deleting escidoc Item: " + id, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#update(java.lang.String, byte[])
     */
    @Override
    public void update(String url, byte[] bytes)
    {
        // TODO Auto-generated method stub
    }

    /**
     * Ipalod the file in escidoc and add it to the {@link Item}
     * 
     * @param bytes
     * @param resolution
     * @param mimeType
     * @param filename
     * @throws IOException
     * @throws Exception
     */
    private void uploadFileAndAdditToItem(byte[] bytes, FileResolution resolution, String mimeType, String filename)
            throws IOException, Exception
    {
        // Transform the file if needed (according to the resolution), and uplod it
        URL url = uploadViaStagingArea(ImageUtils.transformImage(bytes, resolution, mimeType));
        // Update the item with the uploaded file
        util.addImageToEscidocItem(item, url, util.getContentCategory(resolution), filename, mimeType);
    }

    /**
     * Upload a {@link Byte} in the staging area of eSciDoc. Return the {@link URL} of the file
     * 
     * @param stream
     * @return
     */
    private URL uploadViaStagingArea(byte[] bytes)
    {
        try
        {
            StagingHandlerClient handler = new StagingHandlerClient(auth.getServiceAddress());
            handler.setHandle(auth.getHandle());
            return handler.upload(new ByteArrayInputStream(bytes));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error uploading file into eSciDoc staging area", e);
        }
    }

    /**
     * Get the current handle, if null get a new one
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws Exception
     */
    private String getEscidocCookie()
    {
        if (auth.getHandle() == null)
        {
            login();
        }
        return "escidocCookie=" + auth.getHandle();
    }

    /**
     * Log in in eSciDoc
     */
    private void login()
    {
        try
        {
            logger.info("Logging in escidoc");
            auth = util.login();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Logging in eSciDoc: ", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#getAdminstrator()
     */
    @Override
    public StorageAdministrator getAdministrator()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#getCollectionId(java.lang.String)
     */
    @Override
    public String getCollectionId(String url)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
