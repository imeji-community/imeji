package de.mpg.imeji.logic.storage.util;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFParseException;

import de.mpg.imeji.logic.storage.Storage.FileResolution;
import de.mpg.imeji.presentation.util.PropertyReader;

public class PdfUtils
{
    final static String IMAGE_FILE_EXTENSION = "jpg";
    final static int PAGENUMBERTOIMAGE = 0;
    final static int DPI_WEB = 92;
    final static int DPI_THUMB = 72;
    final static int RESOLUTION_DPI_SCREEN = 72;
    final static int RESOLUTION_DPI_IMAGE = 150;

    /**
     * @return the pdf rendering DPI
     */
    private static int getResolutionDPI()
    {
        try
        {
            int resolution = Integer.parseInt(PropertyReader.getProperty("imeji.internal.pdf.resolution.dpi"));
            if (resolution > 0)
                return resolution;
        }
        catch (Exception e)
        {
            return PdfUtils.RESOLUTION_DPI_IMAGE;
        }
        return PdfUtils.RESOLUTION_DPI_IMAGE;
    }

    /**
     * @return the page number which will be transformed for the thumbnail image.
     */
    private static int getThumbnailPage()
    {
        try
        {
            int page = Integer.parseInt(PropertyReader.getProperty("imeji.internal.pdf.thumbnail.page"));
            if (page >= 0)
                return page;
        }
        catch (Exception e)
        {
            return PdfUtils.PAGENUMBERTOIMAGE;
        }
        return PdfUtils.PAGENUMBERTOIMAGE;
    }

    /**
     * Gets the image byte array from byte array file
     * 
     * @param bytes
     * @return byte array from byte array
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static byte[] pdfsToImageBytes(File file) throws FileNotFoundException, IOException, PDFParseException
    {
        return PdfUtils.pdfFileToByteAray(new PDFFile(ByteBuffer.wrap(FileUtils.readFileToByteArray(file))),
                PdfUtils.getThumbnailPage(), BufferedImage.TYPE_INT_RGB, PdfUtils.getResolutionDPI());
    }

    /**
     * Gets the image byte array from byte array file
     * 
     * @param bytes
     * @param resolution
     * @return byte array from PDF page
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static byte[] pdfsToImageBytes(byte[] bytes, FileResolution resolution) throws FileNotFoundException,
            IOException
    {
        if (resolution == FileResolution.WEB)
        {
            return PdfUtils.pdfFileToByteAray(new PDFFile(ByteBuffer.wrap(bytes)), PdfUtils.getThumbnailPage(),
                    BufferedImage.TYPE_INT_RGB, PdfUtils.DPI_WEB);
        }
        else if (resolution == FileResolution.THUMBNAIL)
        {
            return PdfUtils.pdfFileToByteAray(new PDFFile(ByteBuffer.wrap(bytes)), PdfUtils.getThumbnailPage(),
                    BufferedImage.TYPE_INT_RGB, PdfUtils.DPI_THUMB);
        }
        return PdfUtils.pdfFileToByteAray(new PDFFile(ByteBuffer.wrap(bytes)), PdfUtils.getThumbnailPage(),
                BufferedImage.TYPE_INT_RGB, PdfUtils.getResolutionDPI());
    }

    /**
     * Gets the image byte array from a page of a PDF file
     * 
     * @param pdfFile
     * @param pageNumber
     * @param imageType
     * @param resolution
     * @return byte array from PDF page
     * @throws IOException
     */
    public static byte[] pdfFileToByteAray(PDFFile pdfFile, int pageNumber, int imageType, int resolution)
            throws IOException
    {
        // if (pageNumber < 0 || pageNumber > pdfFile.getNumPages()) // hn: randomize a page number if provided page
        // pageNumber = new Random().nextInt(pdfFile.getNumPages()); // number is not proper
        byte[] bytes = null;
        try
        {
            bytes = PdfUtils.pdfPageToByteAray(pdfFile.getPage(pageNumber, true), imageType, resolution);
        }
        catch (Exception e)
        {
            if (pageNumber < pdfFile.getNumPages())
            {
                bytes = pdfFileToByteAray(pdfFile, ++pageNumber, imageType, resolution);
            }
        }
        return bytes;
    }

    /**
     * Gets the image byte array of a PDF page
     * 
     * @param page
     * @param imageType
     * @param resolution
     * @return byte array from PDF page
     * @throws Exception
     */
    public static byte[] pdfPageToByteAray(PDFPage page, int imageType, int resolution) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ImageIO.write(PdfUtils.convertToImage(page, imageType, resolution), PdfUtils.IMAGE_FILE_EXTENSION, baos);
            baos.flush();
            return baos.toByteArray();
        }
        finally
        {
            baos.close();
        }
    }

    /**
     * Convert a PDF page to an image
     * 
     * @param page
     * @param imageType
     * @param resolution
     * @return BufferedImage from PDF page
     * @throws IOException
     */
    private static BufferedImage convertToImage(PDFPage page, int imageType, int resolution) throws Exception
    {
        // get the width and height for the doc at the default zoom
        int width = (int)page.getWidth();
        int height = (int)page.getHeight();
        float scaling = resolution / (float)PdfUtils.RESOLUTION_DPI_SCREEN;
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
        Rectangle rect = new Rectangle(0, 0, width, height);
        BufferedImage retval = null;
        // swap width and height
        if (rotationAngle == 90 || rotationAngle == 270)
        {
            retval = (BufferedImage)page.getImage(heightPx, widthPx, // width & height
                    new Rectangle(0, 0, rect.height, rect.width), // clip rect
                    null, // null for the ImageObserver
                    true, // fill background with white
                    true // block until drawing is done
                    );
        }
        else
        {
            retval = (BufferedImage)page.getImage(widthPx, heightPx, // width & height
                    rect, // clip rect
                    null, // null for the ImageObserver
                    true, // fill background with white
                    true // block until drawing is done
                    );
        }
        Graphics2D graphics = (Graphics2D)retval.getGraphics();
        if (rotationAngle != 0)
        {
            int translateX = 0;
            int translateY = 0;
            switch (rotationAngle)
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
            graphics.translate(translateX, translateY);
            graphics.rotate((float)Math.toRadians(rotationAngle));
        }
        graphics.scale(scaling, scaling);
        return retval;
    }
}
