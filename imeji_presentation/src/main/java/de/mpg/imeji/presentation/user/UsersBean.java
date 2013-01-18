/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.user;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import de.mpg.imeji.presentation.user.util.PasswordGenerator;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

public class UsersBean 
{
	private List<User> users;
	private SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private boolean sysAdmin = false;
	private static Logger logger = Logger.getLogger(UserBean.class);

	public UsersBean()
	{
		if (session.isAdmin())
		{
			sysAdmin = true;
			retrieveUsers();			
		}
	}

	public void retrieveUsers()
	{
		UserController controller = new UserController(session.getUser());
		Security security = new Security();
		
		users = new ArrayList<User>();
		for (User user : controller.retrieveAll())
		{
			if (!security.isSysAdmin(user))
			{
				users.add(user);
			}
		}
	}

	public String sendPassword() throws Exception
	{
		String email = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("email");

		PasswordGenerator generator = new PasswordGenerator();
		String newPassword = generator.generatePassword();

		UserBean userBean = new UserBean(email);
		userBean.getUser().setEncryptedPassword(StringHelper.convertToMD5(newPassword));
		userBean.updateUser();

		sendEmail(email, newPassword, userBean.getUser().getName());
		
		return "";
	}

	public void sendEmail(String email, String password, String username)
	{
		EmailClient emailClient = new EmailClient();
		EmailMessages emailMessages = new EmailMessages();

		try 
		{
			emailClient.sendMail(email, null, session.getMessage("email_new_password_subject")
					, emailMessages.getNewPasswordMessage(password, email, username));
		} 
		catch (Exception e) 
		{
			logger.error("Error sending email", e);
			BeanHelper.error(session.getMessage("error") + ": Email not sent");
		} 
	}
	
	public String deleteUser()
	{
		String email = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("email");

		UserController controller = new UserController( session.getUser());
		
		try 
		{
			controller.delete(ObjectLoader.loadUser(email, session.getUser()));
		} 
		catch (Exception e) 
		{
			BeanHelper.error("Error Deleting user");
			logger.error("Error Deleting user", e);
		}
		
		retrieveUsers();
		
		return "";
	}


	public List<User> getUsers() 
	{
		return users;
	}

	public void setUsers(List<User> users) 
	{
		this.users = users;
	}

	public boolean isSysAdmin() 
	{
		return sysAdmin;
	}

	public void setSysAdmin(boolean sysAdmin) 
	{
		this.sysAdmin = sysAdmin;
	}
}
