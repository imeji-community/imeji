package de.mpg.imeji.upload.helper;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
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
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.escidoc.ItemVO;
import de.mpg.imeji.util.BeanHelper;


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
		        if(bufferedImage.getWidth() > Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail"))|| bufferedImage.getHeight() > Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail"))){
		        	bufferedImage = scaleImage(bufferedImage, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail")), getThumb());
		        }
		        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		        // use imageIO.write to encode the image back into a byte[]
		        ImageIO.write(bufferedImage, format, byteOutput);
		        scaledImageStream = byteOutput.toByteArray();
	            url = ImageHelper.uploadFile(scaledImageStream, mimetype, userHandle);
        	}
        	catch(Exception e){
        		System.err.println("Error transforming image: " + e.getMessage());
        		Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        		String test = navigation.getApplicationUrl() + "resources/icon/defaultThumb.gif";
        		URL noThumbUrl = new URL(test);
        		int contentLength = noThumbUrl.openConnection().getContentLength();
        		InputStream openStream =noThumbUrl.openStream();
        		byte[] data = new byte[contentLength];
        		openStream.read(data);
        		openStream.close();

        		url = ImageHelper.uploadFile(data, mimetype, userHandle);
        	}
        }
        if (contentCategory.equals(getWeb())){   
    		BufferedImage bufferedImage;
        	try
        	{
        		bufferedImage= ImageIO.read( new ByteArrayInputStream(imageStream));

	    		if(bufferedImage.getWidth() < Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web"))&& bufferedImage.getHeight() < Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")))
					scaledImageStream = imageStream;
				else{
					if(format.equalsIgnoreCase("gif")){
	    				GifDecoder gifDecoder = checkAnimation(imageStream);
	            		if(gifDecoder.getFrameCount()>1)
	        				scaledImageStream = scaleAnimation(imageStream, gifDecoder, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")));
	            		else{
	            			bufferedImage = scaleImage(bufferedImage, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")),getWeb());
	        	            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
	        	            // use imageIO.write to encode the image back into a byte[]
	        	            ImageIO.write(bufferedImage, format, byteOutput);
	        	            scaledImageStream = byteOutput.toByteArray();
	                	} 
	    			}else{
	       			bufferedImage = scaleImage(bufferedImage, Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web")), getWeb());
		            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		            // use imageIO.write to encode the image back into a byte[]
		            ImageIO.write(bufferedImage, format, byteOutput);
		            scaledImageStream = byteOutput.toByteArray();
		            } 
				}
	    		url = ImageHelper.uploadFile(scaledImageStream, mimetype, userHandle);
        	}
        	catch(Exception e){
        	    url = ImageHelper.uploadFile(imageStream, mimetype, userHandle);
        	}
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
    
//    public static BufferedImage scaleImage(BufferedImage image, int size) throws Exception{
//    	int width = image.getWidth();
//    	int height = image.getHeight();
//    	Image rescaledImage;
//    	if(width > height)
//    		rescaledImage = image.getScaledInstance(size, -1, Image.SCALE_SMOOTH);
//    	else
//    		rescaledImage = image.getScaledInstance(-1, size, Image.SCALE_SMOOTH);
//        BufferedImage rescaledBufferedImage = new BufferedImage(rescaledImage.getWidth(null), rescaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
//        Graphics g = rescaledBufferedImage.getGraphics();
//        g.drawImage(rescaledImage, 0, 0, null);
//        return rescaledBufferedImage;
//    }
    
    public static BufferedImage scaleImage(BufferedImage image, int size, String resolution) throws Exception{
    	int width = image.getWidth(null);
    	int height = image.getHeight(null);
    	BufferedImage newImg = null;
    	Image rescaledImage;
    	if(width > height)
    	{
    		if(resolution.equals(getThumb()))
    		{
	    		newImg= new BufferedImage(height, height,BufferedImage.TYPE_INT_RGB);
	        	Graphics g1 = newImg.createGraphics();
	        	g1.drawImage(image, (height-width)/2, 0, null);
	        	if(height>size)
	        		rescaledImage = newImg.getScaledInstance(size, -1, Image.SCALE_SMOOTH);
	        	else
	        		rescaledImage = newImg;
    		}
    		else
    			rescaledImage = image.getScaledInstance(size, -1, Image.SCALE_SMOOTH);
    	}
    	else  
    	{
    		if(resolution.equals(getThumb()))
    		{
	    		newImg= new BufferedImage(width, width,BufferedImage.TYPE_INT_RGB);
	        	Graphics g1 = newImg.createGraphics();
	        	g1.drawImage(image, 0, (width-height)/2, null);
	        	if(width>size)
	        		rescaledImage = newImg.getScaledInstance(-1, size, Image.SCALE_SMOOTH);
	        	else
	        		rescaledImage = newImg;
    		}
    		else
            	rescaledImage = image.getScaledInstance(-1, size, Image.SCALE_SMOOTH);

    	}

        BufferedImage rescaledBufferedImage = new BufferedImage(rescaledImage.getWidth(null), rescaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g2 = rescaledBufferedImage.getGraphics();
        g2.drawImage(rescaledImage, 0, 0, null);
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
           BufferedImage scaleImage = scaleImage(frame, width, getWeb());
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
        //TODO remove jbix transformation by something else
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
    

    /**
     * for reading CMYK images
     * Creates new RGB images from all the CMYK images passed
     * in on the command line.
     * 
     */
//    public static BufferedImage cmykRasterToSRGB(byte[] inputStream, String format)throws Exception{
//  	  //Find a suitable ImageReader
//        Iterator readers = ImageIO.getImageReadersByFormatName(format);
//        ImageReader reader = null;
//        while(readers.hasNext()) {
//            reader = (ImageReader)readers.next();
//            if(reader.canReadRaster()) {
//                break;
//            }
//        }
//        //Stream the image file (the original CMYK image)
//        ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(inputStream));
//        reader.setInput(input); 
//        // Create the image.
//        BufferedImage image;
//        Raster raster = reader.readRaster(0, null); 
//	    // Arbitrarily select a BufferedImage type.
//        int imageType;
//        switch(raster.getNumBands()) 
//        {
//        case 1:
//        	imageType = BufferedImage.TYPE_BYTE_GRAY;
//            break;
//            case 3:
//            	imageType = BufferedImage.TYPE_3BYTE_BGR;
//                break;
//                case 4:
//                	imageType = BufferedImage.TYPE_4BYTE_ABGR;
//                	break;
//                	default:
//                		throw new UnsupportedOperationException();
//            }
//        // Create a BufferedImage.
//        image = new BufferedImage(raster.getWidth(),raster.getHeight(),imageType);
//        // Set the image data.
//        image.getRaster().setRect(raster);
//    	return image;
//    }
//    
//    public static BufferedImage readCMYKwithJAI(byte[] inputStream, String format)throws Exception
//    {
//    	ByteArrayInputStream bais = new ByteArrayInputStream(inputStream);
//    	SeekableStream seekableStream = SeekableStream.wrapInputStream(bais,false);
//    	PlanarImage src = JAI.create("Stream", seekableStream);
//    	BufferedImage image = src.getAsBufferedImage();    	
//    	return image;
//    }
//    
//    public static BufferedImage readCMYKwithjm4java(byte[] inputStream, String format)throws Exception
//    {
//    	ByteArrayInputStream bais = new ByteArrayInputStream(inputStream);
//    	Stream2BufferedImage stream4Image= new Stream2BufferedImage();
//    	stream4Image.consumeOutput(bais);
//    	BufferedImage image = stream4Image.getImage();
//    	return image;
//    }



}
