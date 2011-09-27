package de.mpg.imeji.user;


import java.net.URI;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.ViewCollectionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ObjectLoader;
import de.mpg.jena.controller.GrantController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.security.Authorization;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Container;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.User;

public class SharingManager 
{
	
	public boolean share(Object o, User owner, String email, GrantType role, boolean replaceGrant)
	{
		if (isShareUser(o, owner))
		{
			try
			{
				User target = ObjectLoader.loadUser(email, owner);
				GrantController gc = new GrantController(owner);
				
				//for (Grant  g : target.getGrants()) System.out.println(g.getGrantType() + " - " + g.getGrantFor());
				
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
				
				if (replaceGrant) target = gc.updateGrant(target, ng);
				else target = gc.addGrant(target, ng);
				
				if(o instanceof CollectionImeji)
				{
					try 
					{
						Authorization authorization = new Authorization();
						if (GrantType.CONTAINER_EDITOR.equals(role))
						{
							if (replaceGrant) gc.updateGrant(target, new Grant(GrantType.PROFILE_ADMIN, ((CollectionImeji) o).getProfile()));
							else gc.addGrant(target, new Grant(GrantType.PROFILE_ADMIN, ((CollectionImeji) o).getProfile()));
						}
						else if (!authorization.is(GrantType.PROFILE_ADMIN, target, ((CollectionImeji) o).getProfile())
								&& !authorization.is(GrantType.PROFILE_EDITOR, target, ((CollectionImeji) o).getProfile()))
						{
							if (replaceGrant) gc.updateGrant(target, new Grant(GrantType.PROFILE_VIEWER, ((CollectionImeji) o).getProfile()));
							else gc.addGrant(target, new Grant(GrantType.PROFILE_VIEWER, ((CollectionImeji) o).getProfile()));
						}
					} 
					catch (Exception e) 
					{
						gc.removeGrant(target, ng);
						throw e;
					}
				}
				else if (o instanceof MetadataProfile && false)
				{	
					try 
					{
						URI uriCol = ObjectHelper.getURI(CollectionImeji.class, ((SharingBean)BeanHelper.getSessionBean(SharingBean.class)).getColId());
						gc.updateGrant(target, new Grant(GrantType.PRIVILEGED_VIEWER, uriCol));
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
				e.printStackTrace();
				return false;
			}
		}
		BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("error_share_not_enough_priviliges"));
		return false;
	}
	
	
	public boolean isShareUser(Object o, User user)
	{
		Security security = new Security();
		return security.check(OperationsType.UPDATE, user, o);
	}
	
}
