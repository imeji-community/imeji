package de.mpg.jena.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.jena.ImejiBean2RDF;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.User;

public class GrantController extends ImejiController
{
	public GrantController(User user) 
	{
		super(user);
	}
	
	public User addGrant(User user, Grant grant) throws Exception
	{
		if (!isValid(grant)) 
		{
			throw new RuntimeException("Grant: " + grant.getGrantType() + " for " + grant.getGrantFor() + " not valid");
		}
		if (!hasGrant(user, grant) )
		{
			user.getGrants().add(grant);
			saveUser(user);
		}
		else throw new RuntimeException("User " + user.getEmail() + " is already " + grant.getGrantType() + " for " + grant.getGrantFor());
		return user;
	}
	
	/**
	 * Replace the grant of one user for one object with the new Grant. It means only one grant for one object pro user.
	 * @param user
	 * @param grant
	 * @throws Exception 
	 */
	public User updateGrant(User user, Grant grant) throws Exception
	{
		if (!isValid(grant)) 
		{
			throw new RuntimeException("Grant: " + grant.getGrantType() + " for " + grant.getGrantFor() + " not valid");
		}
		
		Collection<Grant> newGrants = new ArrayList<Grant>();
		newGrants.add(grant);

		for (Grant g : user.getGrants())
		{
			if (!g.getGrantFor().equals(grant.getGrantFor()))
			{
				newGrants.add(g);
			}
		}
		user.setGrants(newGrants);
		saveUser(user);
		return user;
	}
	
	public User removeGrant(User user, Grant grant) throws Exception
	{
		if (hasGrant(user, grant))
		{
			user.getGrants().remove(grant);
			saveUser(user);
		}
		return user;
	}
	
	public User removeAllGrantsFor(User user, URI uri) throws Exception
	{
		for (int i = 0 ; i < user.getGrants().size(); i++)
		{
			if (uri != null && uri.equals(((List<Grant>)user.getGrants()).get(i).getGrantFor()))
			{
				((List<Grant>)user.getGrants()).remove(i);
			}
		}
		saveUser(user);
		return user;
	}
	
	public boolean hasGrant(User user, Grant grant)
	{
		for (Grant g : user.getGrants()) 
		{
			if (compare(g, grant)) 
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean compare(Grant grant1, Grant grant2)
	{
		if (isValid(grant1) && isValid(grant2)) 
		{
			return (grant1.getGrantFor().equals(grant2.getGrantFor())
					&& grant1.getGrantType().equals(grant2.getGrantType()));
		}
		return false;
	}
	
	public boolean isValid(Grant grant)
	{
		return (grant != null && grant.getGrantFor() != null && grant.getGrantType() != null); 
		
	}
	
	private void saveUser(User user) throws Exception
	{
		UserController uc = new UserController(user);
		uc.update(user);
	}

	@Override
	protected String getSpecificQuery() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getSpecificFilter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
