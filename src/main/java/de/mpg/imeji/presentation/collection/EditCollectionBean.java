/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.util.LinkedList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.util.BeanHelper;

@ManagedBean(name = "EditCollectionBean")
@SessionScoped
public class EditCollectionBean extends CollectionBean
{
    private static final long serialVersionUID = 568267990816647451L;

    public EditCollectionBean()
    {
        super();
    }

    public void init() throws Exception
    {
        super.setTab(TabType.COLLECTION);
        String id = super.getId();
        if (id != null)
        {
            ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).setId(id);
            ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).init();
            setProfile(((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getProfile());
            setCollection(((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getCollection());
            LinkedList<Person> persons = new LinkedList<Person>();
            if (getCollection().getMetadata().getPersons().size() == 0)
            {
                getCollection().getMetadata().getPersons().add(new Person());
            }
            for (Person p : getCollection().getMetadata().getPersons())
            {
                LinkedList<Organization> orgs = new LinkedList<Organization>();
                for (Organization o : p.getOrganizations())
                {
                    orgs.add(o);
                }
                p.setOrganizations(orgs);
                persons.add(p);
            }
            getCollection().getMetadata().setPersons(persons);
            // set the loaded collection in the session
            ((CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class)).setActive(getCollection());
        }
        else
        {
            BeanHelper.error(sessionBean.getLabel("error") + " : no ID in URL");
        }
    }

    public String save() throws Exception
    {
        if (valid())
        {
            CollectionController collectionController = new CollectionController();
            collectionController.updateLazy(getCollection(), sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_collection_save"));
            Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
            FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .redirect(
                            navigation.getCollectionUrl() + ObjectHelper.getId(getCollection().getId()) + "/"
                                    + navigation.getInfosPath() + "?init=1");
        }
        return "";
    }

    /**
     * Return the link for the Cancel button
     * 
     * @return
     */
    public String getCancel()
    {
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        return nav.getCollectionUrl() + ObjectHelper.getId(getCollection().getId()) + "/" + nav.getInfosPath()
                + "?init=1";
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:editCollection";
    }
}
