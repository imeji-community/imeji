/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.storage.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.PNGDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

import de.mpg.imeji.logic.storage.Storage.FileResolution;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * Mehtods to help wotk with images
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImageUtils
{
    private static Logger logger = Logger.getLogger(ImageUtils.class);
    /**
     * If true, the rescale will keep the better quality of the images
     */
    private static boolean RESCALE_HIGH_QUALITY = true;

    /**
     * Resize an image (only for jpeg) to the given {@link FileResolution}
     * 
     * @param bytes
     * @param resolution
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static byte[] resizeJPEG(byte[] bytes, FileResolution resolution) throws IOException, Exception
    {
        // If it is original resolution, don't touch the file, otherwise resize
        if (!FileResolution.ORIGINAL.equals(resolution))
        {
            BufferedImage image = JpegUtils.readJpeg(bytes);
            bytes = toBytes(scaleImage(image, resolution), StorageUtils.getMimeType("jpg"));
        }
        return bytes;
    }

    /**
     * Transform an image in jpeg. Useful to reduce size of thumbnail and web resolution images. If the format of the
     * image is not supported, return null
     * 
     * @param bytes
     * @param mimeType
     * @return
     */
    public static byte[] toJpeg(byte[] bytes, String mimeType)
    {
        try
        {
            if (mimeType.equals(StorageUtils.getMimeType("tif")))
            {
                return ImageUtils.tiff2Jpeg(bytes);
            }
            else if (mimeType.equals(StorageUtils.getMimeType("png")))
            {
                return ImageUtils.png2Jpeg(bytes);
            }
            else if (mimeType.equals(StorageUtils.getMimeType("bmp")))
            {
                return ImageUtils.bmp2Jpeg(bytes);
            }
            else if (mimeType.equals(StorageUtils.getMimeType("gif")))
            {
                return GifUtils.toJPEG(bytes);
            }
            else if (mimeType.equals(StorageUtils.getMimeType("jpg")))
            {
                return bytes;
            }
        }
        catch (Exception e)
        {
            logger.info("Image could not be compressed: ", e);
        }
        return null;
    }


    /**
     * Scale a {@link BufferedImage} to new size. Is faster than the basic {@link ImageUtils}.scaleImage method, has the
     * same quality. If it is a thumbnail, cut the images to fit into the raster
     * 
     * @param image original image
     * @param size the size to be resized to
     * @param resolution the type of the image. Might be thumb or web
     * @return the resized images
     * @throws Exception
     */
    private static BufferedImage scaleImageFast(BufferedImage image, int size, FileResolution resolution)
            throws Exception
    {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        BufferedImage newImg = null;
        Image rescaledImage;
        if (width > height)
        {
            if (FileResolution.THUMBNAIL.equals(resolution))
            {
                newImg = new BufferedImage(height, height, BufferedImage.TYPE_INT_RGB);
                Graphics g1 = newImg.createGraphics();
                g1.drawImage(image, (height - width) / 2, 0, null);
                if (height > size)
                    rescaledImage = getScaledInstance(newImg, size, size, RenderingHints.VALUE_INTERPOLATION_BILINEAR,
                            RESCALE_HIGH_QUALITY);
                else
                    rescaledImage = newImg;
            }
            else
                rescaledImage = getScaledInstance(image, size, height * size / width,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR, RESCALE_HIGH_QUALITY);
        }
        else
        {
            if (FileResolution.THUMBNAIL.equals(resolution))
            {
                newImg = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
                Graphics g1 = newImg.createGraphics();
                g1.drawImage(image, 0, (width - height) / 2, null);
                if (width > size)
                    rescaledImage = getScaledInstance(newImg, size, size, RenderingHints.VALUE_INTERPOLATION_BILINEAR,
                            RESCALE_HIGH_QUALITY);
                else
                    rescaledImage = newImg;
            }
            else
                rescaledImage = getScaledInstance(image, width * size / height, size,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR, RESCALE_HIGH_QUALITY);
        }
        BufferedImage rescaledBufferedImage = new BufferedImage(rescaledImage.getWidth(null),
                rescaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g2 = rescaledBufferedImage.getGraphics();
        g2.drawImage(rescaledImage, 0, 0, null);
        return rescaledBufferedImage;
    }

    /**
     * Convenience method that returns a scaled instance of the provided {@link BufferedImage}.
     * 
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance, in pixels
     * @param targetHeight the desired height of the scaled instance, in pixels
     * @param hint one of the rendering hints that corresponds to {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *            {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *            {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *            {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step scaling technique that provides higher quality
     *            than the usual one-step technique (only useful in downscaling cases, where {@code targetWidth} or
     *            {@code targetHeight} is smaller than the original dimensions, and generally only when the
     *            {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@link BufferedImage}
     */
    private static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint,
            boolean higherQuality)
    {
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        int w, h;
        if (higherQuality)
        {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        }
        else
        {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }
        do
        {
            if (higherQuality && w > targetWidth)
            {
                w /= 2;
                if (w < targetWidth)
                {
                    w = targetWidth;
                }
            }
            if (higherQuality && h > targetHeight)
            {
                h /= 2;
                if (h < targetHeight)
                {
                    h = targetHeight;
                }
            }
            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();
            ret = tmp;
        }
        while (w != targetWidth || h != targetHeight);
        return ret;
    }

    /**
     * Transform a tiff image into a jpeg image
     * 
     * @param bytes
     * @return
     */
    private static byte[] tiff2Jpeg(byte[] bytes)
    {
        File tiffFile = null;
        try
        {
            tiffFile = File.createTempFile("upload", "tif.tmp");
            FileUtils.writeByteArrayToFile(tiffFile, bytes);
            SeekableStream s = new FileSeekableStream(tiffFile);
            TIFFDecodeParam param = new TIFFDecodeParam();
            ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
            return image2Jpeg(tiffFile, dec);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error transforming tiff to jpeg", e);
        }
        finally
        {
            FileUtils.deleteQuietly(tiffFile);
        }
    }

    /**
     * Transform a png image into a jpeg image
     * 
     * @param bytes
     * @return
     */
    private static byte[] png2Jpeg(byte[] bytes)
    {
        File pngFile = null;
        try
        {
            pngFile = File.createTempFile("uploadPng2Jpg", ".png");
            FileUtils.writeByteArrayToFile(pngFile, bytes);
            SeekableStream s = new FileSeekableStream(pngFile);
            PNGDecodeParam param = new PNGDecodeParam();
            ImageDecoder dec = ImageCodec.createImageDecoder("png", s, param);
            return image2Jpeg(pngFile, dec);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error transforming png to jpeg", e);
        }
        finally
        {
            FileUtils.deleteQuietly(pngFile);
        }
    }

    /**
     * Transform a bmp image into a jpeg image
     * 
     * @param bytes
     * @return
     */
    private static byte[] bmp2Jpeg(byte[] bytes)
    {
        return image2Jpeg(bytes);
    }

    /**
     * Transform a image to a jpeg image. The input image must have a format supported by {@link ImageIO}
     * 
     * @param bytes
     * @return
     */
    private static byte[] image2Jpeg(byte[] bytes)
    {
        try
        {
            InputStream ins = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(ins);
            ByteArrayOutputStream ous = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", ous);
            return ous.toByteArray();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error transforming image to jpeg", e);
        }
    }

    /**
     * Transform a image {@link File} to a jpeg file
     * 
     * @param f - the {@link File} where the image is
     * @param dec - The {@link ImageDecoder} needed to decode the passed image
     * @return the image as {@link Byte} array
     */
    private static byte[] image2Jpeg(File f, ImageDecoder dec)
    {
        File jpgFile = null;
        try
        {
            RenderedImage ri = dec.decodeAsRenderedImage(0);
            jpgFile = File.createTempFile("uploadImage2Jpeg", "jpg");
            FileOutputStream fos = new FileOutputStream(jpgFile);
            JPEGEncodeParam jParam = new JPEGEncodeParam();
            jParam.setQuality(1.0f);
            ImageEncoder imageEncoder = ImageCodec.createImageEncoder("JPEG", fos, jParam);
            imageEncoder.encode(ri);
            fos.flush();
            byte[] bytes = FileUtils.readFileToByteArray(jpgFile);
            // Return the bytes
            return bytes;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error transforming image file to jpeg", e);
        }
        finally
        {
            FileUtils.deleteQuietly(jpgFile);
        }
    }

    /**
     * Return the format of an image (jpg, tif), according to its mime-type
     * 
     * @param mimeType
     * @return
     */
    public static String getImageFormat(String mimeType)
    {
        if (mimeType.equals(StorageUtils.getMimeType("tif")))
        {
            return "tif";
        }
        return mimeType.toLowerCase().replaceAll("image/", "");
    }

    /**
     * TRansform a {@link BufferedImage} to a {@link Byte} array
     * 
     * @param image
     * @param mimeType
     * @return
     * @throws IOException
     */
    public static byte[] toBytes(BufferedImage image, String mimeType) throws IOException
    {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        // use imageIO.write to encode the image back into a byte[]
        ImageIO.write(image, ImageUtils.getImageFormat(mimeType), byteOutput);
        return byteOutput.toByteArray();
    }

    /**
     * cale the image if too big for the size
     * 
     * @param image
     * @param resolution
     * @return
     * @throws Exception
     */
    public static BufferedImage scaleImage(BufferedImage image, FileResolution resolution) throws Exception
    {
        BufferedImage bufferedImage = null;
        int size = getResolution(resolution);
        if (image.getWidth() > size || image.getHeight() > size)
        {
            bufferedImage = scaleImageFast(image, size, resolution);
        }
        else
        {
            bufferedImage = image;
        }
        return bufferedImage;
    }

    /**
     * Return the maximum size of an image according to its {@link FileResolution}. The values are defined in the
     * properties
     * 
     * @param FileResolution
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static int getResolution(FileResolution resolution) throws IOException, URISyntaxException
    {
        switch (resolution)
        {
            case THUMBNAIL:
                return Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail"));
            case WEB:
                return Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web"));
            default:
                return 0;
        }
    }

    /**
     * Return the property xsd.metadata.content-category.thumbnail
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String getEscidocThumbnailContentCategory() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("xsd.metadata.content-category.thumbnail");
    }

    /**
     * Return the property xsd.metadata.content-category.web-resolution
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String getEscidocWebContentCategory() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("xsd.metadata.content-category.web-resolution");
    }

    /**
     * Return the property xsd.metadata.content-category.original-resolution
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String getEscidocOriginalContentCategory() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("xsd.metadata.content-category.original-resolution");
    }
}
