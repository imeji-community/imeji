package de.mpg.imeji.upload.helper;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.naming.InitialContext;
import org.ajax4jsf.resource.image.animatedgif.GifDecoder;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.xmlbeans.XmlCursor;
import de.escidoc.schemas.components.x09.ComponentDocument.Component;
import de.escidoc.schemas.components.x09.ComponentDocument.Component.Content.Storage.Enum;
import de.escidoc.schemas.components.x09.PropertiesDocument.Properties;
import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.escidoc.ItemVO;


public class ImageHelper{
	
    public static Item setComponent(String contentCategory, Item item, byte[] imageStream, String fileName,
            String mimetype, String format, String userHandle) throws Exception{

        URL url = null;
        if (item.getComponents() == null){
            item.addNewComponents();
        }
        Component component = item.getComponents().addNewComponent();
        component.addNewContent();
        component.addNewProperties();
        component.getProperties().addNewValidStatus();
        Properties props = component.getProperties();
        props.addNewVisibility();
        XmlCursor propCursor = component.getProperties().getVisibility().newCursor();
        propCursor.toEndToken();
        propCursor.insertChars("private");
        component.getProperties().setFileName(fileName);
        component.getProperties().setMimeType(mimetype);
        
        byte[] scaledImageStream = null;
        if (contentCategory.equals(getThumb())){        
	        BufferedImage bufferedImage;
        	try
        	{
        		bufferedImage= ImageIO.read( new ByteArrayInputStream(imageStream));
        	}
        	catch(Exception e){
        		bufferedImage = cmykRasterToSRGB(imageStream, format);
        	}

	        if(bufferedImage.getWidth() > Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail"))|| bufferedImage.getWidth() > Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail"))){
	        	bufferedImage = scaleImage(bufferedImage, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail")));
	        }
	        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
	        // use imageIO.write to encode the image back into a byte[]
	        ImageIO.write(bufferedImage, format, byteOutput);
	        scaledImageStream = byteOutput.toByteArray();
            url = ImageHelper.uploadFile(scaledImageStream, mimetype, userHandle);
        }
        if (contentCategory.equals(getWeb())){   
    		BufferedImage bufferedImage;
        	try
        	{
        		bufferedImage= ImageIO.read( new ByteArrayInputStream(imageStream));
        	}
        	catch(Exception e){
        	    bufferedImage = cmykRasterToSRGB(imageStream, format);
        	}
    		if(bufferedImage.getWidth() < Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")))
				scaledImageStream = imageStream;
			else{
				if(format.equalsIgnoreCase("gif")){
    				GifDecoder gifDecoder = checkAnimation(imageStream);
            		if(gifDecoder.getFrameCount()>1)
        				scaledImageStream = scaleAnimation(imageStream, gifDecoder, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")));
            		else{
            			bufferedImage = scaleImage(bufferedImage, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")));
        	            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        	            // use imageIO.write to encode the image back into a byte[]
        	            ImageIO.write(bufferedImage, format, byteOutput);
        	            scaledImageStream = byteOutput.toByteArray();
                	} 
    			}else{
       			bufferedImage = scaleImage(bufferedImage, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")));
	            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
	            // use imageIO.write to encode the image back into a byte[]
	            ImageIO.write(bufferedImage, format, byteOutput);
	            scaledImageStream = byteOutput.toByteArray();
	            } 
			}
        	url = ImageHelper.uploadFile(scaledImageStream, mimetype, userHandle);
        }
        component.getProperties().setContentCategory(contentCategory);
        if(contentCategory.equals(getOrig())){
            url = ImageHelper.uploadFile(imageStream, mimetype, userHandle);
        }
        component.getContent().setHref(url.toExternalForm());
        Enum enu = Enum.forString("internal-managed");
        component.getContent().setStorage(enu);
        return item;
    } 
    
    public static BufferedImage scaleImage(BufferedImage image, int size) throws Exception{
    	int width = image.getWidth();
    	int height = image.getHeight();
    	Image rescaledImage;
    	if(width > height)
    		rescaledImage = image.getScaledInstance(size, -1, Image.SCALE_SMOOTH);
    	else
    		rescaledImage = image.getScaledInstance(-1, size, Image.SCALE_SMOOTH);
        BufferedImage rescaledBufferedImage = new BufferedImage(rescaledImage.getWidth(null), rescaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = rescaledBufferedImage.getGraphics();
        g.drawImage(rescaledImage, 0, 0, null);
        return rescaledBufferedImage;
    }
    
    public static GifDecoder checkAnimation(byte[] image) throws Exception{
        GifDecoder gifDecoder = new GifDecoder(); 
        gifDecoder.read(new ByteArrayInputStream(image));
        return gifDecoder;
    }        
        
    public static byte[] scaleAnimation(byte[] image, GifDecoder gifDecoder, int width) throws Exception{
    	ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
    	outputStream.write("".getBytes());
    	AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        int frameCount = gifDecoder.getFrameCount();
        int loopCount = gifDecoder.getLoopCount();
        animatedGifEncoder.setRepeat(loopCount);
        animatedGifEncoder.start(outputStream);
        for (int frameNumber = 0; frameNumber < frameCount; frameNumber++) {

           BufferedImage frame = gifDecoder.getFrame(frameNumber);  // frame i
           int delay = gifDecoder.getDelay(frameNumber);  // display duration of frame in milliseconds
           animatedGifEncoder.setDelay(delay);   // frame delay per sec
           BufferedImage scaleImage = scaleImage(frame, width);
           animatedGifEncoder.addFrame( scaleImage );
        }    
        animatedGifEncoder.finish();
		return outputStream.toByteArray();
       }

    /**
     * Uploads a file to the staging servlet and returns the corresponding URL.
     * 
     * @param InputStream to upload
     * @param mimetype The mimetype of the file
     * @param userHandle The userhandle to use for upload
     * @return The URL of the uploaded file.
     * @throws Exception If anything goes wrong...
     */
    public static URL uploadFile(byte[] image, String mimetype, String userHandle) throws Exception{
        // Prepare the HttpMethod.
        String fwUrl = ServiceLocator.getFrameworkUrl();
        PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
        method.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(image)));
        method.setRequestHeader("Content-Type", mimetype);
        method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
        // Execute the method with HttpClient.
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        InitialContext context = new InitialContext();
        XmlTransforming ctransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
        return ctransforming.transformUploadResponseToFileURL(response);
    }

    public static String getThumbnailUrl(ItemVO item) throws Exception{
        return getContentUrl(item, getThumb());
    }

    public static String getWebResolutionUrl(ItemVO item) throws Exception{
        return getContentUrl(item, getWeb());
    }

    public static String getOriginalResolution(ItemVO item) throws Exception{
        return getContentUrl(item, getOrig());
    }

    public static String getContentUrl(ItemVO item, String contentCategory) throws Exception{
        for (Component c : item.getItemDocument().getItem().getComponents().getComponentArray())
        {
            if (c.getProperties().getContentCategory().equals(contentCategory))
            {
                return ServiceLocator.getFrameworkUrl() + "/ir/item/" + item.getItemDocument().getItem().getObjid()
                        + "/components/component/" + c.getObjid() + "/content";
            }
        }
        return null;
    }

    public static String getThumb() throws IOException, URISyntaxException{
        return PropertyReader.getProperty("xsd.metadata.content-category.thumbnail");
    }

    public static String getWeb() throws IOException, URISyntaxException{
        return PropertyReader.getProperty("xsd.metadata.content-category.web-resolution");
    }

    public static String getOrig() throws IOException, URISyntaxException{
        return PropertyReader.getProperty("xsd.metadata.content-category.original-resolution");
    }
    
    public static BufferedImage cmykRasterToSRGB(byte[] inputStream, String format)throws Exception{
  	  //Find a suitable ImageReader
        Iterator readers = ImageIO.getImageReadersByFormatName(format);
        ImageReader reader = null;
        while(readers.hasNext()) {
            reader = (ImageReader)readers.next();
            if(reader.canReadRaster()) {
                break;
            }
        }
        //Stream the image file (the original CMYK image)
        ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(inputStream));
        reader.setInput(input); 
        // Create the image.
        BufferedImage image;
        Raster raster = reader.readRaster(0, null); 
	        // Arbitrarily select a BufferedImage type.
            int imageType;
            switch(raster.getNumBands()) {
            case 1:
                imageType = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                imageType = BufferedImage.TYPE_3BYTE_BGR;
                break;
            case 4:
                imageType = BufferedImage.TYPE_4BYTE_ABGR;
                break;
            default:
                throw new UnsupportedOperationException();
            }

            // Create a BufferedImage.
          image = new BufferedImage(raster.getWidth(),raster.getHeight(),imageType);

           // Set the image data.
          image.getRaster().setRect(raster);
          return image;

    }
    
    /**
     * If 'filename' is a CMYK file, then convert the image into RGB,
     * store it into a JPEG file, and return the new filename.
     *
     * @param filename
     */
    private static String cmyk2rgb(String filename) throws IOException
    {
        // Change this format into any ImageIO supported format.
        String format = "jpg";
        File imageFile = new File(filename);
        String rgbFilename = filename;
        BufferedImage image = ImageIO.read(imageFile);
        if (image != null)
        {
            int colorSpaceType = image.getColorModel().getColorSpace().getType();
            if (colorSpaceType == ColorSpace.TYPE_CMYK)
            {
                BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                ColorConvertOp op = new ColorConvertOp(null);
                op.filter(image, rgbImage);

                rgbFilename = changeExtension(imageFile.getName(), format);
                rgbFilename = new File(imageFile.getParent(), format + "_" + rgbFilename).getPath();
                ImageIO.write(rgbImage, format, new File(rgbFilename));
            }
        }
        return rgbFilename;
    }

    /**
     * Change the extension of 'filename' to 'newExtension'.
     *
     * @param filename
     * @param newExtension
     * @return filename with new extension
     */
    private static String changeExtension(String filename, String newExtension)
    {
        String result = filename;
        if (filename != null && newExtension != null && newExtension.length() != 0);
        {
            int dot = filename.lastIndexOf('.');
            if (dot != -1)
            {
                result = filename.substring(0, dot) + '.' + newExtension;
            }
        }
        return result;
    }
}
