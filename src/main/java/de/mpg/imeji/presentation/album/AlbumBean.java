/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.album;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.ingest.vo.Items;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.ContainerBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.image.ThumbnailBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.CommonUtils;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * The javabean for the {@link Album}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AlbumBean extends ContainerBean
{
    protected SessionBean sessionBean = null;
    private Album album = null;
    private String id = null;
    private List<SelectItem> profilesMenu = new ArrayList<SelectItem>();
    private boolean active;
    private String tab;
    /**
     * True if the {@link AlbumBean} is used for the create page, else false
     */
    // protected boolean create;
    private boolean selected;
    private static Logger logger = Logger.getLogger(AlbumBean.class);
    /**
     * Maximum number of character displayed in the list for the description
     */
    private static final int DESCRIPTION_MAX_SIZE = 300;
    /**
     * A small description when the description of the {@link Album} is too large for the list view
     */
    // private String smallDescription = null;
    private String description = "";
    private String descriptionFull = null;
    private ThumbnailBean thumbnail;
    private Navigation navigation;

    /**
     * Construct an {@link AlbumBean} from an {@link Album}
     * 
     * @param album
     */
    public AlbumBean(Album album)
    {
        this.album = album;
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        this.id = ObjectHelper.getId(album.getId());
        if (sessionBean.getActiveAlbum() != null && sessionBean.getActiveAlbum().getId().equals(album.getId()))
        {
            active = true;
        }
        AlbumController ac = new AlbumController();
        this.album = (Album)ac.loadContainerItems(album, sessionBean.getUser(), -1, 0);
        loadItems(sessionBean.getUser());
        description = album.getMetadata().getDescription();
        descriptionFull = description;
        description = CommonUtils.removeTags(description);
        if (description != null && description.length() > DESCRIPTION_MAX_SIZE)
        {
            description = description.substring(0, DESCRIPTION_MAX_SIZE) + "...";
        }
        // Init the thumbnail
        if (!album.getImages().isEmpty())
        {
            ItemController ic = new ItemController();
            try
            {
                thumbnail = new ThumbnailBean(ic.retrieve(album.getImages().iterator().next(), sessionBean.getUser()));
            }
            catch (Exception e)
            {
                logger.error("Error loading thumbnail of album", e);
            }
        }
    }

    /**
     * Construct an emtpy {@link AlbumBean}
     */
    public AlbumBean()
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    /**
     * Load the {@link Album} and its {@link Item} when the {@link AlbumBean} page is called, and initialize it.
     */
    public void initView()
    {
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        try
        {
            if (id != null)
            {
                Album a = ObjectLoader.loadAlbumLazy(ObjectHelper.getURI(Album.class, id), sessionBean.getUser());
                ItemController ic = new ItemController(sessionBean.getUser());
                ic.loadContainerItems(a, sessionBean.getUser(), 1, 0);
                setAlbum(a);
                if (album != null)
                {
                    if (sessionBean.getActiveAlbum() != null
                            && sessionBean.getActiveAlbum().getId().equals(album.getId()))
                    {
                        active = true;
                        sessionBean.setActiveAlbum(album);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Error init album view", e);
        }
    }

    /**
     * Initialize the album form to edit the metadata of the album
     */
    public void initEdit()
    {
        AlbumController ac = new AlbumController();
        try
        {
            setAlbum(ac.retrieveLazy(ObjectHelper.getURI(Album.class, id), sessionBean.getUser()));
            if (sessionBean.getActiveAlbum() != null
                    && sessionBean.getActiveAlbum().getId().toString().equals(album.getId().toString()))
            {
                active = true;
            }
        }
        catch (Exception e)
        {
            BeanHelper.error(e.getMessage());
            logger.error("Error init album edit", e);
        }
    }

    /**
     * Return the link for the Cancel button
     * 
     * @return
     */
    public String getCancel()
    {
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        return nav.getAlbumUrl() + id + "/" + nav.getInfosPath();
    }

    /**
     * True if a the information about the {@link Album} are valid
     * 
     * @return
     */
    public boolean valid()
    {
        boolean valid = true;
        boolean hasAuthor = false;
        if (getAlbum().getMetadata().getTitle() == null || "".equals(getAlbum().getMetadata().getTitle()))
        {
            BeanHelper.error(sessionBean.getMessage("collection_create_error_title"));
            valid = false;
        }
        for (Person c : getAlbum().getMetadata().getPersons())
        {
            boolean hasOrganization = false;
            if (!"".equals(c.getFamilyName()))
            {
                hasAuthor = true;
            }
            for (Organization o : c.getOrganizations())
            {
                if (!"".equals(o.getName()) || "".equals(c.getFamilyName()))
                {
                    hasOrganization = true;
                }
                if (hasOrganization && "".equals(c.getFamilyName()))
                {
                    BeanHelper.error(sessionBean.getMessage("error_author_need_one_family_name"));
                    valid = false;
                }
            }
            if (!hasOrganization)
            {
                BeanHelper.error(sessionBean.getMessage("error_author_need_one_organization"));
                valid = false;
            }
        }
        if (!hasAuthor)
        {
            BeanHelper.error(sessionBean.getMessage("error_album_need_one_author"));
            valid = false;
        }
        return valid;
    }

    @Override
    protected String getErrorMessageNoAuthor()
    {
        return "error_album_need_one_author";
    }

    /**
     * Listener for the discard comment
     * 
     * @param event
     */
    public void discardCommentListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue().toString().trim().length() > 0)
        {
            album.setDiscardComment(event.getNewValue().toString().trim());
        }
    }

    /**
     * getter
     * 
     * @return
     */
    protected String getNavigationString()
    {
        return "pretty:";
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<SelectItem> getProfilesMenu()
    {
        return profilesMenu;
    }

    /**
     * setter
     * 
     * @param profilesMenu
     */
    public void setProfilesMenu(List<SelectItem> profilesMenu)
    {
        this.profilesMenu = profilesMenu;
    }

    /**
     * return thr number of item in the album
     * 
     * @return
     */
    public int getSize()
    {
        if (album != null)
            return album.getImages().size();
        return 0;
    }

    /**
     * True if the current user is the owner of the albun
     * 
     * @return
     */
    public boolean getIsOwner()
    {
        if (sessionBean.getUser() != null)
        {
            return getAlbum().getCreatedBy().equals(ObjectHelper.getURI(User.class, sessionBean.getUser().getEmail()));
        }
        else
            return false;
    }

    /**
     * Load the 5 first {@link Items} of the {@link Album} as {@link ThumbnailBean}
     * 
     * @return
     * @throws Exception
     */
    public List<ThumbnailBean> getThumbnails() throws Exception
    {
        ItemController ic = new ItemController(sessionBean.getUser());
        if (album != null)
        {
            List<String> uris = new ArrayList<String>();
            for (URI uri : album.getImages())
            {
                uris.add(uri.toString());
            }
            return ImejiFactory.imageListToThumbList(ic.loadItems(uris, 13, 0));
        }
        return null;
    }

    /**
     * Save (create or update) the {@link Album} in the database
     * 
     * @return
     * @throws Exception
     */
    public String save() throws Exception
    {
        update();
        return "";
    }

    /**
     * Update the {@link Album} in the dabatase with the values defined in this {@link AlbumBean}
     * 
     * @return
     * @throws Exception
     */
    public String update() throws Exception
    {
        AlbumController ac = new AlbumController();
        if (valid())
        {
            ac.updateLazy(album, sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_album_update"));
            Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
            FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .redirect(
                            navigation.getAlbumUrl() + ObjectHelper.getId(getAlbum().getId()) + "/"
                                    + navigation.getInfosPath() + "?init=1");
        }
        return "";
    }

    /**
     * setter
     * 
     * @param album
     */
    public void setAlbum(Album album)
    {
        this.album = album;
    }

    /**
     * getter
     * 
     * @return
     */
    public Album getAlbum()
    {
        return album;
    }

    /**
     * Return the all author of this album as a single {@link String}
     * 
     * @return
     */
    public String getPersonString()
    {
        String personString = "";
        for (Person p : album.getMetadata().getPersons())
        {
            if (!"".equals(personString))
            {
                personString += "; ";
            }
            personString +=  p.getFamilyName() + ", " +  p.getGivenName();
        }
        return personString;
    }

    /**
     * setter
     * 
     * @param active
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /**
     * getter
     * 
     * @return
     */
    public boolean getActive()
    {
        return active;
    }

    /**
     * Make the current {@link Album} active
     * 
     * @return
     */
    public String makeActive()
    {
        sessionBean.setActiveAlbum(this.album);
        this.setActive(true);
        return "pretty:";
    }

    /**
     * Make the current {@link Album} inactive
     * 
     * @return
     */
    public String makeInactive()
    {
        sessionBean.setActiveAlbum(null);
        this.setActive(false);
        return "pretty:";
    }

    /**
     * Release the current {@link Album}
     * 
     * @return
     */
    public String release()
    {
        AlbumController ac = new AlbumController();
        try
        {
            ac.release(album, sessionBean.getUser());
            makeInactive();
            BeanHelper.info(sessionBean.getMessage("success_album_release"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_album_release"));
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        return "pretty:";
    }

    /**
     * delete an {@link Album}
     * 
     * @return
     */
    public String delete()
    {
        AlbumController c = new AlbumController();
        try
        {
            makeInactive();
            c.delete(album, sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_album_delete"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_album_delete"));
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        return "pretty:albums";
    }

    /**
     * Discard the {@link AlbumImeji} of this {@link Album}
     * 
     * @return
     * @throws Exception
     */
    public String withdraw() throws Exception
    {
        AlbumController c = new AlbumController();
        try
        {
            c.withdraw(album, sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_album_withdraw"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_album_withdraw"));
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        return "pretty:";
    }

    /**
     * True if the {@link Album} is selected in the album list
     * 
     * @return
     */
    public boolean getSelected()
    {
        if (sessionBean.getSelectedAlbums().contains(album.getId()))
            selected = true;
        else
            selected = false;
        return selected;
    }

    /**
     * setter: called when the user click on the select box to select the {@link Album}. Set the status "selected" in
     * the session
     * 
     * @param selected
     */
    public void setSelected(boolean selected)
    {
        if (selected)
        {
            if (!(sessionBean.getSelectedAlbums().contains(album.getId())))
                sessionBean.getSelectedAlbums().add(album.getId());
        }
        else
            sessionBean.getSelectedAlbums().remove(album.getId());
        this.selected = selected;
    }

    /**
     * getter
     * 
     * @return the thumbnail
     */
    public ThumbnailBean getThumbnail()
    {
        return thumbnail;
    }

    /**
     * setter
     * 
     * @param thumbnail the thumbnail to set
     */
    public void setThumbnail(ThumbnailBean thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public String getFormattedDescription()
    {
        if (this.getAlbum() == null || this.getAlbum().getMetadata().getDescription() == null)
            return "";
        return this.getAlbum().getMetadata().getDescription().replaceAll("\n", "<br/>");
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDescriptionFull()
    {
        return descriptionFull;
    }

    public void setDescriptionFull(String descriptionFull)
    {
        this.descriptionFull = descriptionFull;
    }

    public String getTab()
    {
        if (UrlHelper.getParameterValue("tab") != null)
        {
            tab = UrlHelper.getParameterValue("tab").toUpperCase();
        }
        return tab;
    }

    public void setTab(String tab)
    {
        this.tab = tab.toUpperCase();
    }

    public String getPageUrl()
    {
        return navigation.getAlbumUrl() + id;
    }

    public User getAlbumCreator() throws Exception
    {
        UserController uc = new UserController(sessionBean.getUser());
        User user = uc.retrieve(album.getCreatedBy());
        return user;
    }

    public String getCitation()
    {
        String title = album.getMetadata().getTitle();
        String author = this.getPersonString();
        String url = this.getPageUrl();
        String citation = title + " " + sessionBean.getLabel("from") + " <i>" + author + "</i></br>" + url;
        return citation;
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.presentation.beans.ContainerBean#getType()
     */
    @Override
    public String getType()
    {
        return CONTAINER_TYPE.ALBUM.name();
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.presentation.beans.ContainerBean#getContainer()
     */
    @Override
    public Container getContainer()
    {
        return getAlbum();
    }

    /*
     * (non-Javadoc) following getter functions are for standardization and simplification the output of album data in a
     * general template system
     */
    public String getTitle()
    {
        if (getContainer() != null)
            return getContainer().getMetadata().getTitle();
        return null;
    }

    public String getAuthors()
    {
        return this.getPersonString();
    }

    public Date getCreationDate()
    {
        return this.getContainer().getCreated().getTime();
    }

    public Date getLastModificationDate()
    {
        return this.getContainer().getModified().getTime();
    }

    public Date getVersionDate()
    {
        return this.getContainer().getVersionDate().getTime();
    }

    public Status getStatus()
    {
        return this.getContainer().getStatus();
    }

    public String getDiscardComment()
    {
        return this.getContainer().getDiscardComment();
    }

    public void setDiscardComment(String comment)
    {
        this.getContainer().setDiscardComment(comment);
    }
}
