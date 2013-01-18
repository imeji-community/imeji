package de.mpg.imeji.presentation.upload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.upload.helper.ImageHelper;
import de.mpg.imeji.presentation.upload.uploader.Uploader;
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
    private Uploader uploader;
    private static Logger logger = Logger.getLogger(UploadManager.class);

    /**
     * Construct a new {@link UploadManager} with an {@link Uploader} to upload files of an {@link Item}
     * 
     * @param uploader
     */
    public UploadManager(Uploader uploader)
    {
        this.uploader = uploader;
    }

    /**
     * Upload a file for the 3 imeji resolution (original, web, thumbnail). Use the {@link Uploader} defined in the
     * constructor. If some parameters needs to be give back, it should be done throught the {@link Uploader}
     * 
     * @param is - {@link InputStream} from the uploader compoment
     * @param format - the image format (image/jpg, image/png, etc)
     * @throws IOException
     * @throws URISyntaxException
     */
    public void uploadItemFiles(InputStream is) throws IOException, URISyntaxException
    {
        byte[] stream = inputStreamToByteArray(is);
        uploadFile(stream, ImageHelper.getOrig());
        uploadFile(stream, ImageHelper.getWeb());
        uploadFile(stream, ImageHelper.getThumb());
    }

    /**
     * Upload one single File in the repository defined throught the {@link Uploader}.
     * 
     * @param stream
     * @param contentCategory
     * @param format
     */
    private void uploadFile(byte[] stream, String contentCategory)
    {
        try
        {
            stream = prepareImageForUpload(stream, contentCategory, uploader.getMimetype());
            uploader.upload(stream, contentCategory);
        }
        catch (Exception e)
        {
            logger.error("Error transforming image", e);
            uploader.upload(getDefaultThumbnailAsByteArray(), contentCategory);
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
    public byte[] prepareImageForUpload(byte[] stream, String contentCategory, String mimeType) throws IOException,
            Exception
    {
        if (!contentCategory.equals(ImageHelper.getOrig()))
        {
            byte[] compressed = compressImage(stream, mimeType);
            if (!Arrays.equals(compressed, stream))
            {
                mimeType = ImageHelper.getMimeType("jpg");
            }
            stream = scaleImage(ImageIO.read(new ByteArrayInputStream(compressed)), mimeType, contentCategory);
        }
        return stream;
    }

    /**
     * Compress an image in jpeg. Useful to reduce size of thumbnail and web resolution images
     * 
     * @param bytes
     * @param mimeType
     * @return
     */
    private byte[] compressImage(byte[] bytes, String mimeType)
    {
        if (mimeType.equals(ImageHelper.getMimeType("tif")))
        {
            bytes = ImageHelper.tiff2Jpeg(bytes);
        }
        else if (mimeType.equals(ImageHelper.getMimeType("png")))
        {
            bytes = ImageHelper.png2Jpeg(bytes);
        }
        else if (mimeType.equals(ImageHelper.getMimeType("bmp")))
        {
            bytes = ImageHelper.bmp2Jpeg(bytes);
        }
        else if (mimeType.equals(ImageHelper.getMimeType("gif")))
        {
             bytes = ImageHelper.gif2Jpeg(bytes);
        }
        return bytes;
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
     * @param mimeType
     * @param contentCategory
     * @return
     * @throws Exception
     */
    private byte[] scaleImage(BufferedImage image, String mimeType, String contentCategory) throws Exception
    {
        BufferedImage bufferedImage = null;
        int size = getResolution(contentCategory);
        if (image.getWidth() > size || image.getHeight() > size)
        {
            bufferedImage = ImageHelper.scaleImageFast(image, size, contentCategory);
        }
        else
        {
            bufferedImage = image;
        }
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        // use imageIO.write to encode the image back into a byte[]
        ImageIO.write(bufferedImage, ImageHelper.getImageFormat(mimeType), byteOutput);
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
    public byte[] inputStreamToByteArray(InputStream inputStream)
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

    /**
     * The {@link Uploader} used by the {@link UploadManager} to upload the files
     * 
     * @return
     */
    public Uploader getUploader()
    {
        return uploader;
    }
}
