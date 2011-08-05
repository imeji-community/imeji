package de.mpg.imeji.user;


import java.net.URI;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.GrantController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.security.Security;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.vo.CollectionImeji;
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
		if (isShareUser(o, owner))
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
				
				if(o instanceof CollectionImeji)
				{
					gc.addGrant(target, new Grant(GrantType.PROFILE_VIEWER, ((CollectionImeji) o).getProfile()));
				}
				
				return true;
			}
			catch (NotFoundException e)
			{
				BeanHelper.error(email  + " " + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("error_share_email_not_imeji_account"));
			}
			catch (Exception e) 
			{
				BeanHelper.error(e.getMessage());
				return false;
			}
		}
		BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("error_share_not_enough_priviliges"));
		return false;
	}
	
	public User getUser(User user, String email)
	{
		UserController uc = new UserController(user);
		return uc.retrieve(email);
	}
	
	public boolean isShareUser(Object o, User user)
	{
		Security security = new Security();
		return security.check(OperationsType.UPDATE, user, o);
	}
	
}
