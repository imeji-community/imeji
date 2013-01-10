package de.mpg.imeji.presentation.upload.uploader;

import java.io.ByteArrayInputStream;
import java.net.URL;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.component.Components;
import de.mpg.imeji.presentation.escidoc.EscidocHelper;
import de.mpg.imeji.presentation.upload.UploadManager;

/**
 * {@link Uploader} for eSciDoc.
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class EscidocUploader implements Uploader
{
    private Item item;
    private String fileName;
    private String mimetype;
    private Authentication auth;

    /**
     * {@link Uploader} to upload files in eSciDoc, to be used to construct a {@link UploadManager}
     * 
     * @param item - {@link Item} in which the files are uploaded
     * @param contentCategory -
     * @param fileName
     * @param mimetype
     * @param auth
     */
    public EscidocUploader(Item item, String fileName, String mimetype, Authentication auth)
    {
        this.auth = auth;
        this.fileName = fileName;
        this.item = item;
        this.mimetype = mimetype;
    }

    /**
     * Upload a {@link Byte} array file into eSciDoc {@link Item} as a {@link Components}
     */
    @Override
    public URL upload(byte[] stream, String contentCategory)
    {
        try
        {
            URL url = uploadStream(stream);
            EscidocHelper helper = new EscidocHelper();
            helper.addImageToEscidocItem(item, url, contentCategory, fileName, mimetype);
            return url;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Uploading image in eSciDoc: ", e);
        }
    }

    /**
     * Upload a {@link Byte} in the staging area of eSciDoc. REturn the {@link URL} of the file
     * 
     * @param stream
     * @return
     */
    public URL uploadStream(byte[] stream)
    {
        try
        {
            StagingHandlerClient handler = new StagingHandlerClient(auth.getServiceAddress());
            handler.setHandle(auth.getHandle());
            return handler.upload(new ByteArrayInputStream(stream));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error uploading file into eSciDoc staging area", e);
        }
    }

    /**
     * The {@link Item} in which the upload has been done
     * 
     * @return
     */
    public Item getItem()
    {
        return item;
    }

    @Override
    public String getMimetype()
    {
        return mimetype;
    }
}
