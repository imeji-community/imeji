package de.mpg.imeji.collection;

import java.util.LinkedList;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.mdProfile.MdProfileBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.CollectionVO;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;

public abstract class CollectionBean
{
    public enum TabType
    {
        COLLECTION, PROFILE, HOME;
    }

    protected TabType tab = TabType.HOME;
    protected SessionBean sessionBean = null;
    protected CollectionVO collection = null;
    protected CollectionSessionBean collectionSession = null;
    protected String id = null;
    protected MdProfileBean mdProfileBean = null;
    protected CollectionController collectionController = null;
    private int authorPosition;
    private int organizationPosition;

    public CollectionBean()
    {
        collection = new CollectionVO();
        mdProfileBean = new MdProfileBean();
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        // sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        // collectionController = new CollectionController(sessionBean.getUser());
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
        System.out.println("ADD AUTHOR");
        collection.addPerson(authorPosition + 1, ImejiFactory.newPersonVO());
        return getNavigationString();
    }

    public String removeAuthor()
    {
        if (authorPosition > 0)
        {
            collection.removePerson(authorPosition);
        }
        return getNavigationString();
    }

    public String addOrganization()
    {
        ((LinkedList)collection.getPerson(authorPosition).getOrganizations()).add(organizationPosition + 1,
                ImejiFactory.newPersonVO().getOrganizations().get(0));
        return getNavigationString();
    }

    public String removeOrganization()
    {
        if (organizationPosition > 0)
        {
            ((LinkedList)collection.getPerson(authorPosition).getOrganizations()).remove(organizationPosition);
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
    public CollectionVO getCollection()
    {
        return collection;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(CollectionVO collection)
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
}
