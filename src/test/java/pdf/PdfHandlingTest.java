package pdf;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.PDFImageWriter;
import org.junit.Test;

public class PdfHandlingTest {

	// http://pdfbox.apache.org/cookbook/documentcreation.html

	// @Test
	public void createBlankPdfTest() throws IOException, COSVisitorException {
		// Create a new empty document
		PDDocument document = new PDDocument();

		// Create a new blank page and add it to the document
		PDPage blankPage = new PDPage();
		document.addPage(blankPage);

		// Save the newly created document
		document.save("src/test/resources/pdf/BlankPage.pdf");

		// finally make sure that the document is properly
		// closed.
		document.close();
	}

	// @Test
	public void createHelloWorldPdfTest() throws IOException,
			COSVisitorException {
		// Create a document and add a page to it
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);

		// Create a new font object selecting one of the PDF base fonts
		PDFont font = PDType1Font.HELVETICA_BOLD;

		// Start a new content stream which will "hold" the to be created
		// content
		PDPageContentStream contentStream = new PDPageContentStream(document,
				page);

		// Define a text content stream using the selected font, moving the
		// cursor and drawing the text "Hello World"
		contentStream.beginText();
		contentStream.setFont(font, 12);
		contentStream.moveTextPositionByAmount(100, 700);
		contentStream.drawString("Hello World");
		contentStream.endText();

		// Make sure that the content stream is closed:
		contentStream.close();

		// Save the results and ensure that the document is properly closed:
		document.save("src/test/resources/pdf/Hello World.pdf");
		document.close();
	}

	// @Test
	public void createMultiPagePdfFIleTest() throws IOException,
			COSVisitorException {
		// Create a document and add a page to it
		PDDocument document = new PDDocument();

		int p = new Random().nextInt(10);

		for (int i = 0; i < p; i++) {
			PDPage page = new PDPage();
			document.addPage(page);

			// Create a new font object selecting one of the PDF base fonts
			PDFont font = PDType1Font.HELVETICA_BOLD;

			// Start a new content stream which will "hold" the to be created
			// content
			PDPageContentStream contentStream = new PDPageContentStream(
					document, page);

			// Define a text content stream using the selected font, moving the
			// cursor and drawing the text "Hello World"
			contentStream.beginText();
			contentStream.setFont(font, 12);
			contentStream.moveTextPositionByAmount(100, 700);
			contentStream.drawString("Hello World in page: " + (i + 1));
			contentStream.endText();

			// Make sure that the content stream is closed:
			contentStream.close();

		}
		// Save the results and ensure that the document is properly closed:
		document.save("src/test/resources/pdf/pdfWith" + p + "Pages.pdf");
		document.close();
	}

	@Test
	public void createImageFromPdfTest() throws IOException,
			COSVisitorException {
		// Lead a pdf document
		PDDocument document = PDDocument.load(new File(
				"src/test/resources/pdf/pdfWith4Pages.pdf"));

		int i = new Random().nextInt(document.getNumberOfPages()) + 1;
		System.out.println(i);
		PDFImageWriter imageWriter = new PDFImageWriter();
		imageWriter.writeImage(document, "png", null, i, i,
				"src/test/resources/pdf/image_", BufferedImage.TYPE_INT_RGB,
				300);

		// finally make sure that the document is properly
		// closed.
		document.close();
	}

}
