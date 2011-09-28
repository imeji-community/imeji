package de.mpg.imeji.collection;

import javax.faces.context.FacesContext;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.CollectionController;

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
            	((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).setId(id);
            	((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).init();
            	setProfile(((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getProfile());
            	setCollection(((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getCollection());
            	
//                CollectionImeji coll = ObjectLoader.loadCollection(ObjectHelper.getURI(CollectionImeji.class, id), sessionBean.getUser());
//                if (coll != null)
//                {
//                    super.setCollection(coll);
//                    LinkedList<Person> persons = new LinkedList<Person>();
//                    for (Person p : super.getCollection().getMetadata().getPersons())
//                    {
//                        LinkedList<Organization> orgs = new LinkedList<Organization>();
//                        for (Organization o : p.getOrganizations())
//                        {
//                            orgs.add(o);
//                        }
//                        p.setOrganizations(orgs);
//                        persons.add(p);
//                    }
//                    super.getCollection().getMetadata().setPersons(persons);
//                    collectionSession.setActive(super.getCollection());
//                }
            }
            else
            {
                BeanHelper.error(sessionBean.getLabel("error") + " : no ID in URL");
            }
            init = false;
        }
    }

    public String save() throws Exception
    {
        if (valid())
        {
            collectionController.update(super.getCollection());
            BeanHelper.info(sessionBean.getMessage("success_collection_save"));
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
