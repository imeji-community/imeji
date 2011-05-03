package de.mpg.imeji.collection;

import java.util.ArrayList;
import java.util.List;
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
            this.setProfile(profileController.retrieve( this.getCollection().getProfile()));
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
            if (!"".equalsIgnoreCase(personString)) personString += " / ";
        	personString += p.getFamilyName() + ", " + p.getGivenName() + " ";
        }
        return personString;
    }

    public String getSmallDescription()
    {
        if(this.getCollection().getMetadata().getDescription() == null) return "No Description";
    	if (this.getCollection().getMetadata().getDescription().length() > 100)
        {
            return this.getCollection().getMetadata().getDescription().substring(0, 100) + "...";
        }
        else
        {
            return this.getCollection().getMetadata().getDescription();
        }
    }
    
    public String getFormattedDescription()
    {
        if (this.getCollection().getMetadata().getDescription() == null) return "";
    	return this.getCollection().getMetadata().getDescription().replaceAll("\n", "<br/>");
    }
}
