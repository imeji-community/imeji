package de.mpg.imeji.collection;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.mdProfile.MdProfileBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.CollectionVO;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;

public class CollectionBean
{
    public enum TabType
    {
        COLLECTION, PROFILE, HOME;
    }

    protected TabType tab = TabType.HOME;
    protected SessionBean sessionBean = null;
    protected CollectionVO collection = null;
    protected String id = null;
    protected MdProfileBean mdProfileBean = null;
    protected CollectionController collectionController = null;

    public CollectionBean()
    {
        collection = new CollectionVO();
        mdProfileBean = new MdProfileBean();
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
