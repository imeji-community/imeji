package de.mpg.imeji.presentation.upload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.resources.om.item.Item;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.escidoc.EscidocHelper;
import de.mpg.imeji.presentation.upload.helper.ImageHelper;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * Manage the Upload of the imeji files into a repository
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UploadManager
{
    /**
     * Upload all the files (for the 3 resolution) of an {@link Item}
     * 
     * @param item
     * @param inputStream the file
     * @param fileName
     * @param mimetype
     * @param format
     * @param auth
     * @return
     * @throws URISyntaxException
     * @throws Exception
     */
    public Item uploadInEscidoc(Item item, InputStream inputStream, String fileName, String mimetype, String format,
            Authentication auth) throws URISyntaxException, Exception
    {
        byte[] imageStream = inputStreamToByteArray(inputStream);
        EscidocHelper escidocHelper = new EscidocHelper();
        item = escidocHelper.uploadFile(item, ImageHelper.getOrig(), imageStream, fileName, mimetype, format, auth);
        item = escidocHelper.uploadFile(item, ImageHelper.getWeb(), imageStream, fileName, mimetype, format, auth);
        item = escidocHelper.uploadFile(item, ImageHelper.getThumb(), imageStream, fileName, mimetype, format, auth);
        return item;
    }

    /**
     * Prepare the image for the upload: <br/>
     * if it is original image upload, do nothing <br/>
     * if it is another resolution, resize it <br/>
     * if it is a tiff to be resized, transformed it to jpeg and resize it
     * 
     * @param imageStream
     * @param contentCategory
     * @param format
     * @return
     * @throws IOException
     * @throws Exception
     */
    public byte[] prepareImageForUpload(byte[] imageStream, String contentCategory, String format) throws IOException,
            Exception
    {
        if (!contentCategory.equals(ImageHelper.getOrig()))
        {
            if (format.equalsIgnoreCase("tif"))
            {
                // transform tiff image to a jpeg
                imageStream = ImageHelper.tiff2Jpeg(imageStream);
            }
            imageStream = scaleImage(ImageIO.read(new ByteArrayInputStream(imageStream)), format, contentCategory);
        }
        return imageStream;
    }

    /**
     * Return the {@link Byte} array of the default Thumbnail used when an error occured
     * 
     * @return
     */
    public byte[] getDefaultThumbnailAsByteArray()
    {
        try
        {
            Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
            String test = navigation.getApplicationUrl() + "resources/icon/defaultThumb.gif";
            URL noThumbUrl = new URL(test);
            int contentLength = noThumbUrl.openConnection().getContentLength();
            InputStream openStream = noThumbUrl.openStream();
            byte[] data = new byte[contentLength];
            openStream.read(data);
            openStream.close();
            return data;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Scale the image if too big for the size
     * 
     * @param image
     * @param size
     * @param resolution
     * @param format
     * @param contentCategory
     * @return
     * @throws Exception
     */
    private byte[] scaleImage(BufferedImage image, String format, String contentCategory) throws Exception
    {
        BufferedImage bufferedImage = null;
        int size = getResolution(contentCategory);
        if (image.getWidth() > size || image.getHeight() > size)
        {
            bufferedImage = ImageHelper.scaleImageFast(image, size, contentCategory);
        }
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        // use imageIO.write to encode the image back into a byte[]
        ImageIO.write(bufferedImage, format, byteOutput);
        return byteOutput.toByteArray();
    }

    /**
     * Return the maximum size of an image according to its content category. The values are defined in the properties
     * 
     * @param contentCategory
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private int getResolution(String contentCategory) throws IOException, URISyntaxException
    {
        if (contentCategory.equals(ImageHelper.getWeb()))
        {
            return Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web"));
        }
        else if (contentCategory.equals(ImageHelper.getThumb()))
        {
            return Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail"));
        }
        return 0;
    }

    /**
     * Transform an {@link InputStream} to a {@link Byte} array
     * 
     * @param inputStream
     * @return
     */
    private static byte[] inputStreamToByteArray(InputStream inputStream)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        try
        {
            while ((b = inputStream.read()) != -1)
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
}
