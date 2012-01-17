/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.collection;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ObjectLoader;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.User;

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

	public void init()
	{
		try
		{
			User user = sessionBean.getUser();
			String id = super.getId();

			setCollection(ObjectLoader.loadCollection(ObjectHelper.getURI(CollectionImeji.class, id), user));

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
			if (!"".equalsIgnoreCase(personString)) personString += " / ";
			personString += p.getFamilyName() + ", " + p.getGivenName() + " ";
		}
		return personString;
	}

	public String getSmallDescription()
	{
		if(this.getCollection() == null || this.getCollection().getMetadata().getDescription() == null) return "No Description";
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
		if (this.getCollection()  == null || this.getCollection().getMetadata().getDescription() == null) return "";
		return this.getCollection().getMetadata().getDescription().replaceAll("\n", "<br/>");
	}


}
