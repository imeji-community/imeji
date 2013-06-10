/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.image;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.metadata.MetadataSetBean;
import de.mpg.imeji.presentation.metadata.SingleEditBean;
import de.mpg.imeji.presentation.metadata.extractors.BasicExtractor;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.session.SessionObjectsController;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Bean for a Single image
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImageBean
{
    private static Logger logger = Logger.getLogger(ImageBean.class);
    private String tab;
    private SessionBean sessionBean;
    private Item item;
    private String id;
    private boolean selected;
    private CollectionImeji collection;
    private List<String> techMd;
    private Navigation navigation;
    private MetadataProfile profile;
    private SingleEditBean edit;
    protected String prettyLink;
    private MetadataLabels labels;
    private SingleImageBrowse browse = null;
    private MetadataSetBean mds;

    /**
     * Construct a default {@link ImageBean}
     * 
     * @throws Exception
     */
    public ImageBean() throws Exception
    {
        item = new Item();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        prettyLink = "pretty:editImage";
        labels = (MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class);
    }

    /**
     * Initialize the {@link ImageBean}
     * 
     * @return
     * @throws Exception
     */
    public String getInit() throws Exception
    {
        tab = UrlHelper.getParameterValue("tab");
        loadImage();
        if (item != null)
        {
            if ("techmd".equals(tab))
            {
                initViewTechnicalMetadata();
            }
            else if ("util".equals(tab))
            {
                // TODO
            }
            else
            {
                initViewMetadataTab();
            }
            initBrowsing();
            selected = sessionBean.getSelected().contains(item.getId().toString());
        }
        else
        {
            edit = null;
        }
        return "";
    }

    /**
     * Initialize the metadata information when the "view metadata" tab is called.
     * 
     * @throws Exception
     */
    public void initViewMetadataTab() throws Exception
    {
        if (item != null)
        {
            loadCollection();
            loadProfile();
            removeDeadMetadata();
            labels.init(profile);
            edit = new SingleEditBean(item, profile, getPageUrl());
            mds = new MetadataSetBean(item.getMetadataSet());
        }
    }

    /**
     * Initialize the technical metadata when the "technical metadata" tab is called
     * 
     * @throws Exception
     */
    public void initViewTechnicalMetadata() throws Exception
    {
        try
        {
            techMd = new ArrayList<String>();
            techMd = BasicExtractor.extractTechMd(item);
        }
        catch (Exception e)
        {
            techMd = new ArrayList<String>();
            techMd.add(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initiliaue the {@link SingleImageBrowse} for this {@link ImageBean}
     */
    public void initBrowsing()
    {
        if (item != null)
            browse = new SingleImageBrowse((ImagesBean)BeanHelper.getSessionBean(ImagesBean.class), item, "item", "");
    }


    /**
     * Load the item according to the idntifier defined in the URL
     */
    public void loadImage()
    {
        item = ObjectLoader.loadItem(ObjectHelper.getURI(Item.class, id), sessionBean.getUser());
    }

    /**
     * Load the collection according to the identifier defined in the URL
     */
    public void loadCollection()
    {
        try
        {
            collection = ObjectLoader.loadCollectionLazy(item.getCollection(), sessionBean.getUser());
        }
        catch (Exception e)
        {
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
            collection = null;
        }
    }

    /**
     * Load the {@link MetadataProfile} of the {@link Item}
     */
    public void loadProfile()
    {
        profile = ObjectCachedLoader.loadProfile(item.getMetadataSet().getProfile());
        if (profile == null)
        {
            profile = new MetadataProfile();
        }
    }

    /**
     * If a metadata is deleted in profile, or the type is changed, the metadata should be removed in image
     * 
     * @throws Exception
     */
    public void removeDeadMetadata() throws Exception
    {
        boolean update = false;
        Collection<Metadata> mds = new ArrayList<Metadata>();
        try
        {
            for (Metadata md : item.getMetadataSet().getMetadata())
            {
                boolean isStatement = false;
                for (Statement st : profile.getStatements())
                {
                    if (st.getId().toString().equals(md.getStatement().toString()))
                    {
                        isStatement = true;
                        if (!st.getType().toString().equals(md.getTypeNamespace()))
                        {
                            isStatement = false;
                        }
                    }
                }
                if (isStatement)
                    mds.add(md);
                else
                    update = true;
            }
            if (update)
            {
                ItemController itemController = new ItemController(sessionBean.getUser());
                item.getMetadataSet().setMetadata(mds);
                List<Item> l = new ArrayList<Item>();
                l.add(item);
                itemController.update(l);
            }
        }
        catch (Exception e)
        {
            /* this user has not the privileges to update the image */
        }
    }

    public String getInitLabels() throws Exception
    {
        labels.init(profile);
        return "";
    }

    public List<String> getTechMd() throws Exception
    {
        return techMd;
    }

    public void setTechMd(List<String> md)
    {
        this.techMd = md;
    }

    public String getPageUrl()
    {
        return navigation.getItemUrl() + id;
    }

    public String clearAll()
    {
        sessionBean.getSelected().clear();
        return "pretty:";
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    public void setImage(Item item)
    {
        this.item = item;
    }

    public Item getImage()
    {
        return item;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public boolean getSelected()
    {
        return selected;
    }

    public String getThumbnailImageUrlAsString()
    {
        if (item.getThumbnailImageUrl() == null)
            return "/no_thumb";
        return item.getThumbnailImageUrl().toString();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTab()
    {
        return tab;
    }

    public void setTab(String tab)
    {
        this.tab = tab.toUpperCase();
    }

    public String getNavigationString()
    {
        return "pretty:item";
    }

    public SessionBean getSessionBean()
    {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean)
    {
        this.sessionBean = sessionBean;
    }

    /**
     * Add the current {@link Item} to the active {@link Album}
     * 
     * @return
     * @throws Exception
     */
    public String addToActiveAlbum() throws Exception
    {
        SessionObjectsController soc = new SessionObjectsController();
        List<String> l = new ArrayList<String>();
        l.add(item.getId().toString());
        int sizeBeforeAdd = sessionBean.getActiveAlbumSize();
        soc.addToActiveAlbum(l);
        int sizeAfterAdd = sessionBean.getActiveAlbumSize();
        boolean added = sizeAfterAdd > sizeBeforeAdd;
        if (!added)
        {
            BeanHelper
                    .error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("image")
                            + " "
                            + item.getFilename()
                            + " "
                            + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                                    .getMessage("already_in_active_album"));
        }
        else
        {
            BeanHelper.info(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("image") + " "
                    + item.getFilename() + " "
                    + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("added_to_active_album"));
        }
        return "";
    }

    /**
     * Remove the {@link Item} from the database. If the item was in the current {@link Album}, remove the {@link Item}
     * from it
     * 
     * @throws Exception
     */
    public void remove() throws Exception
    {
        if (getIsInActiveAlbum())
        {
            removeFromActiveAlbum();
        }
        ItemController ic = new ItemController(sessionBean.getUser());
        List<Item> l = new ArrayList<Item>();
        l.add(item);
        ic.delete(l, sessionBean.getUser());
        SessionObjectsController soc = new SessionObjectsController();
        soc.unselectItem(item.getId().toString());
        redirectToBrowsePage();
    }

    /**
     * Remove the {@link Item} from the active {@link Album}
     * 
     * @return
     * @throws Exception
     */
    public String removeFromActiveAlbum() throws Exception
    {
        SessionObjectsController soc = new SessionObjectsController();
        List<String> l = new ArrayList<String>();
        l.add(item.getId().toString());
        soc.removeFromActiveAlbum(l);
        BeanHelper.info(sessionBean.getLabel("image") + " " + item.getFilename() + " "
                + sessionBean.getMessage("success_album_remove_from"));
        return "pretty:";
    }

    /**
     * Return true if the {@link Item} is in the active {@link Album}
     * 
     * @return
     */
    public boolean getIsInActiveAlbum()
    {
        if (sessionBean.getActiveAlbum() != null && item != null)
        {
            return sessionBean.getActiveAlbum().getImages().contains(item.getId());
        }
        return false;
    }

    /**
     * Redirect to the browse page
     * 
     * @throws IOException
     */
    public void redirectToBrowsePage() throws IOException
    {
        FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getBrowseUrl());
    }

    /**
     * Listener of the value of the select box
     * 
     * @param event
     */
    public void selectedChanged(ValueChangeEvent event)
    {
        SessionObjectsController soc = new SessionObjectsController();
        if (event.getNewValue().toString().equals("true"))
        {
            setSelected(true);
            soc.selectItem(item.getId().toString());
        }
        else if (event.getNewValue().toString().equals("false"))
        {
            setSelected(false);
            soc.unselectItem(item.getId().toString());
        }
    }

    public MetadataProfile getProfile()
    {
        return profile;
    }

    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }

    public List<SelectItem> getStatementMenu()
    {
        List<SelectItem> statementMenu = new ArrayList<SelectItem>();
        if (profile == null)
        {
            loadProfile();
        }
        for (Statement s : profile.getStatements())
        {
            statementMenu.add(new SelectItem(s.getId(), s.getLabels().iterator().next().toString()));
        }
        return statementMenu;
    }

    public SingleEditBean getEdit()
    {
        return edit;
    }

    public void setEdit(SingleEditBean edit)
    {
        this.edit = edit;
    }

    public boolean isLocked()
    {
        return Locks.isLocked(this.item.getId().toString(), sessionBean.getUser().getEmail());
    }

    public boolean isEditable()
    {
        Security security = new Security();
        return security.check(OperationsType.UPDATE, sessionBean.getUser(), item) && item != null
                && !item.getStatus().equals(Status.WITHDRAWN) && profile.getStatements().size() > 0;
    }

    public boolean isVisible()
    {
        Security security = new Security();
        return security.check(OperationsType.READ, sessionBean.getUser(), item);
    }

    public boolean isDeletable()
    {
        Security security = new Security();
        return security.check(OperationsType.DELETE, sessionBean.getUser(), item);
    }

    public SingleImageBrowse getBrowse()
    {
        return browse;
    }

    public void setBrowse(SingleImageBrowse browse)
    {
        this.browse = browse;
    }

    public String getDescription()
    {
        for (Statement s : getProfile().getStatements())
        {
            if (s.isDescription())
            {
                for (Metadata md : this.getImage().getMetadataSet().getMetadata())
                {
                    if (md.getStatement().equals(s.getId()))
                    {
                        return md.asFulltext();
                    }
                }
            }
        }
        return item.getFilename();
    }

    /**
     * Returns a list of all albums this image is added to.
     * 
     * @return
     * @throws Exception
     */
    public List<Album> getRelatedAlbums() throws Exception
    {
        List<Album> albums = new ArrayList<Album>();
        AlbumController ac = new AlbumController();
        Search s = new Search(SearchType.ALL, null);
        List<String> res = s.searchSimpleForQuery(SPARQLQueries.selectAlbumIdOfFile(item.getId().toString()), null);
        for (int i = 0; i < res.size(); i++)
        {
            albums.add(ac.retrieveLazy(new URI(res.get(i)), sessionBean.getUser()));
        }
        return albums;
    }

    /**
     * Return the {@link User} having uploaded the file for this item
     * 
     * @return
     * @throws Exception
     */
    public User getImageUploader() throws Exception
    {
        User user = null;
        UserController uc = new UserController();
        user = uc.retrieve(item.getCreatedBy());
        return user;
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
     * getter
     * 
     * @return the mds
     */
    public MetadataSetBean getMds()
    {
        return mds;
    }
}
