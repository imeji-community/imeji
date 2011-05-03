package de.mpg.imeji.user;


import java.net.URI;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.GrantController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.security.Security;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.vo.Container;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.User;

public class SharingManager 
{
	public SharingManager() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean share(Object o, User owner, String email, GrantType role)
	{
		if (isAdmin(o, owner))
		{
			try
			{
				User target = getUser(owner, email);
				GrantController gc = new GrantController(owner);
				
				URI uri = null;
				if (o instanceof Container)
				{
					uri = ((Container)o).getId();
				}
				else if (o instanceof MetadataProfile)
				{
					 uri = ((MetadataProfile) o).getId();
				}
				
				gc.addGrant(target, new Grant(role,uri));
				return true;
			}
			catch (NotFoundException e)
			{
				BeanHelper.error("User " + email  + " doesn't have an account in Imeji! Sharing works only for Imeji Users.");
			}
			catch (Exception e) 
			{
				BeanHelper.error(e.getMessage());
				return false;
			}
		}
		return false;
	}
	
	public User getUser(User user, String email)
	{
		UserController uc = new UserController(user);
		return uc.retrieve(email);
	}
	
	public boolean isAdmin(Object o, User user)
	{
		Security security = new Security();
		return security.check(OperationsType.DELETE, user, o);
	}
	
}
