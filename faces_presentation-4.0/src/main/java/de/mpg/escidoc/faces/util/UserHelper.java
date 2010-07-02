package de.mpg.escidoc.faces.util;

import javax.naming.InitialContext;

import de.escidoc.schemas.useraccount.x07.UserAccountDocument.UserAccount;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class UserHelper 
{

	/**
	 * 
	 * @param userHandle
	 * @param grant
	 */
	public static void addGrantToUser(String userHandle, GrantVO grant)
	{
		try 
		{
			String userXml = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentUser();
			
			AccountUserVO user = getAccounUserVO(userHandle);
			
			addGrantToUser(user, grant, userHandle);
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error creating new Grants to creator: " + e);
		}
	}
	
	public static void addGrantToUser(AccountUserVO user, GrantVO grant, String userHandle)
	{
		try 
		{			
			String grantXml = getXmlTransforming().transformToGrant(grant);
			
			ServiceLocator.getUserGroupHandler(userHandle).createGrant(user.getUserid(), grantXml);
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error creating new Grants to creator: " + e);
		}
	}
	
	public static AccountUserVO getAccounUserVO(String userHandle) 
	{
		try
		{
			String userXml = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentUser();
			
			return getXmlTransforming().transformToAccountUser(userXml);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error retrieving user Account: " + e);
		}
	}
	
	public static XmlTransforming getXmlTransforming() throws Exception
	{
		InitialContext context = new InitialContext();
		XmlTransforming xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
		
		return xmlTransforming;
	}
}
