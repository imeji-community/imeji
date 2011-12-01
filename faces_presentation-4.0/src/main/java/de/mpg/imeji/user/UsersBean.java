package de.mpg.imeji.user;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.user.util.EmailClient;
import de.mpg.imeji.user.util.EmailMessages;
import de.mpg.imeji.user.util.PasswordGenerator;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.security.Security;
import de.mpg.jena.vo.User;

public class UsersBean 
{
	private List<User> users = new ArrayList<User>();
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
		userBean.getUser().setEncryptedPassword(UserController.convertToMD5(newPassword));
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
