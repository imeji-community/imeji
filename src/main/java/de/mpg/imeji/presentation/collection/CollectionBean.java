/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.security.Authorization;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.image.ThumbnailBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Abstract bean for all collection beans
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class CollectionBean
{
    public enum TabType
    {
        COLLECTION, PROFILE, HOME;
    }

    private static Logger logger = Logger.getLogger(CollectionBean.class);
    private TabType tab = TabType.HOME;
    private SessionBean sessionBean;
    private CollectionImeji collection;
    private MetadataProfile profile;
    private String id;
    private String profileId;
    private int authorPosition;
    private int organizationPosition;
    private List<SelectItem> profilesMenu = new ArrayList<SelectItem>();
    private boolean selected;
    private int size = 0;


    /**
     * New default {@link CollectionBean}
     */
    public CollectionBean()
    {
        collection = new CollectionImeji();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
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

    /**
     * Add a new author to the {@link CollectionImeji}
     * 
     * @return
     */
    public String addAuthor()
    {
        List<Person> c = (List<Person>)collection.getMetadata().getPersons();
        Person p = ImejiFactory.newPerson();
        p.setPos(authorPosition + 1);
        c.add(authorPosition + 1, p);
        return getNavigationString();
    }

    /**
     * Remove an author of the {@link CollectionImeji}
     * 
     * @return
     */
    public String removeAuthor()
    {
        List<Person> c = (List<Person>)collection.getMetadata().getPersons();
        if (c.size() > 1)
            c.remove(authorPosition);
        else
            BeanHelper.error(sessionBean.getMessage("error_collection_need_one_author"));
        return getNavigationString();
    }

    /**
     * Add an organization to an author of the {@link CollectionImeji}
     * 
     * @return
     */
    public String addOrganization()
    {
        List<Person> persons = (List<Person>)collection.getMetadata().getPersons();
        List<Organization> orgs = (List<Organization>)persons.get(authorPosition).getOrganizations();
        Organization o = ImejiFactory.newOrganization();
        o.setPos(organizationPosition + 1);
        orgs.add(organizationPosition + 1, o);
        return getNavigationString();
    }

    /**
     * Remove an organization to an author of the {@link CollectionImeji}
     * 
     * @return
     */
    public String removeOrganization()
    {
        List<Person> persons = (List<Person>)collection.getMetadata().getPersons();
        List<Organization> orgs = (List<Organization>)persons.get(authorPosition).getOrganizations();
        if (orgs.size() > 1)
            orgs.remove(organizationPosition);
        else
            BeanHelper.error(sessionBean.getMessage("error_author_need_one_organization"));
        return getNavigationString();
    }

    /**
     * return the navigation value (according to jsf2 standard) of the current page
     * 
     * @return
     */
    protected abstract String getNavigationString();

    /**
     * getter
     * 
     * @return
     */
    public int getAuthorPosition()
    {
        return authorPosition;
    }

    /**
     * setter
     * 
     * @param pos
     */
    public void setAuthorPosition(int pos)
    {
        this.authorPosition = pos;
    }

    /**
     * @return the collectionPosition
     */
    public int getOrganizationPosition()
    {
        return organizationPosition;
    }

    /**
     * @param collectionPosition the collectionPosition to set
     */
    public void setOrganizationPosition(int organizationPosition)
    {
        this.organizationPosition = organizationPosition;
    }

    /**
     * Listener for the discard comment
     * 
     * @param event
     */
    public void discardCommentListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null)
        {
            collection.setDiscardComment(event.getNewValue().toString());
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
     * @param tab the tab to set
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
     * Return the 5 {@link ThumbnailBean} for the {@link CollectionImeji} startpage. Use a specific sparql query with a
     * limit, to increase performance
     * 
     * @return
     * @throws Exception
     */
    public List<ThumbnailBean> getThumbnails() throws Exception
    {
        if (collection != null)
        {
            List<String> uris = new ArrayList<String>();
            for (URI uri : getCollection().getImages())
            {
                uris.add(uri.toString());
            }
            ItemController ic = new ItemController(sessionBean.getUser());
            return ImejiFactory.imageListToThumbList(ic.loadItems(uris, 13, 0));
        }
        return null;
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

    /**
     * true if current {@link User} can UPDATE the {@link CollectionImeji}
     * 
     * @return
     */
    public boolean isEditable()
    {
        Security security = new Security();
        return security.check(OperationsType.UPDATE, sessionBean.getUser(), collection);
    }

    /**
     * true if current {@link User} can VIEW the {@link CollectionImeji}
     * 
     * @return
     */
    public boolean isVisible()
    {
        Security security = new Security();
        return security.check(OperationsType.READ, sessionBean.getUser(), collection);
    }

    /**
     * true if current {@link User} can DELETE the {@link CollectionImeji}
     * 
     * @return
     */
    public boolean isDeletable()
    {
        Security security = new Security();
        return security.check(OperationsType.DELETE, sessionBean.getUser(), collection);
    }

    /**
     * true if current {@link User} can EDIT the {@link MetadataProfile} of the {@link CollectionImeji}
     * 
     * @return
     */
    public boolean isProfileEditor()
    {
        Security security = new Security();
        return security.check(OperationsType.UPDATE, sessionBean.getUser(), profile);
    }

    /**
     * True if the current {@link User} is SYSADMIN
     * 
     * @return
     */
    public boolean isAdmin()
    {
        Authorization auth = new Authorization();
        return auth.isContainerAdmin(sessionBean.getUser(), collection);
    }
}
