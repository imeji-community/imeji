/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.metadata.SingleEditBean;
import de.mpg.imeji.presentation.metadata.extractors.BasicExtractor;
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

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

    public ImageBean(Item img) throws Exception
    {
        item = img;
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        prettyLink = "pretty:editImage";
        labels = (MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class);
        if (sessionBean.getSelected().contains(item.getId()))
        {
            setSelected(true);
        }
        loadProfile();
        removeDeadMetadata();
        sortMetadataAccordingtoProfile();
    }

    public ImageBean() throws Exception
    {
        item = new Item();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        prettyLink = "pretty:editImage";
        labels = (MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class);
    }

    public String getInitPopup() throws Exception
    {
        labels.init(profile);
        return "";
    }

    public String getInit() throws Exception
    {
        tab = UrlHelper.getParameterValue("tab");
        loadImage();
        if ("techmd".equals(tab))
        {
            initViewTechnicalMetadata();
        }
        else
        {
            initViewMetadataTab();
        }
        initBrowsing();
        selected = sessionBean.getSelected().contains(item.getId().toString());
        return "";
    }

    public void initViewMetadataTab() throws Exception
    {
        loadCollection();
        loadProfile();
        removeDeadMetadata();
        sortMetadataAccordingtoProfile();
        labels.init(profile);
        edit = new SingleEditBean(item, profile, getPageUrl());
        cleanImageMetadata();
    }

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

    public void initBrowsing()
    {
        browse = new SingleImageBrowse((ImagesBean)BeanHelper.getSessionBean(ImagesBean.class), item);
    }

    private void sortMetadataAccordingtoProfile()
    {
        Collection<Metadata> mdSorted = new ArrayList<Metadata>();
        if (profile != null)
        {
            for (Statement st : profile.getStatements())
            {
                for (Metadata md : item.getMetadataSet().getMetadata())
                {
                    if (st.getId().equals(md.getStatement()))
                    {
                        mdSorted.add(md);
                    }
                }
            }
        }
        item.getMetadataSet().setMetadata(mdSorted);
    }

    public void loadImage()
    {
        try
        {
            item = ObjectLoader.loadImage(ObjectHelper.getURI(Item.class, id), sessionBean.getUser());
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_image_load"));
            logger.error(sessionBean.getMessage("error_image_load"), e);
        }
    }

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

    public void loadProfile()
    {
        try
        {
            profile = sessionBean.getProfileCached().get(item.getMetadataSet().getProfile());
            if (profile == null)
            {
                profile = ObjectLoader.loadProfile(item.getMetadataSet().getProfile(), sessionBean.getUser());
            }
            if (profile == null)
            {
                profile = new MetadataProfile();
            }
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_profile_load") + " " + item.getMetadataSet().getProfile()
                    + "  " + sessionBean.getLabel("of") + " " + item.getId());
            profile = new MetadataProfile();
            logger.error("Error load profile " + item.getMetadataSet().getProfile() + " of image " + item.getId(), e);
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
            /* this user has not the priviliges to update the image */
        }
    }

    /**
     * Remove empty metadata
     */
    private void cleanImageMetadata()
    {
        for (int i = 0; i < item.getMetadataSet().getMetadata().size(); i++)
        {
            if (MetadataHelper.isEmpty(((List<Metadata>)item.getMetadataSet().getMetadata()).get(i)))
            {
                ((List<Metadata>)item.getMetadataSet().getMetadata()).remove(i);
                i--;
            }
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

    public String addToActiveAlbum() throws Exception
    {
        AlbumController ac = new AlbumController(sessionBean.getUser());
        List<String> l = new ArrayList<String>();
        l.add(item.getId().toString());
        boolean added = ac.addToAlbum(sessionBean.getActiveAlbum(), l, sessionBean.getUser()).size() == 0;
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

    public String removeFromAlbum() throws Exception
    {
        AlbumController ac = new AlbumController(sessionBean.getUser());
        List<String> l = new ArrayList<String>();
        l.add(item.getId().toString());
        ac.removeFromAlbum(sessionBean.getActiveAlbum(), l, sessionBean.getUser());
        BeanHelper.info(sessionBean.getLabel("image") + " " + item.getFilename() + " "
                + sessionBean.getMessage("success_album_remove_from"));
        return "pretty:";
    }

    public boolean getIsInActiveAlbum()
    {
        if (sessionBean.getActiveAlbum() != null)
        {
            return sessionBean.getActiveAlbum().getImages().contains(item.getId());
        }
        return false;
    }

    public void selectedChanged(ValueChangeEvent event)
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (event.getNewValue().toString().equals("true") && !sessionBean.getSelected().contains(item.getId()))
        {
            setSelected(true);
            select();
        }
        else if (event.getNewValue().toString().equals("false") && sessionBean.getSelected().contains(item.getId()))
        {
            setSelected(false);
            select();
        }
    }

    public String select()
    {
        if (!selected)
        {
            ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getSelected().remove(item.getId().toString());
        }
        else
        {
            ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getSelected().add(item.getId().toString());
        }
        return "";
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
                && !item.getStatus().equals(Status.WITHDRAWN);
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
}
