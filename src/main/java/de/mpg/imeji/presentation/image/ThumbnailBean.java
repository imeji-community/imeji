/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.image;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.presentation.metadata.MetadataSetBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.session.SessionObjectsController;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.CommonUtils;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;

/**
 * Bean for Thumbnail list elements. Each element of a list with thumbnail is an instance of a {@link ThumbnailBean}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ThumbnailBean
{
    private String link = "";
    private String filename = "";
    private String caption = "";
    private URI uri = null;
    private String id;
    private URI profile = null;
    private List<Metadata> metadata = new ArrayList<Metadata>();
    private List<Statement> statements = new ArrayList<Statement>();
    private boolean selected = false;
    private boolean isInActiveAlbum = false;
    private SessionBean sessionBean;
    private static Logger logger = Logger.getLogger(ThumbnailBean.class);
    private MetadataSetBean mds;
    private CollectionImeji collection;
    private String collectionName = "";

    /**
     * Bean for Thumbnail list elements. Each element of a list with thumbnail is an instance of a {@link ThumbnailBean}
     * 
     * @param item
     */
    public ThumbnailBean(Item item)
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        uri = item.getId();
        id = ObjectHelper.getId(uri);
        link = item.getThumbnailImageUrl().toString();
        profile = item.getMetadataSet().getProfile();
        filename = item.getFilename();
        setCollection(ObjectCachedLoader.loadCollection(item.getCollection()));
        metadata = (List<Metadata>)item.getMetadataSet().getMetadata();
        statements = loadStatements(item.getMetadataSet().getProfile());
        collectionName = ObjectCachedLoader.loadCollection(item.getCollection()).getMetadata().getTitle();
        caption = findCaption();
        selected = sessionBean.getSelected().contains(uri.toString());
        if (sessionBean.getActiveAlbum() != null)
        {
            isInActiveAlbum = sessionBean.getActiveAlbum().getImages().contains(item.getId());
        }
        mds = new MetadataSetBean(item.getMetadataSet(), false);
    }

    /**
     * Inititialize the popup with the metadata for this image. The method is called directly from xhtml
     * 
     * @return
     * @throws Exception
     */
    public String getInitPopup() throws Exception
    {
        List<Item> l = new ArrayList<Item>();
        Item im = new Item();
        im.getMetadataSets().add(ImejiFactory.newMetadataSet(profile));
        l.add(im);
        return "";
    }

    /**
     * Load the statements of a {@link MetadataProfile} according to its id ( {@link URI} )
     * 
     * @param uri
     * @return
     */
    private List<Statement> loadStatements(URI uri)
    {
        try
        {
            MetadataProfile profile = ObjectCachedLoader.loadProfile(uri);
            if (profile != null)
            {
                return (List<Statement>)profile.getStatements();
            }
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_profile_load") + " " + uri + "  "
                    + sessionBean.getLabel("of") + " " + uri);
            // TODO
            logger.error("Error load profile " + uri + " of item " + uri, e);
        }
        return new ArrayList<Statement>();
    }

    /**
     * Find the caption for this {@link ThumbnailBean} as defined in the {@link MetadataProfile}. If none defined in the
     * {@link MetadataProfile} return the filename
     * 
     * @return
     */
    private String findCaption()
    {
        for (Statement s : statements)
        {
            if (s.isDescription())
            {
                for (Metadata md : metadata)
                {
                    if (md.getStatement().equals(s.getId()))
                    {
                        if (md instanceof Link)
                            return ((Link)md).getLabel();
                        if (md instanceof Publication)
                            return CommonUtils.removeTags(((Publication)md).getCitation());
                        return md.asFulltext();
                    }
                }
            }
        }
        return filename;
    }

    /**
     * Listener for the select box of this {@link ThumbnailBean}
     * 
     * @param event
     */
    public void selectedChanged(ValueChangeEvent event)
    {
        SessionObjectsController soc = new SessionObjectsController();
        if (event.getNewValue().toString().equals("true"))
        {
            setSelected(true);
            soc.selectItem(uri.toString());
        }
        else if (event.getNewValue().toString().equals("false"))
        {
            setSelected(false);
            soc.unselectItem(uri.toString());
        }
    }

    /**
     * getter
     * 
     * @return
     */
    public String getLink()
    {
        return link;
    }

    /**
     * setter
     * 
     * @param link
     */
    public void setLink(String link)
    {
        this.link = link;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * setter
     * 
     * @param filename
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getCaption()
    {
        return caption;
    }

    /**
     * setter
     * 
     * @param caption
     */
    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    /**
     * getter
     * 
     * @return
     */
    public URI getUri()
    {
        return uri;
    }

    /**
     * setter
     * 
     * @param id
     */
    public void setUri(URI id)
    {
        this.uri = id;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<Metadata> getMetadata()
    {
        return metadata;
    }

    /**
     * setter
     * 
     * @param metadata
     */
    public void setMetadata(List<Metadata> metadata)
    {
        this.metadata = metadata;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<Statement> getStatements()
    {
        return statements;
    }

    /**
     * setter
     * 
     * @param statements
     */
    public void setStatements(List<Statement> statements)
    {
        this.statements = statements;
    }

    /**
     * getter
     * 
     * @return
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * setter
     * 
     * @param selected
     */
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    /**
     * getter
     * 
     * @return
     */
    public boolean isInActiveAlbum()
    {
        return isInActiveAlbum;
    }

    /**
     * setter
     * 
     * @param isInActiveAlbum
     */
    public void setInActiveAlbum(boolean isInActiveAlbum)
    {
        this.isInActiveAlbum = isInActiveAlbum;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * getter
     * 
     * @return the mds
     */
    public MetadataSetBean getMds()
    {
        return mds;
    }

    /**
     * setter
     * 
     * @param mds the mds to set
     */
    public void setMds(MetadataSetBean mds)
    {
        this.mds = mds;
    }

    /**
     * @return the collection
     */
    public CollectionImeji getCollection()
    {
        return collection;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    /**
     * @return the collectionName
     */
    public String getCollectionName()
    {
        return collectionName;
    }

    /**
     * @param collectionName the collectionName to set
     */
    public void setCollectionName(String collectionName)
    {
        this.collectionName = collectionName;
    }
}
