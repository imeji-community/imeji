/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

public class UserBean 
{
	private User user;
	private String newPassword = null;
	private String repeatedPassword = null;
	private boolean isAdmin;
	private SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private String id;

	public UserBean() 
	{
		// TODO Auto-generated constructor stub
	}
	public UserBean(String email) 
	{
		id = email;
		retrieveUser();
	}

	public String getInit()
	{
		id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
		newPassword = null;
		repeatedPassword = null;
		retrieveUser();
		return "";
	}

	public void retrieveUser()
	{
		if (id != null && session.getUser() != null)
		{
			user = ObjectLoader.loadUser(id, session.getUser());

			isAdmin = (session.isAdmin() || (user.getEmail().equals(session.getUser().getEmail())));
		}
		else if (id != null && session.getUser() == null) 
		{
			LoginBean loginBean = (LoginBean) BeanHelper.getRequestBean(LoginBean.class);
			loginBean.setLogin(id);
		}
	}
	
	public void changePassword() throws Exception
	{
		if (user != null && newPassword != null && !"".equals(newPassword))
		{
			if (newPassword.equals(repeatedPassword))
			{
				user.setEncryptedPassword(UserController.convertToMD5(newPassword));
				updateUser();
				BeanHelper.info("Password changed!");
			}
			else
			{
				BeanHelper.error(session.getMessage("error_user_repeat_password"));
			}
		}
		reloadPage();
	}

	public void revokeGrant() throws IOException
	{
		String grantType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("grantType");
		String grantFor = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("grantFor");

		Collection<Grant> newGrants = new ArrayList<Grant>();

		for(Grant g : user.getGrants())
		{
			if (!g.getGrantFor().toString().equals(grantFor) && !g.asGrantType().equals(grantType))
			{
				newGrants.add(g);
			}
		}

		user.setGrants(newGrants);
		updateUser();
		BeanHelper.info("Grant revoked");
		reloadPage();
	}

	public void updateUser()
	{
		if (user != null)
		{
			UserController controller = new UserController(session.getUser());
		
			try 
			{
				controller.update(user);
			} 
			catch (Exception e) 
			{
				BeanHelper.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void reloadPage() throws IOException
	{
		Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
		FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getApplicationUrl() + "/jsf/User.xhtml?id=" + user.getEmail());
	}

	public User getUser() 
	{
		return user;
	}

	public void setUser(User user) 
	{
		this.user = user;
	}

	public String getNewPassword() 
	{
		return newPassword;
	}

	public void setNewPassword(String newPassword) 
	{
		this.newPassword = newPassword;
	}

	public String getRepeatedPassword() 
	{
		return repeatedPassword;
	}

	public void setRepeatedPassword(String repeatedPassword) 
	{
		this.repeatedPassword = repeatedPassword;
	}

	public boolean isAdmin() 
	{
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) 
	{
		this.isAdmin = isAdmin;
	}


}
