package de.mpg.imeji.presentation.metadata;

import java.net.URI;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;

/**
 * Bean for item element in the metadata editors
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class EditorItemBean
{
    private MetadataSetBean mds;
    private Item item;
    private MetadataProfile profile;

    /**
     * Bean for item element in the metadata editors
     * 
     * @param item
     */
    public EditorItemBean(Item item, MetadataProfile profile)
    {
        init(item);
        this.profile = profile;
    }

    /**
     * Intialize the {@link EditorItemBean} with an {@link Item}
     * 
     * @param item
     */
    public void init(Item item)
    {
        this.item = item;
        mds = new MetadataSetBean(item.getMetadataSet());
    }

    /**
     * Get {@link EditorItemBean} as {@link Item}
     * 
     * @return
     */
    public Item asItem()
    {
        item.getMetadataSet().getMetadata().clear();
        for (SuperMetadataBean smdb : mds.getTree().getList())
        {
            item.getMetadataSet().getMetadata().add(smdb.asMetadata());
        }
        return item;
    }

    /**
     * Add a Metadata of the same type as the passed metadata
     */
    public void addMetadata(SuperMetadataBean smb)
    {
        SuperMetadataBean newMd = smb.copyEmpty();
        newMd.addEmtpyChilds(profile);
        mds.getTree().add(newMd);
    }

    /**
     * Remove the active metadata
     */
    public void removeMetadata(SuperMetadataBean smb)
    {
        mds.getTree().remove(smb);
    }

    /**
     * Clear the {@link Metadata} for one {@link Statement}: remove all {@link Metadata} and its Childs and add an empty
     * one
     * 
     * @param st
     */
    public void clear(Statement st)
    {
        for (SuperMetadataBean smd : mds.getTree().getList())
        {
            if (st.getId().compareTo(smd.getStatement().getId()) == 0)
            {
                // Clear the childs
                for (SuperMetadataBean child : mds.getTree().getChilds(smd.getTreeIndex()))
                    child.clear();
                // clear the metadata
                smd.clear();
            }
        }
        // Remove all emtpy values
        mds.trim();
    }

    /**
     * Return the position of the last {@link SuperMetadataBean} in the editor for this {@link Statement}
     * 
     * @param st
     * @return
     */
    public int getLastPosition(Statement st)
    {
        int p = 0;
        for (SuperMetadataBean smd : mds.getTree().getList())
        {
            if (st.getId().compareTo(smd.getStatement().getId()) == 0 && smd.getPos() > p)
                p = smd.getPos();
        }
        return p;
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
     * @return the mds
     */
    public MetadataSetBean getMds()
    {
        return mds;
    }

    /**
     * @param mds the mds to set
     */
    public void setMds(MetadataSetBean mds)
    {
        this.mds = mds;
    }
}
