package de.mpg.imeji.user;


import java.net.URI;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionBean;
import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.collection.ViewCollectionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.GrantController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.security.Security;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.util.ObjectHelper;
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
				
				Grant ng = new Grant(role,uri);
				gc.addGrant(target, ng);
				
				if(o instanceof CollectionImeji)
				{
					try 
					{
						gc.addGrant(target, new Grant(GrantType.PROFILE_VIEWER, ((CollectionImeji) o).getProfile()));
					} 
					catch (Exception e) 
					{
						gc.removeGrant(target, ng);
						throw e;
					}
				}
				else if (o instanceof MetadataProfile)
				{	
					try 
					{
						URI uriCol = ObjectHelper.getURI(CollectionImeji.class, ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getId());
						gc.addGrant(target, new Grant(GrantType.PRIVILEGED_VIEWER, uriCol));
					} 
					catch (Exception e) 
					{
						gc.removeGrant(target, ng);
						throw e;
					}
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
