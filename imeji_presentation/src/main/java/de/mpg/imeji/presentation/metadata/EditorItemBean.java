package de.mpg.imeji.presentation.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;

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
     * @param item
     */
    public EditorItemBean(Item item)
    {
        init(item);
    }
    
    /**
     * Intialize the {@link EditorItemBean} with an {@link Item}
     * @param item
     */
    public void init(Item item)
    {
        this.item = item;
        metadata = new ArrayList<SuperMetadataBean>();
        for (Metadata md : item.getMetadataSet().getMetadata())
        {
            metadata.add(new SuperMetadataBean(md));
        }
    }

    /**
     * Get {@link EditorItemBean} as {@link Item}
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

    public URI getThumbnail()
    {
        return item.getThumbnailImageUrl();
    }

    public String getFilename()
    {
        return item.getFilename();
    }

    public URI getProfile()
    {
        return item.getMetadataSet().getProfile();
    }

    public List<SuperMetadataBean> getMetadata()
    {
        return metadata;
    }

    public void setMetadata(List<SuperMetadataBean> metadata)
    {
        this.metadata = metadata;
    }
}
