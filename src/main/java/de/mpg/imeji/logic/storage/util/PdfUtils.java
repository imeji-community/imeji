package de.mpg.imeji.logic.storage.util;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import de.mpg.imeji.logic.storage.Storage.FileResolution;
import de.mpg.imeji.presentation.util.PropertyReader;

public class PdfUtils {
	
	final static String IMAGE_FILE_EXTENSION = "jpg";
	final static int PAGENUMBERTOIMAGE = 0;
	final static int DPI_WEB = 92;
	final static int DPI_THUMB = 72;
	
	final static int RESOLUTION_DPI_SCREEN = 72;
	final static int RESOLUTION_DPI_IMAGE = 150;
	
	private static int getResolutionDPI() {
		try {
			int resolution = Integer.parseInt(PropertyReader.getProperty("imeji.internal.pdf.resolution.dpi"));
			if(resolution > 0)
				return resolution;
		} catch (Exception e) {
			return PdfUtils.RESOLUTION_DPI_IMAGE;
		}
		
		return PdfUtils.RESOLUTION_DPI_IMAGE; 
	}
	
	/**
	 * Gets the image byte array from byte array file
	 * @param bytes
	 * @return byte array from byte array
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static byte[] pdfsToImageBytes(byte[] bytes) throws FileNotFoundException, IOException {
//		ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
//		byteBuffer.put(bytes);		
//		PDFFile pdfFile = new PDFFile(byteBuffer);	
//		return PdfUtils.pdfFileToByteAray(pdfFile, PdfUtils.PAGENUMBERTOIMAGE, BufferedImage.TYPE_INT_RGB, PdfUtils.getResolutionDPI());
		return PdfUtils.pdfFileToByteAray(new PDFFile(ByteBuffer.wrap(bytes)), PdfUtils.PAGENUMBERTOIMAGE, BufferedImage.TYPE_INT_RGB, PdfUtils.getResolutionDPI());
	}
	
	/**
	 * Gets the image byte array from byte array file
	 * @param bytes
	 * @param resolution
	 * @return byte array from PDF page
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static byte[] pdfsToImageBytes(byte[] bytes, FileResolution resolution) throws FileNotFoundException, IOException {
		if(resolution == FileResolution.WEB) {
			return PdfUtils.pdfFileToByteAray(new PDFFile(ByteBuffer.wrap(bytes)), PdfUtils.PAGENUMBERTOIMAGE, BufferedImage.TYPE_INT_RGB, PdfUtils.DPI_WEB);
		} else if (resolution == FileResolution.THUMBNAIL) {			
			return PdfUtils.pdfFileToByteAray(new PDFFile(ByteBuffer.wrap(bytes)), PdfUtils.PAGENUMBERTOIMAGE, BufferedImage.TYPE_INT_RGB, PdfUtils.DPI_THUMB);
		}
		return PdfUtils.pdfFileToByteAray(new PDFFile(ByteBuffer.wrap(bytes)), PdfUtils.PAGENUMBERTOIMAGE, BufferedImage.TYPE_INT_RGB, PdfUtils.getResolutionDPI());
	}
	
	
	/**
	 * Gets the image byte array from a page of a PDF file
	 * @param pdfFile
	 * @param pageNumber
	 * @param imageType
	 * @param resolution
	 * @return byte array from PDF page
	 * @throws IOException
	 */
	public static byte[] pdfFileToByteAray(PDFFile pdfFile, int pageNumber, int imageType, int resolution) throws IOException {	
		if (pageNumber < 0 || pageNumber >= pdfFile.getNumPages()) // hn: randomize a page number if provided page number is not proper 
			pageNumber = new Random().nextInt(pdfFile.getNumPages());
		System.out.println(pdfFile.getNumPages());		
		return PdfUtils.pdfPageToByteAray(pdfFile.getPage(pageNumber,true),imageType,resolution);
	}
	
	/**
	 * Gets the image byte array of a PDF page
	 * @param page
	 * @param imageType
	 * @param resolution
	 * @return byte array from PDF page
	 * @throws IOException
	 */
	public static byte[] pdfPageToByteAray(PDFPage page, int imageType, int resolution) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(PdfUtils.convertToImage(page, imageType, resolution), PdfUtils.IMAGE_FILE_EXTENSION, baos);
		baos.flush();
		byte[] bytes = baos.toByteArray();
		baos.close();
		return bytes;
	}
	
	/**
	 * Convert a PDF page to an image
	 * @param page
	 * @param imageType
	 * @param resolution
	 * @return BufferedImage from PDF page 
	 * @throws IOException
	 */
	private static BufferedImage convertToImage(PDFPage page, int imageType, int resolution)
	{
		//get the width and height for the doc at the default zoom				
		int width = (int)page.getWidth();
		int height = (int)page.getHeight();	
		float scaling = resolution / (float) PdfUtils.RESOLUTION_DPI_SCREEN;
		
		int widthPx = Math.round(width * scaling);
		int heightPx = Math.round(height * scaling);
		
		int rotationAngle = page.getRotation();
		
        // normalize the rotation angle
        if (rotationAngle < 0)
        {
            rotationAngle += 360;
        }
        else if (rotationAngle >= 360)
        {
            rotationAngle -= 360;
        }
        
        Rectangle rect = new Rectangle(0, 0, (int)width, (int)height);
        BufferedImage retval = null;
        
        // swap width and height
        if (rotationAngle == 90 || rotationAngle == 270)
        {		        	
            retval = (BufferedImage)page.getImage( heightPx, widthPx, //width & height
            		new Rectangle(0,0,rect.height,rect.width), // clip rect
					null, // null for the ImageObserver
					true, // fill background with white
					true  // block until drawing is done
			);
        }
        else
        {
        	retval = (BufferedImage)page.getImage( widthPx, heightPx, //width & height
					rect, // clip rect
					null, // null for the ImageObserver
					true, // fill background with white
					true  // block until drawing is done
			);
        }

        Graphics2D graphics = (Graphics2D)retval.getGraphics();        
        
        if (rotationAngle != 0)
        {
            int translateX = 0;
            int translateY = 0;
            switch(rotationAngle) 
            {
                case 90:
                    translateX = retval.getWidth();
                    break;
                case 270:
                    translateY = retval.getHeight();
                    break;
                case 180:
                    translateX = retval.getWidth();
                    translateY = retval.getHeight();
                    break;
                default:
                    break;
            }
            graphics.translate(translateX,translateY);
            graphics.rotate((float)Math.toRadians(rotationAngle));
        }
        graphics.scale( scaling, scaling );
        
		return retval;
	}
}
