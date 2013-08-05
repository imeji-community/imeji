package de.mpg.imeji.presentation.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
     * The Metadata which has been clicked
     */
    private SuperMetadataBean activeMetadata;

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
        //mds.prepareMetadataSetForEditor();
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

    public void addMetadata()
    {
        SuperMetadataBean newMd = activeMetadata.copyEmpty();
        newMd.addEmtpyChilds(profile);
        mds.getTree().add(newMd);
    }

    public void removeMetadata()
    {
        mds.getTree().remove(activeMetadata);
    }

    /**
     * Clear the {@link Metadata} for one {@link Statement}: remove all {@link Metadata} and its Childs and add an empty
     * one TODO Will not work, need testing - Bastien
     * 
     * @param st
     */
    public void clear(Statement st)
    {
        List<SuperMetadataBean> l = new ArrayList<SuperMetadataBean>();
        for (SuperMetadataBean smd : mds.getTree().getList())
        {
            if (st.getId().compareTo(smd.getStatement().getId()) != 0
                    && (smd.getLastParent() != null && st.getId().compareTo(smd.getLastParent()) != 0))
                l.add(smd);
        }
        mds.initTreeFromList(l);
        init(asItem());
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
     * Add a {@link Metadata} to this {@link EditorItemBean} at the position requested
     * 
     * @param s
     * @param position
     */
    private void addMetadata(Statement s, int position)
    {
        // SuperMetadataBean smd = new SuperMetadataBean(MetadataFactory.createMetadata(s), s);
        // smd.setLastParent(ProfileHelper.getLastParent(s, ObjectCachedLoader.loadProfile(getProfile())));
        // metadata.add(position, smd);
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

//    public List<SuperMetadataBean> getMetadata()
//    {
//        return mds.getMetadataFlat();
//    }

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

    /**
     * @return the newMetadata
     */
    public SuperMetadataBean getActiveMetadata()
    {
        return activeMetadata;
    }

    /**
     * @param newMetadata the newMetadata to set
     */
    public void setActiveMetadata(SuperMetadataBean newMetadata)
    {
        this.activeMetadata = newMetadata;
    }
}
