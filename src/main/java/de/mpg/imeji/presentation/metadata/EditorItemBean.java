package de.mpg.imeji.presentation.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;
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
    public EditorItemBean(Item item)
    {
        init(item);
    }

    /**
     * Intialize the {@link EditorItemBean} with an {@link Item}
     * 
     * @param item
     */
    public void init(Item item)
    {
        this.item = item;
        metadata = new ArrayList<SuperMetadataBean>();
        MetadataSetBean mdsb = new MetadataSetBean(item.getMetadataSet());
        mdsb.prepareMetadataSetForEditor();
        metadata = mdsb.getMetadata();
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
     * Add a {@link Metadata} and all its childs to this {@link EditorItemBean} after the position requested
     * 
     * @param position
     */
    public void addMetadata(int position)
    {
        Statement s = metadata.get(position).getStatement();
        List<Statement> childs = ProfileHelper.getChilds(s, ObjectCachedLoader.loadProfile(getProfile()), false);
        // increment the position with the number of childs
        position = position + childs.size();
        // Add a new metadata with the same statement
        addMetadata(s, position + 1);
        // Add the childs
        int i = 2;
        for (Statement st : childs)
        {
            addMetadata(st, position + i);
            i++;
        }
        resetPositionToMetadata();
    }

    /**
     * Rmove the {@link SuperMetadataBean} and all its childs at the defined position in the editor
     * 
     * @param position
     */
    public void removeMetadata(int position)
    {
        Statement s = metadata.get(position).getStatement();
        List<Statement> childs = ProfileHelper.getChilds(s, ObjectCachedLoader.loadProfile(getProfile()), false);
        // remove the first metadata
        metadata.remove(position);
        // remove the childs
        for (int i = 0; i < childs.size(); i++)
        {
            metadata.remove(position);
        }
        resetPositionToMetadata();
    }

    /**
     * Clear the {@link Metadata} for one {@link Statement}: remove all {@link Metadata} and its Childs and add an empty
     * one
     * 
     * @param st
     */
    public void clear(Statement st)
    {
        List<SuperMetadataBean> l = new ArrayList<SuperMetadataBean>();
        for (SuperMetadataBean smd : metadata)
        {
            if (st.getId().compareTo(smd.getStatement().getId()) != 0
                    && (smd.getLastParent() != null && st.getId().compareTo(smd.getLastParent()) != 0))
                l.add(smd);
        }
        metadata = l;
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
        for (SuperMetadataBean smd : metadata)
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
        SuperMetadataBean smd = new SuperMetadataBean(MetadataFactory.createMetadata(s), s);
        smd.setLastParent(ProfileHelper.getLastParent(s, ObjectCachedLoader.loadProfile(getProfile())));
        metadata.add(position, smd);
    }

    /**
     * reset the position of all {@link SuperMetadataBean}
     */
    private void resetPositionToMetadata()
    {
        int pos = 0;
        for (SuperMetadataBean smd : metadata)
        {
            smd.setPos(pos);
            pos++;
        }
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
