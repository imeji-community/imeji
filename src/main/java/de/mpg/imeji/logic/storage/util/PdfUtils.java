package de.mpg.imeji.logic.storage.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.AccessException;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFImageWriter;

public class PdfUtils {

	public static FileOutputStream convertPdfPageToImage(FileInputStream fis, int pageNumber, int imageTyp, int resoulution) throws FileNotFoundException, IOException {			
		return new FileOutputStream(new File(PdfUtils.convertPdfPageToImage(PDDocument.load(fis),pageNumber,imageTyp,resoulution)));
	}
	
	public static String convertPdfPageToImage(PDDocument document, int pageNumber, int imageTyp, int resoulution) throws IOException {
		
		if (pageNumber <= 0 && pageNumber > document.getNumberOfPages())
			throw new AccessException("Page not existed!");
		
		String folder = System.getProperty("java.io.tmpdir");

		PDFImageWriter imageWriter = new PDFImageWriter();
		imageWriter.writeImage(document, "png", null, pageNumber, pageNumber,
				folder + "/pdf2image_", BufferedImage.TYPE_INT_RGB,	resoulution);
		document.close();
		return folder + "/pdf2image_" + pageNumber;
	}
}
