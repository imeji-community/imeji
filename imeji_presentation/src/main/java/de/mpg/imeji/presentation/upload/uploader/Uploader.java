package de.mpg.imeji.presentation.upload.uploader;

import java.net.URL;

import de.mpg.imeji.presentation.upload.UploadManager;
import de.mpg.imeji.presentation.upload.helper.ImageHelper;

/**
 * Interface for Uploader Object. Uploader are user to initialized the {@link UploadManager}, which then called the
 * upload method. The class implementing {@link Uploader}, needs to initialized all the necessary parameters for the
 * upload in their constructor
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public interface Uploader
{
    /**
     * Start the Upload into the specified service. Manages all necessary step for the file repository
     * 
     * @param stream - File as {@link Byte} array to be upload
     * @param contentCategory - One of the 3 imeji file content-category (orginal, web, thumbnail). Value are defined in
     *            imeji.properties, and are accessible via the {@link ImageHelper}
     * @return - the {@link URL} of the uploaded file
     */
    public URL upload(byte[] stream, String contentCategory);

    /**
     * Return the Mime-type of the file being uploaded
     * 
     * @return
     */
    public String getMimetype();
}
