/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import de.mpg.imeji.presentation.user.util.GrantHelper;
import de.mpg.imeji.presentation.user.util.PasswordGenerator;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * Java Bean for the view users page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UsersBean
{
    private List<User> users;
    private SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    private boolean sysAdmin = false;
    private static Logger logger = Logger.getLogger(UserBean.class);

    /**
     * Initialize the bean
     */
    public UsersBean()
    {
        if (session.isAdmin())
        {
            sysAdmin = true;
            retrieveUsers();
        }
    }

    /**
     * Retrieve all users
     */
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

    /**
     * Method called when a new password is sent
     * 
     * @return
     * @throws Exception
     */
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

    /**
     * Send an Email to a {@link User} for its new password
     * 
     * @param email
     * @param password
     * @param username
     */
    public void sendEmail(String email, String password, String username)
    {
        EmailClient emailClient = new EmailClient();
        EmailMessages emailMessages = new EmailMessages();
        try
        {
            emailClient.sendMail(email, null, emailMessages.getEmailSubject(false),
                    emailMessages.getNewPasswordMessage(password, email, username));
        }
        catch (Exception e)
        {
            logger.error("Error sending email", e);
            BeanHelper.error(session.getMessage("error") + ": Email not sent");
        }
    }

    public String grantsString (Grant grant)
    {
    	String grantStr = "";   	
        String role = GrantHelper.grantString(grant);
    	
        grantStr = role + " "+  ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("for") + " " + grant.getGrantFor();
        
    	return grantStr;
    }
    
    /**
     * Delete a {@link User}
     * 
     * @return
     */
    public String deleteUser()
    {
        String email = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("email");
        UserController controller = new UserController(session.getUser());
        
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

    /**
     * getter
     * 
     * @return
     */
    public List<User> getUsers()
    {
        return users;
    }

    /**
     * setter
     * 
     * @param users
     */
    public void setUsers(List<User> users)
    {
        this.users = users;
    }

    /**
     * getter
     * 
     * @return
     */
    public boolean isSysAdmin()
    {
        return sysAdmin;
    }

    /**
     * setter
     * 
     * @param sysAdmin
     */
    public void setSysAdmin(boolean sysAdmin)
    {
        this.sysAdmin = sysAdmin;
    }
}
