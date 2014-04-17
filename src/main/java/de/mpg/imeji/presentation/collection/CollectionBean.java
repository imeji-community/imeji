/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.ContainerBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Abstract bean for all collection beans
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class CollectionBean extends ContainerBean
{
    public enum TabType
    {
        COLLECTION, PROFILE, HOME, UTIL;
    }

    private static Logger logger = Logger.getLogger(CollectionBean.class);
    private TabType tab = TabType.HOME;
    protected SessionBean sessionBean;
    private CollectionImeji collection;
    private MetadataProfile profile;
    private String id;
    private String profileId;
    private boolean selected;
    private int size = 0;
    private Navigation navigation;

    /**
     * New default {@link CollectionBean}
     */
    public CollectionBean()
    {
        collection = new CollectionImeji();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }

    /**
     * Validate whether the {@link CollectionImeji} values are correct
     * 
     * @return
     */
    public boolean valid()
    {
        if (collection.getMetadata().getTitle() == null || "".equals(collection.getMetadata().getTitle()))
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_need_title"));
            return false;
        }
        List<Person> pers = new ArrayList<Person>();
        for (Person c : collection.getMetadata().getPersons())
        {
            List<Organization> orgs = new ArrayList<Organization>();
            for (Organization o : c.getOrganizations())
            {
                if (!"".equals(o.getName()))
                {
                    orgs.add(o);
                }
            }
            if (!"".equals(c.getFamilyName()))
            {
                if (orgs.size() > 0)
                {
                    c.setOrganizations(orgs);
                    pers.add(c);
                }
                else
                {
                    BeanHelper.error(sessionBean.getMessage("error_author_need_one_organization"));
                    return false;
                }
            }
            else
            {
                BeanHelper.error(sessionBean.getMessage("error_author_need_one_family_name"));
                return false;
            }
        }
        if (pers.size() == 0)
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_need_one_author"));
            return false;
        }
        collection.getMetadata().setPersons(pers);
        return true;
    }

    @Override
    protected String getErrorMessageNoAuthor()
    {
        return "error_collection_need_one_author";
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
            getContainer().setDiscardComment(event.getNewValue().toString().trim());
        }
    }

    /**
     * getter
     * 
     * @return the tab
     */
    public TabType getTab()
    {
        if (UrlHelper.getParameterValue("tab") != null)
        {
            tab = TabType.valueOf(UrlHelper.getParameterValue("tab").toUpperCase());
        }
        return tab;
    }

    /**
     * setter
     * 
     * @param the tab to set
     */
    public void setTab(TabType tab)
    {
        this.tab = tab;
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
     * @return the selected
     */
    public boolean getSelected()
    {
        if (sessionBean.getSelectedCollections().contains(collection.getId()))
            selected = true;
        else
            selected = false;
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected)
    {
        if (selected)
        {
            if (!(sessionBean.getSelectedCollections().contains(collection.getId())))
                sessionBean.getSelectedCollections().add(collection.getId());
        }
        else
            sessionBean.getSelectedCollections().remove(collection.getId());
        this.selected = selected;
    }

    /**
     * setter
     * 
     * @param size
     */
    public void setSize(int size)
    {
        this.size = size;
    }

    /**
     * getter (get size of collection)
     * 
     * @return
     */
    public int getSize()
    {
        return size;
    }

    /**
     * True if the current {@link User} is the creator of the {@link CollectionImeji}
     * 
     * @return
     */
    public boolean getIsOwner()
    {
        if (collection != null && collection.getCreatedBy() != null && sessionBean.getUser() != null)
        {
            return collection.getCreatedBy().equals(ObjectHelper.getURI(User.class, sessionBean.getUser().getEmail()));
        }
        return false;
    }

    /**
     * release the {@link CollectionImeji}
     * 
     * @return
     */
    public String release()
    {
        CollectionController cc = new CollectionController();
        try
        {
            cc.release(collection, sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_collection_release"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_release"));
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        return "pretty:";
    }

    /**
     * Delete the {@link CollectionImeji}
     * 
     * @return
     */
    public String delete()
    {
        CollectionController cc = new CollectionController();
        try
        {
            cc.delete(collection, sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_collection_delete"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_delete"));
            logger.error("Error delete collection", e);
        }
        return "pretty:collections";
    }

    /**
     * Discard the {@link CollectionImeji} of this {@link CollectionBean}
     * 
     * @return
     * @throws Exception
     */
    public String withdraw() throws Exception
    {
        CollectionController cc = new CollectionController();
        try
        {
            cc.withdraw(collection, sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_collection_withdraw"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_withdraw"));
            BeanHelper.error(e.getMessage());
            logger.error("Error discarding collection:", e);
        }
        return "pretty:";
    }

    /**
     * getter
     * 
     * @return
     */
    public MetadataProfile getProfile()
    {
        return profile;
    }

    /**
     * setter
     * 
     * @param profile
     */
    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getProfileId()
    {
        return profileId;
    }

    /**
     * setter
     * 
     * @param profileId
     */
    public void setProfileId(String profileId)
    {
        this.profileId = profileId;
    }

    public String getPageUrl()
    {
        return navigation.getCollectionUrl() + id;
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.presentation.beans.ContainerBean#getType()
     */
    @Override
    public String getType()
    {
        return CONTAINER_TYPE.COLLECTION.name();
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.presentation.beans.ContainerBean#getContainer()
     */
    @Override
    public Container getContainer()
    {
        return collection;
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
