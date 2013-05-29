package de.mpg.imeji.presentation.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.util.ProfileHelper;

/**
 * Bean for item element in the metadata editors
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class EditorItemBean
{
    private List<SuperMetadataBean> metadata;
    private Item item;

    /**
     * Bean for item element in the metadata editors
     * 
     * @param item
     */
    public EditorItemBean(Item item, MetadataProfile profile)
    {
        init(item, profile);
    }

    /**
     * Intialize the {@link EditorItemBean} with an {@link Item}
     * 
     * @param item
     */
    public void init(Item item, MetadataProfile profile)
    {
        this.item = item;
        metadata = new ArrayList<SuperMetadataBean>();
        for (Metadata md : item.getMetadataSet().getMetadata())
        {
            metadata.add(new SuperMetadataBean(md, ProfileHelper.getStatement(md.getStatement(), profile)));
        }
    }

    /**
     * Get {@link EditorItemBean} as {@link Item}
     * 
     * @return
     */
    public Item asItem()
    {
        item.getMetadataSet().getMetadata().clear();
        for (SuperMetadataBean smdb : metadata)
        {
            item.getMetadataSet().getMetadata().add(smdb.asMetadata());
        }
        return item;
    }

    /**
     * get the thumbnail of the {@link Item}
     * 
     * @return
     */
    public URI getThumbnail()
    {
        return item.getThumbnailImageUrl();
    }

    /**
     * get the filename of the {@link Item}
     * 
     * @return
     */
    public String getFilename()
    {
        return item.getFilename();
    }

    /**
     * getter
     * 
     * @return
     */
    public URI getProfile()
    {
        return item.getMetadataSet().getProfile();
    }

    /**
     * getter
     * 
     * @return
     */
    public List<SuperMetadataBean> getMetadata()
    {
        return metadata;
    }

    /**
     * setter
     * 
     * @param metadata
     */
    public void setMetadata(List<SuperMetadataBean> metadata)
    {
        this.metadata = metadata;
    }
}
