package de.mpg.imeji.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.mdProfile.MdProfileBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;

public abstract class CollectionBean
{
    public enum TabType
    {
        COLLECTION, PROFILE, HOME;
    }

    private TabType tab = TabType.HOME;
    private SessionBean sessionBean = null;
    private CollectionImeji collection = null;
    private CollectionSessionBean collectionSession = null;
    private String id = null;
    private MdProfileBean mdProfileBean = null;
    private CollectionController collectionController = null;
    private int authorPosition;
    private int organizationPosition;
    private List<SelectItem> profilesMenu = new ArrayList<SelectItem>();

    public CollectionBean()
    {
        collection = new CollectionImeji();
        mdProfileBean = new MdProfileBean();
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        collectionController = new CollectionController(sessionBean.getUser());
    }

    public boolean valid()
    {
        boolean valid = true;
        boolean hasAuthor = false;
        if (collection.getMetadata().getTitle() == null || "".equals(collection.getMetadata().getTitle()))
        {
            BeanHelper.error(sessionBean.getMessage("collection_create_error_title"));
            valid = false;
        }
        for (Person c : collection.getMetadata().getPersons())
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
                    BeanHelper.error(sessionBean.getMessage("collection_create_error_family_name"));
                    valid = false;
                }
            }
            if (!hasOrganization)
            {
                BeanHelper.error(sessionBean.getMessage("collection_create_error_organization"));
                valid = false;
            }
        }
        if (!hasAuthor)
        {
            BeanHelper.error(sessionBean.getMessage("collection_create_error_author"));
            valid = false;
        }
        return valid;
    }

    public String addAuthor()
    {
        LinkedList<Person> list =  (LinkedList<Person>)collection.getMetadata().getPersons();
        list.add(authorPosition + 1, ImejiFactory.newPerson());
        return getNavigationString();
    }

    public String removeAuthor()
    {
        if (authorPosition > 0)
        {
            LinkedList<Person> list =  (LinkedList<Person>)collection.getMetadata().getPersons();
            list.remove(authorPosition);
        }
        return getNavigationString();
    }

    public String addOrganization()
    {
        LinkedList<Person> persons =  (LinkedList<Person>)collection.getMetadata().getPersons();
        LinkedList<Organization> orgs = (LinkedList<Organization>)persons.get(authorPosition).getOrganizations();
        orgs.add(organizationPosition + 1, ImejiFactory.newOrganization());
        return getNavigationString();
    }

    public String removeOrganization()
    {
        if (organizationPosition > 0)
        {
            LinkedList<Person> persons =  (LinkedList<Person>)collection.getMetadata().getPersons();
            LinkedList<Organization> orgs = (LinkedList<Organization>)persons.get(authorPosition).getOrganizations();
            orgs.remove(organizationPosition);
        }
        return getNavigationString();
    }

    protected abstract String getNavigationString();


    public int getAuthorPosition()
    {
        return authorPosition;
    }

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
     * @return the mdProfileBean
     */
    public MdProfileBean getMdProfileBean()
    {
        return mdProfileBean;
    }

    /**
     * @param mdProfileBean the mdProfileBean to set
     */
    public void setMdProfileBean(MdProfileBean mdProfileBean)
    {
        this.mdProfileBean = mdProfileBean;
    }

    public List<SelectItem> getProfilesMenu()
    {
        return profilesMenu;
    }

    public void setProfilesMenu(List<SelectItem> profilesMenu)
    {
        this.profilesMenu = profilesMenu;
    }
    
    
}
