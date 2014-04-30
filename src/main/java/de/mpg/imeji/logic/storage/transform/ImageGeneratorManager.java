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
package de.mpg.imeji.logic.storage.transform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.mpg.imeji.logic.storage.Storage.FileResolution;
import de.mpg.imeji.logic.storage.transform.impl.MagickImageGenerator;
import de.mpg.imeji.logic.storage.transform.impl.MicroscopeImageGenerator;
import de.mpg.imeji.logic.storage.transform.impl.PdfImageGenerator;
import de.mpg.imeji.logic.storage.transform.impl.RawFileImageGenerator;
import de.mpg.imeji.logic.storage.transform.impl.SimpleAudioImageGenerator;
import de.mpg.imeji.logic.storage.transform.impl.SimpleImageGenerator;
import de.mpg.imeji.logic.storage.transform.impl.XuggleImageGenerator;
import de.mpg.imeji.logic.storage.util.GifUtils;
import de.mpg.imeji.logic.storage.util.ImageUtils;
import de.mpg.imeji.logic.storage.util.StorageUtils;

/**
 * Implements all process to generate the images
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImageGeneratorManager
{
    private List<ImageGenerator> generators = null;
    private static Logger logger = Logger.getLogger(ImageGeneratorManager.class);

    /**
     * Default constructor of {@link ImageGeneratorManager}
     */
    public ImageGeneratorManager()
    {
        generators = new ArrayList<ImageGenerator>();
        generators.add(new PdfImageGenerator());
        generators.add(new SimpleAudioImageGenerator());
        generators.add(new MagickImageGenerator());
        generators.add(new XuggleImageGenerator());
        generators.add(new SimpleImageGenerator());
        generators.add(new MicroscopeImageGenerator());
        generators.add(new RawFileImageGenerator());
    }

    /**
     * Generate a Thumbnail image for imeji
     * 
     * @param bytes
     * @param extension
     * @return
     */
    public byte[] generateThumbnail(File file, String extension)
    {
        return generate(file, extension, FileResolution.THUMBNAIL);
    }

    /**
     * Generate a Web resolution image for imeji
     * 
     * @param bytes
     * @param extension
     * @return
     */
    public byte[] generateWebResolution(File file, String extension)
    {
        return generate(file, extension, FileResolution.WEB);
    }

    /**
     * Generate an image (only jpg and gif supported here) into a smaller image according to the {@link FileResolution}
     * 
     * @param bytes
     * @param extension
     * @param resolution
     * @return
     */
    public byte[] generate(File file, String extension, FileResolution resolution)
    {
        if (StorageUtils.compareExtension("gif", extension))
        {
            try
            {
                return generateGif(FileUtils.readFileToByteArray(file), extension, resolution);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return generateJpeg(file, extension, resolution);
    }

    /**
     * Generate an animated gif in the wished size. The file must be a gif, otherwise an exception is thrown
     * 
     * @param bytes
     * @param extension
     * @param resolution
     * @return
     */
    private byte[] generateGif(byte[] bytes, String extension, FileResolution resolution)
    {
        try
        {
            return GifUtils.resizeAnimatedGif(bytes, resolution);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error transforming gif", e);
        }
    }

    /**
     * Generate an jpeg image in the wished size.
     * 
     * @param bytes
     * @param extension
     * @param resolution
     * @return
     */
    private byte[] generateJpeg(File file, String extension, FileResolution resolution)
    {
        // Make a jpg out of the file
        byte[] bytes = toJpeg(file, extension);
        try
        {
            return ImageUtils.resizeJPEG(bytes, resolution);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error transforming image", e);
        }
    }

    /**
     * Uses the {@link ImageGenerator} to transform the bytes into a jpg
     * 
     * @param bytes
     * @param extension
     * @return
     */
    private byte[] toJpeg(File file, String extension)
    {
        byte[] jpeg = null;
        Iterator<ImageGenerator> it = generators.iterator();
        if (StorageUtils.compareExtension(extension, "jpg"))
            try
            {
                return FileUtils.readFileToByteArray(file);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        while (it.hasNext() && (jpeg == null || jpeg.length == 0))
        {
            try
            {
                ImageGenerator imageGenerator = it.next();
                jpeg = imageGenerator.generateJPG(file, extension);
            }
            catch (Exception e)
            {
                logger.warn("Error generating image", e);
            }
        }
        if (jpeg == null || jpeg.length == 0)
            throw new RuntimeException("Unsupported file format (requested was " + extension + ")");
        return jpeg;
    }
}
