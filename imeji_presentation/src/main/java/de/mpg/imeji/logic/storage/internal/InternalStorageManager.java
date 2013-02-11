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
package de.mpg.imeji.logic.storage.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.imeji.logic.storage.Storage.FileResolution;
import de.mpg.imeji.logic.storage.util.ImageUtils;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * Manage internal storage in file system
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class InternalStorageManager
{
    /**
     * The directory path where files are stored
     */
    private final String storagePath;
    /**
     * The URL used to access the storage (this is a dummy url, used by the internal storage to parse file location)
     */
    private final String storageUrl = "http://localhost/imeji/file/";
    private static Logger logger = Logger.getLogger(InternalStorageManager.class);

    /**
     * Default Constructor
     */
    public InternalStorageManager()
    {
        try
        {
            storagePath = PropertyReader.getProperty("imeji.storage.path");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Internal storage couldn't be initialized!!!!!", e);
        }
    }

    /**
     * Add a new file to the internal storage
     * 
     * @param bytes
     * @param filename
     * @return
     */
    public InternalStorageItem addFile(byte[] bytes, String filename)
    {
        InternalStorageItem item = newItem(filename);
        try
        {
            return writeItemFiles(item, bytes);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error writing file in internal storage " + storagePath, e);
        }
    }

    /**
     * Remove a file from the internal storage
     * 
     * @param item
     */
    public void removeFile(InternalStorageItem item)
    {
        if (item.getId() != null && !item.getId().trim().equals(""))
        {
            File file = new File(storagePath + item.getId());
            if (!FileUtils.deleteQuietly(file))
            {
                logger.error("File " + file.getAbsolutePath() + " could not be deleted!!!");
            }
        }
    }

    /**
     * Create an {@link InternalStorageItem} for one file
     * 
     * @param filename
     * @return
     */
    private InternalStorageItem newItem(String filename)
    {
        String id = UUID.randomUUID().toString();
        InternalStorageItem item = new InternalStorageItem();
        item.setId(id);
        item.setFileName(filename);
        item.setOrignalPath(getPath(id, filename, FileResolution.ORIGINAL));
        item.setThumbnailPath(getPath(id, filename, FileResolution.THUMBNAIL));
        item.setWebPath(getPath(id, filename, FileResolution.WEB));
        return item;
    }

    /**
     * Create a path for a file according to its {@link FileResolution}
     * 
     * @param id
     * @param filename
     * @param resolution
     * @return
     */
    private String getPath(String id, String filename, FileResolution resolution)
    {
        return id + "/" + resolution.name().toLowerCase() + "/" + filename;
    }

    /**
     * Extract the file system path from the url
     * 
     * @param url
     * @return
     */
    public String transformUrlToPath(String url)
    {
        return url.replace(storageUrl, storagePath);
    }
    
    /**
     * Transform the path of the item into a path
     * @param path
     * @return
     */
    public String transformPathToUrl(String path)
    {
        return path.replace(storagePath, storageUrl).replace("\\", "/");
    }

    /**
     * Write a new file for the 3 resolution of one file
     * 
     * @param item
     * @param bytes
     * @throws IOException
     * @throws Exception
     */
    private InternalStorageItem writeItemFiles(InternalStorageItem item, byte[] bytes) throws IOException, Exception
    {
        item.setOrignalPath(write(bytes, item.getOrignalPath()));
        item.setWebPath(write(
                ImageUtils.transformImage(bytes, FileResolution.WEB,
                        StorageUtils.getMimeType(StorageUtils.getFileExtension(item.getFileName()))), item.getWebPath()));
        item.setThumbnailPath(write(
                ImageUtils.transformImage(bytes, FileResolution.THUMBNAIL,
                        StorageUtils.getMimeType(StorageUtils.getFileExtension(item.getFileName()))),
                item.getThumbnailPath()));
        return item;
    }

    /**
     * Write the bytes in the filesystem
     * 
     * @param bytes
     * @param path
     * @return
     * @throws IOException
     */
    private String write(byte[] bytes, String path) throws IOException
    {
        File file = new File(storagePath + path);
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        }
        else
        {
            throw new RuntimeException("File " + path + " already exists in internal storage!");
        }
    }

    /**
     * @return the storageUrl
     */
    public String getStorageUrl()
    {
        return storageUrl;
    }
}
