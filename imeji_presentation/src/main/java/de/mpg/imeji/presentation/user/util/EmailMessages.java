/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.user.util;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

public class EmailMessages 
{
	public String getNewAccountMessage(String password, String email, String username)
	{
		return getEmailMessage(password, email, username, "email_new_user");
	}
	
	public String getNewPasswordMessage(String password, String email, String username)
	{
		return getEmailMessage(password, email, username, "email_new_password");
	}
	
	public String getSharedCollectionMessage(String sender, String dest, String collectionName, String collectionLink)
	{
		String message = getBundle("email_shared_collection");
		
		message = message.replace("XXX_USER_NAME_XXX,", dest)
			.replace("XXX_NAME_XXX", collectionName)
			.replace("XXX_LINK_XXX", collectionLink)
			.replace("XXX_SENDER_NAME_XXX", sender);
		
		return message;
	}
	
	public String getSharedAlbumMessage(String sender, String dest, String collectionName, String collectionLink)
	{
		String message = getBundle("email_shared_album");
		
		message = message.replace("XXX_USER_NAME_XXX,", dest)
			.replace("XXX_NAME_XXX", collectionName)
			.replace("XXX_LINK_XXX", collectionLink)
			.replace("XXX_SENDER_NAME_XXX", sender);
		
		return message;
	}
	
	private String getBundle(String messageBundle)
	{
		SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		return session.getMessage(messageBundle);
	}
	
	private String getEmailMessage(String password, String email, String username, String message_bundle)
	{
		Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
		SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		String userPage = navigation.getApplicationUrl() + "user?id=" + email;

		String emailMessage = session.getMessage(message_bundle);
		
		if ("email_new_user".equals(message_bundle))
		{
			emailMessage = emailMessage.replace("XXX_LINK_TO_APPLICATION_XXX", navigation.getApplicationUrl());
		}
		
		emailMessage = emailMessage
					.replace("XXX_USER_NAME_XXX,", username)
					.replace("XXX_LOGIN_XXX", email)
					.replace("XXX_PASSWORD_XXX", password)
					.replace("XXX_LINK_TO_USER_PAGE_XXX", userPage);
			
		return emailMessage;
	}
	
	public String getEmailSubject(boolean newAccount)
	{
		SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		String emailsubject = "";
		
		if (newAccount)
		{
			emailsubject = session.getMessage("email_new_user_subject");
		}
		else
		{
			emailsubject = session.getMessage("email_new_password_subject");
		}
		
		return emailsubject;
	}
}
