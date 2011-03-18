package de.mpg.jena.controller;

import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.User;

public class GrantController extends ImejiController
{
	public GrantController(User user) 
	{
		super(user);
	}
	
	public void addGrant(User user, Grant grant) throws Exception
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
	}
	
	public void removeGrant(User user, Grant grant) throws Exception
	{
		if (hasGrant(user, grant))
		{
			user.getGrants().remove(grant);
			saveUser(user);
		}
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
