/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.image;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.factory.MetadataSetFactory;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;

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
    // security
    private boolean editable = false;
    private boolean visible = false;
    private boolean deletable = false;
    private SessionBean sessionBean;
    private static Logger logger = Logger.getLogger(ThumbnailBean.class);

    public ThumbnailBean(Item item)
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        uri = item.getId();
        id = uri.getPath().split("/item/")[1];
        link = item.getThumbnailImageUrl().toString();
        profile = item.getMetadataSet().getProfile();
        filename = item.getFilename();
        metadata = (List<Metadata>)item.getMetadataSet().getMetadata();
        statements = loadStatements(item.getMetadataSet().getProfile());
        caption = findCaption();
        selected = sessionBean.getSelected().contains(uri);
        if (sessionBean.getActiveAlbum() != null)
        {
            isInActiveAlbum = sessionBean.getActiveAlbum().getAlbum().getImages().contains(item.getId());
        }
        initSecurity(item);
    }

    public String getInitPopup() throws Exception
    {
        List<Item> l = new ArrayList<Item>();
        Item im = new Item();
        im.getMetadataSets().add(MetadataSetFactory.create(profile));
        l.add(im);
        ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class)).init(l);
        return "";
    }

    private void initSecurity(Item item)
    {
        Security security = new Security();
        editable = security.check(OperationsType.UPDATE, sessionBean.getUser(), item) && item != null
                && !item.getProperties().getStatus().equals(Status.WITHDRAWN);
        visible = security.check(OperationsType.READ, sessionBean.getUser(), item);
        deletable = security.check(OperationsType.DELETE, sessionBean.getUser(), item);
    }

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
            logger.error("Error load profile " + uri + " of image " + uri, e);
        }
        return new ArrayList<Statement>();
    }

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
                        return md.getSearchValue();
                    }
                }
            }
        }
        return filename;
    }

    public void selectedChanged(ValueChangeEvent event)
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (event.getNewValue().toString().equals("true") && !sessionBean.getSelected().contains(uri))
        {
            selected = true;
            select();
        }
        else if (event.getNewValue().toString().equals("false") && sessionBean.getSelected().contains(uri))
        {
            selected = false;
            select();
        }
    }

    public String select()
    {
        if (!selected)
        {
            ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getSelected().remove(uri);
        }
        else
        {
            ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getSelected().add(uri);
        }
        return "";
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getCaption()
    {
        return caption;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    public URI getUri()
    {
        return uri;
    }

    public void setUri(URI id)
    {
        this.uri = id;
    }

    public List<Metadata> getMetadata()
    {
        return metadata;
    }

    public void setMetadata(List<Metadata> metadata)
    {
        this.metadata = metadata;
    }

    public List<Statement> getStatements()
    {
        return statements;
    }

    public void setStatements(List<Statement> statements)
    {
        this.statements = statements;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public static Logger getLogger()
    {
        return logger;
    }

    public static void setLogger(Logger logger)
    {
        ThumbnailBean.logger = logger;
    }

    public boolean isInActiveAlbum()
    {
        return isInActiveAlbum;
    }

    public void setInActiveAlbum(boolean isInActiveAlbum)
    {
        this.isInActiveAlbum = isInActiveAlbum;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isDeletable()
    {
        return deletable;
    }

    public void setDeletable(boolean deletable)
    {
        this.deletable = deletable;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
