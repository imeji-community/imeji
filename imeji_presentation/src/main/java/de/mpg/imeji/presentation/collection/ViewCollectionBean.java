/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectLoader;

public class ViewCollectionBean extends CollectionBean
{
    private SessionBean sessionBean = null;
    private List<Person> persons = null;
    private static Logger logger = Logger.getLogger(ViewCollectionBean.class);

    public ViewCollectionBean(CollectionImeji coll)
    {
        super(coll);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    public ViewCollectionBean()
    {
        super();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    public void createBigCollection()
    {
        List<Item> l = new ArrayList<Item>();
        for (int i = 0; i < 100; i++)
        {
            de.mpg.imeji.logic.vo.Item item = ImejiFactory.newItem(getCollection());
            item.setCollection(this.getCollection().getId());
            item.setFullImageUrl(URI.create("http://imeji.org/item/test"));
            item.setThumbnailImageUrl(URI.create("http://imeji.org/item/test"));
            item.setWebImageUrl(URI.create("http://imeji.org/item/test"));
            item.setVisibility(Visibility.PUBLIC);
            item.setFilename("Test image");
            l.add(item);
        }
        ItemController itemController = new ItemController(sessionBean.getUser());
        try
        {
            itemController.create(l, getCollection().getId());
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void init()
    {
        try
        {
            User user = sessionBean.getUser();
            String id = super.getId();
            setCollection(ObjectLoader.loadCollectionLazy(ObjectHelper.getURI(CollectionImeji.class, id), user));
            if (getCollection() != null && getCollection().getId() != null)
            {
                ItemController ic = new ItemController(sessionBean.getUser());
                setSize(ic.countImagesInContainer(getCollection().getId(), new SearchQuery()));
            }
            if (getCollection() != null)
            {
                setProfile(ObjectLoader.loadProfile(getCollection().getProfile(), user));
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
                getCollection().getMetadata().setPersons(persons);
            }
        }
        catch (Exception e)
        {
            BeanHelper.error(e.getMessage());
            logger.error("Error init of collection home page", e);
        }
        // createBigCollection();
    }

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
            if (!"".equalsIgnoreCase(personString))
                personString += " / ";
            personString += p.getFamilyName() + ", " + p.getGivenName() + " ";
        }
        return personString;
    }

    public String getSmallDescription()
    {
        if (this.getCollection() == null || this.getCollection().getMetadata().getDescription() == null)
            return "No Description";
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
        if (this.getCollection() == null || this.getCollection().getMetadata().getDescription() == null)
            return "";
        return this.getCollection().getMetadata().getDescription().replaceAll("\n", "<br/>");
    }
}
