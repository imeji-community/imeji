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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.storage.Storage.FileResolution;
import de.mpg.imeji.logic.storage.transform.impl.MagickImageGenerator;
import de.mpg.imeji.logic.storage.transform.impl.MicroscopeImageGenerator;
import de.mpg.imeji.logic.storage.transform.impl.PdfImageGenerator;
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
        generators.add(new XuggleImageGenerator());
        generators.add(new MagickImageGenerator());
        generators.add(new SimpleImageGenerator());
        generators.add(new MicroscopeImageGenerator());
    }

    /**
     * Generate a Thumbnail image for imeji
     * 
     * @param bytes
     * @param extension
     * @return
     */
    public byte[] generateThumbnail(byte[] bytes, String extension)
    {
        return generate(bytes, extension, FileResolution.THUMBNAIL);
    }

    /**
     * Generate a Web resolution image for imeji
     * 
     * @param bytes
     * @param extension
     * @return
     */
    public byte[] generateWebResolution(byte[] bytes, String extension)
    {
        return generate(bytes, extension, FileResolution.WEB);
    }

    /**
     * Generate an image (only jpg and gif supported here) into a smaller image according to the {@link FileResolution}
     * 
     * @param bytes
     * @param extension
     * @param resolution
     * @return
     */
    public byte[] generate(byte[] bytes, String extension, FileResolution resolution)
    {
        if (StorageUtils.compareExtension("gif", extension))
        {
            return generateGif(bytes, extension, resolution);
        }
        return generateJpeg(bytes, extension, resolution);
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
            throw new RuntimeException("Error transforming gif:", e);
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
    private byte[] generateJpeg(byte[] bytes, String extension, FileResolution resolution)
    {
        try
        {
            return ImageUtils.resizeJPEG(toJpeg(bytes, extension), resolution);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error transforming image:", e);
        }
    }

    /**
     * Uses the {@link ImageGenerator} to transform the bytes into a jpg
     * 
     * @param bytes
     * @param extension
     * @return
     */
    private byte[] toJpeg(byte[] bytes, String extension)
    {
        byte[] jpeg = null;
        Iterator<ImageGenerator> it = generators.iterator();
        while (it.hasNext() && jpeg == null)
        {
            try
            {
                ImageGenerator imageGenerator = (ImageGenerator)it.next();
                jpeg = imageGenerator.generateJPG(bytes, extension);
            }
            catch (Exception e)
            {
                logger.debug("Error generating image", e);
            }
        }
        if (jpeg == null)
            throw new RuntimeException("Unsupported file format (requested was " + extension + ")");
        return jpeg;
    }
}
