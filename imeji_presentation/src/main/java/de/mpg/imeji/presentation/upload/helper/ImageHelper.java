/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.upload.helper;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.ajax4jsf.resource.image.animatedgif.GifDecoder;

import de.mpg.imeji.presentation.util.PropertyReader;

public class ImageHelper
{
	public static BufferedImage scaleImage(BufferedImage image, int size, String resolution) throws Exception
	{
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

	public static GifDecoder checkAnimation(byte[] image) throws Exception
	{
		GifDecoder gifDecoder = new GifDecoder(); 
		gifDecoder.read(new ByteArrayInputStream(image));
		return gifDecoder;
	}        

	public static byte[] scaleAnimation(byte[] image, GifDecoder gifDecoder, int width) throws Exception
	{
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


	public static String getThumb() throws IOException, URISyntaxException
	{
		return PropertyReader.getProperty("xsd.metadata.content-category.thumbnail");
	}

	public static String getWeb() throws IOException, URISyntaxException
	{
		return PropertyReader.getProperty("xsd.metadata.content-category.web-resolution");
	}

	public static String getOrig() throws IOException, URISyntaxException
	{
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
