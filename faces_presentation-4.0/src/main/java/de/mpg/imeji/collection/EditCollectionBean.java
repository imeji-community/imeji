package de.mpg.imeji.collection;

import java.util.LinkedList;

import javax.faces.context.FacesContext;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;

public class EditCollectionBean extends CollectionBean
{
    private CollectionController collectionController;
    private SessionBean sessionBean;
    private CollectionSessionBean collectionSession = null;
    private boolean init;

    public EditCollectionBean()
    {
        super();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        collectionController = new CollectionController(sessionBean.getUser());
        super.setTab(TabType.COLLECTION);
        init = UrlHelper.getParameterBoolean("init");
        super.setCollection(collectionSession.getActive());
    }

    public void init() throws Exception
    {
        if (init)
        {
            String id = super.getId();
            if (id != null)
            {
                CollectionImeji coll = collectionController.retrieve(id);
                if (coll != null)
                {
                    super.setCollection(coll);
                    LinkedList<Person> persons = new LinkedList<Person>();
                    for (Person p : super.getCollection().getMetadata().getPersons())
                    {
                        LinkedList<Organization> orgs = new LinkedList<Organization>();
                        for (Organization o : p.getOrganizations())
                        {
                            orgs.add(o);
                        }
                        p.setOrganizations(orgs);
                        persons.add(p);
                    }
                    super.getCollection().getMetadata().setPersons(persons);
                    collectionSession.setActive(super.getCollection());
                }
                else
                {
                    BeanHelper.error("Collection " + id + " not found");
                }
            }
            else
            {
                BeanHelper.error("id not found", "No parameter id found in the url");
            }
            init = false;
        }
    }

    public String save() throws Exception
    {
        if (valid())
        {
            collectionController.update(super.getCollection());
            BeanHelper.info("collection_success_save");
            Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
            FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getApplicationUri() + getCollection().getId().getPath() + "/details?init=1");
        }
        
        return "";
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:editCollection";
    }
}
