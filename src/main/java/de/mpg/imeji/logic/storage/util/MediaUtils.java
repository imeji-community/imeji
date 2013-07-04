/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.storage.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.core.InfoException;

import de.mpg.imeji.logic.storage.Storage.FileResolution;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * Mehtods to help wotk with images
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class MediaUtils
{
    private static Logger logger = Logger.getLogger(MediaUtils.class);
    /**
     * If true, the rescale will keep the better quality of the images
     */
    private static boolean RESCALE_HIGH_QUALITY = true;

    public static void main(String[] args) throws IOException, URISyntaxException
    {
        verifyImageMagickInstallation();
    }
    
    /*
     * TODO Ye: Execute when upload page shows and show install ImageMagick tips
     */
    public static boolean verifyImageMagickInstallation() throws IOException, URISyntaxException
    {
        // TODO set in properties
        String imPath = getImageMagickInstallationPath();
        ConvertCmd cmd = new ConvertCmd(false);
        cmd.setSearchPath(imPath);
        IMOperation op = new IMOperation();
        // get ImageMagick version
        op.version();
        try
        {
            cmd.run(op);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            // org.im4java.core.CommandException: java.io.FileNotFoundException:
            // convert
            // System.out.println("Not installed: " + e);
            return false;
        }
        return true;
    }

    public static boolean verifyMediaFormatSupport(String filename)
    {
        try
        {
            new Info(filename, true);
            // System.out.println("extension:"+extension.getImageFormat()+"|| "+extension.getImageHeight()+"||"+extension.getImageWidth());
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            System.out.println("Not support format: " + e + "||" + filename);
            return false;
        }
        return true;
    }

    public static Info getMediaInfo(String filename) throws InfoException
    {
        Info mediaInfo = new Info(filename, true);
        // System.out.println("extension:"+extension.getImageFormat()+"|| "+extension.getImageHeight()+"||"+extension.getImageWidth());
        return mediaInfo;
    }

    /*
     * if mimeType is kind of video types, then extract first frame
     */
    public static void resizeImage(String mimeType, String orginalPath, String targetPath, FileResolution resolution)
            throws IOException, URISyntaxException
    {
        File file = new File(orginalPath);
        // the dir is required to exist in advance
        file.getParentFile().mkdirs();
        file = new File(targetPath);
        file.getParentFile().mkdirs();
        String imPath = getImageMagickInstallationPath();
        // TODO Ye:ConvertCmd(true) to use GraphicsMagick, which is said faster
        ConvertCmd cmd = new ConvertCmd(false);
        cmd.setSearchPath(imPath);
        // create the operation, add images and operators/options
        IMOperation op = new IMOperation();
        if (mimeType.equals("MOV") || mimeType.equals("MP4") || mimeType.equals("GIF") || mimeType.equals("WMV"))
        {
            // extract the first frame
            orginalPath = orginalPath + "[0]";
        }
        op.addImage(orginalPath);
        int size = getResolution(resolution);
        Info imageObject;
        int width = 0;
        int height = 0;
		try {
			imageObject=getMediaInfo(orginalPath);
			width=imageObject.getImageWidth();
			height = imageObject.getImageHeight();
		} catch (InfoException e1) {
			e1.printStackTrace();
		}
		if(width==0||height==0){
			//TODO Ye: return link to default thumbnail
			return;
		}
			
        if (width > size) {
            height = size * height / width;
            width = size;
        }
        //Only Shrink Larger Images ('>' flag)
        op.resize(width, height,">");
        op.addImage(targetPath);
        try
        {// execute the operation
            cmd.run(op);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IM4JavaException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Return the maximum size of an image according to its {@link FileResolution}. The values are defined in the
     * properties
     * 
     * @param FileResolution
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private static int getResolution(FileResolution resolution) throws IOException, URISyntaxException
    {
        switch (resolution)
        {
            case THUMBNAIL:
                return Integer.parseInt(PropertyReader.getProperty("xsd.resolution.thumbnail"));
            case WEB:
                return Integer.parseInt(PropertyReader.getProperty("xsd.resolution.web"));
            default:
                return 0;
        }
    }

    private static String getImageMagickInstallationPath() throws IOException, URISyntaxException
    {
    	return PropertyReader.getProperty("imeji.imagemagick.installpath");
    }

    /**
     * Return the Mime Type of a file according to im4java helper class FIXME, imagemagic not support types: mkv,
     * ogg/ogv, webm
     * 
     * @param format
     * @return
     */
    public static String getMimeType(String filepath)
    {
        Info imageInfo = null;
        try
        {
            imageInfo = new Info(filepath, true);
            return imageInfo.getImageFormat();
        }
        catch (InfoException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Create a temporary file or directory
     * @return temparory file or diectory
     * @throws IOException
     */
    public static File createTempDirectory() throws IOException {
		return File.createTempFile("temp", Long.toString(System.nanoTime()));
	}
}