package de.mpg.imeji.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import thewebsemantic.LocalizedString;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.User;

public class ViewCollectionBean extends CollectionBean
{
    private CollectionController collectionController = null;
    private SessionBean sessionBean = null;
    private List<Person> persons = null;

    public ViewCollectionBean(CollectionImeji coll)
    {
        super(coll);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        collectionController = new CollectionController(sessionBean.getUser());
    }

    public ViewCollectionBean()
    {
        super();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        collectionController = new CollectionController(sessionBean.getUser());
    }

    public void init()
    {
        try
        {
            User user = sessionBean.getUser();
            collectionController = new CollectionController(user);
            String id = super.getId(); 
            super.setCollection(collectionController.retrieve(id));
            super.setTab(TabType.COLLECTION);
            persons = new ArrayList<Person>();
            for (Person p : super.getCollection().getMetadata().getPersons())
            {
                List<Organization> orgs = new ArrayList<Organization>();
                for (Organization o : p.getOrganizations())
                {
                    orgs.add(o);
                }
                p.setOrganizations(orgs);
                persons.add(p);
            }
            this.getCollection().getMetadata().setPersons(persons);
            
            ProfileController profileController = new ProfileController(sessionBean.getUser());
            this.getCollection().setProfile(profileController.retrieve(this.getCollection().getProfile().getId()));
        }
        catch (Exception e)
        {
           throw new RuntimeException(e);
        }
    }

    /**
     * Transform Persons from HashSet to List TODO: Check why persons are return in hashSet
     * 
     * @return
     */
    public List<Person> getPersons()
    {
        return persons;
    }

    public void setPersons(List<Person> persons)
    {
        this.persons = persons;
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:viewCollection";
    }

    public String getPersonString()
    {
        String personString = "";
        for (Person p : getCollection().getMetadata().getPersons())
        {
            personString += p.getFamilyName() + ", " + p.getGivenName();
        }
        return personString;
    }
    
    
    
}
