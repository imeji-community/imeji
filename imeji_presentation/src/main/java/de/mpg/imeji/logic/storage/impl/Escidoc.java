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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.axis.holders.ImageHolder;

import com.sun.media.jai.util.ImageUtil;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.escidoc.core.resources.om.item.Item;
import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.UploadResult;
import de.mpg.imeji.logic.storage.escidoc.EscidocUtils;
import de.mpg.imeji.logic.storage.util.ImageUtils;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * eSciDoc Storage implementation. Implements {@link Storage} with eSciDoc Methods
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Escidoc implements Storage
{
    private final String name = "escidoc";
    private EscidocUtils util;
    private Authentication auth;
    private Item item;

    /**
     * Constructor for {@link Escidoc}
     */
    public Escidoc()
    {
        util = new EscidocUtils();
        try
        {
            auth = util.login();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Logging in eSciDoc: ", e);
        }
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
    public UploadResult upload(String filename, byte[] bytes)
    {
        String mimeType = StorageUtils.getMimeType(StorageUtils.getFileExtension(filename));
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
            UploadResult result = new UploadResult(EscidocUtils.getOriginalResolution(item),
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
    public byte[] read(String url)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.storage.Storage#delete(java.lang.String)
     */
    @Override
    public void delete(String url)
    {
        // TODO Auto-generated method stub
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
        URL url = uploadViaStagingArea(transformFile(bytes, resolution, mimeType));
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
     * Prepare the image for the upload: <br/>
     * if it is original image upload, do nothing <br/>
     * if it is another resolution, resize it <br/>
     * if it is a tiff to be resized, transformed it to jpeg and resize it
     * 
     * @param stream
     * @param contentCategory
     * @param format
     * @return
     * @throws IOException
     * @throws Exception
     */
    private byte[] transformFile(byte[] bytes, FileResolution resolution, String mimeType) throws IOException,
            Exception
    {
        if (!FileResolution.ORIGINAL.equals(resolution))
        {
            byte[] compressed = ImageUtils.compressImage(bytes, mimeType);
            if (!Arrays.equals(compressed, bytes))
            {
                mimeType = StorageUtils.getMimeType("jpg");
            }
            bytes = ImageUtils.scaleImage(ImageIO.read(new ByteArrayInputStream(compressed)), mimeType, resolution);
        }
        return bytes;
    }
}
