package de.mpg.imeji.logic.storage.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFImageWriter;

import de.mpg.imeji.logic.storage.Storage.FileResolution;

public class PdfUtils {
	
	final static String IMAGETYPE = "jpg";
	final static int PAGENUMBERTOIMAGE = 0;
	final static int DPI_WEB = 92;
	final static int DPI_THUMB = 72;
	final static int DPI_ORIGINAL = 300;

	/**
	 * Gets byte array of an uploaded file as well in a provided byte array.
	 * @param bytes
	 * @return byte array of a PDF page within the PDF document as byte array.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static byte[] pdfsToImageBytes(byte[] bytes) throws FileNotFoundException, IOException {
		PDDocument pdfDoc = PDDocument.loadNonSeq(new ByteArrayInputStream(bytes), null);
		byte[] newBytes = PdfUtils.pdfFileToByteAray(pdfDoc, PdfUtils.PAGENUMBERTOIMAGE, BufferedImage.TYPE_INT_RGB, PdfUtils.DPI_ORIGINAL);
		pdfDoc.close();
		return newBytes;
	}
	
	/**
	 * Gets byte array of an uploaded file as well in a provided byte array. 
	 * @param bytes
	 * @param resolution
	 * @return byte array of a PDF page within the PDF document as byte array.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static byte[] pdfsToImageBytes(byte[] bytes, FileResolution resolution) throws FileNotFoundException, IOException {
		PDDocument pdfDoc = PDDocument.loadNonSeq(new ByteArrayInputStream(bytes), null);
		
		if(resolution == FileResolution.WEB) {
			byte[] newBytes = PdfUtils.pdfFileToByteAray(pdfDoc, PdfUtils.PAGENUMBERTOIMAGE, BufferedImage.TYPE_INT_RGB, PdfUtils.DPI_WEB);
			pdfDoc.close();
			return newBytes;
		} else if (resolution == FileResolution.THUMBNAIL) {
			byte[] newBytes = PdfUtils.pdfFileToByteAray(pdfDoc, PdfUtils.PAGENUMBERTOIMAGE, BufferedImage.TYPE_INT_RGB, PdfUtils.DPI_THUMB);
			pdfDoc.close();
			return newBytes;
		}
		
		byte[] newBytes = PdfUtils.pdfFileToByteAray(pdfDoc, PdfUtils.PAGENUMBERTOIMAGE, BufferedImage.TYPE_INT_RGB, PdfUtils.DPI_ORIGINAL);
		pdfDoc.close();
		return newBytes;
	}
	
	/**
	 * Gets the image filename.
	 * @param document
	 * @param pageNumber
	 * @param imageType
	 * @param resolution
	 * @return
	 * @throws IOException
	 */
	public static String pdfsToImageAsString(PDDocument document, int pageNumber, int imageType, int resolution) throws IOException {
		
		if (pageNumber <= 0 && pageNumber > document.getNumberOfPages()) // hn: randomize a page number if provided page number is not proper 
			pageNumber = new Random().nextInt(document.getNumberOfPages()) + 1; // + 1 since, the pdfbox function access on the page started at 1 and not 0
		
		String folder = System.getProperty("java.io.tmpdir");

		PDFImageWriter imageWriter = new PDFImageWriter();
		imageWriter.writeImage(document, "png", null, pageNumber, pageNumber,
				folder + "/pdf2image_", BufferedImage.TYPE_INT_RGB,	resolution);
		document.close();
		return folder + "/pdf2image_" + pageNumber;
	}
	
	/**
	 * Convert a provided page within the PDF file into a buffered image.
	 * @param document
	 * @param pageNumber
	 * @return buffered image
	 * @throws IOException
	 */
	public static BufferedImage pdfFileToSingleBuffered(PDDocument document, int pageNumber) throws IOException {
		
		if (pageNumber < 0 || pageNumber >= document.getNumberOfPages()) // hn: randomize a page number if provided page number is not proper 
			pageNumber = new Random().nextInt(document.getNumberOfPages());
		
		return ((PDPage) document.getDocumentCatalog().getAllPages().get(pageNumber)).convertToImage();
		
	}
	
	/**
	 * Create a provided PDF page in a PDF file into BufferedImage.
	 * @param document
	 * @param pageNumber, if provided page number is invalid, a valid random page will be generated.
	 * @param imageType
	 * @param resolution
	 * @return a buffered image
	 * @throws IOException
	 */
	public static BufferedImage pdfFileToSingleBufferedImage(PDDocument document, int pageNumber, int imageType, int resolution) throws IOException {
		
		if (pageNumber < 0 || pageNumber >= document.getNumberOfPages()) // hn: randomize a page number if provided page number is not proper 
			pageNumber = new Random().nextInt(document.getNumberOfPages());
		
		return ((PDPage) document.getDocumentCatalog().getAllPages().get(pageNumber)).convertToImage(imageType, resolution);
	}
	
	/**
	 * Convert a PDF file to byte array.
	 * @param document
	 * @param pageNumber
	 * @param imageType
	 * @param resolution
	 * @return byte array of a PDF page within the PDF file
	 * @throws IOException
	 */
	public static byte[] pdfFileToByteAray(PDDocument document, int pageNumber, int imageType, int resolution) throws IOException {
		if (pageNumber < 0 || pageNumber >= document.getNumberOfPages()) // hn: randomize a page number if provided page number is not proper 
			pageNumber = new Random().nextInt(document.getNumberOfPages());		
		return PdfUtils.pdfPageToByteAray((PDPage) document.getDocumentCatalog().getAllPages().get(pageNumber),imageType,resolution);
	}
	
	/**
	 * Convert a PDF page to byte array.
	 * @param page
	 * @param imageType
	 * @param resolution
	 * @return byte array from a PDF page.
	 * @throws IOException
	 */
	public static byte[] pdfPageToByteAray(PDPage page, int imageType, int resolution) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(page.convertToImage(), PdfUtils.IMAGETYPE, baos);
		baos.flush();
		byte[] bytes = baos.toByteArray();
		baos.close();
		return bytes;
	}
}
