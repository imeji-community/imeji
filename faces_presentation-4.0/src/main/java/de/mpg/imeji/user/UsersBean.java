package de.mpg.imeji.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.security.Security;
import de.mpg.jena.vo.User;

public class UsersBean 
{
	private List<User> users = new ArrayList<User>();
	private SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private boolean sysAdmin = false;

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

		System.out.println("sending email to " + email);

		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", "mail.mpdl.mpg.de");
		props.setProperty("mail.user", "saquet@mpdl.mpg.de");
		props.setProperty("mail.password", "");

		Session mailSession = Session.getDefaultInstance(props, null);
		Transport transport = mailSession.getTransport();

		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject("Your new password for Imeji");
		message.setContent("This is a test", "text/plain");
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		message.setFrom(new InternetAddress("saquet@mpdl.mpg.de"));
		
		transport.connect();
		transport.sendMessage(message,message.getRecipients(Message.RecipientType.TO));
		transport.close();

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
