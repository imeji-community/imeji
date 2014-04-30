/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserGroupController;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import de.mpg.imeji.presentation.user.util.PasswordGenerator;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Java Bean for the view users page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "UsersBean")
@ViewScoped
public class UsersBean implements Serializable
{
    private static final long serialVersionUID = 909531319532057429L;
    private List<User> users;
    private UserGroup group;
    private String query;
    @ManagedProperty(value = "#{SessionBean.user}")
    private User sessionUser;
    private static Logger logger = Logger.getLogger(UserBean.class);

    /**
     * Initialize the bean
     */
    @PostConstruct
    public void init()
    {
        String q = UrlHelper.getParameterValue("q");
        query = q == null ? "" : q;
        doSearch();
        retrieveGroup();
    }

    /**
     * Trigger the search to users Groups
     */
    public void search()
    {
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        try
        {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(nav.getApplicationUrl() + "users?q=" + query);
        }
        catch (IOException e)
        {
            BeanHelper.error(e.getMessage());
            logger.error(e);
        }
    }

    /**
     * Retrieve all users
     */
    public void doSearch()
    {
        UserController controller = new UserController(sessionUser);
        users = new ArrayList<User>();
        for (User user : controller.retrieveAll(query))
        {
            users.add(user);
        }
    }

    /**
     * If the parameter group in the url is not null, try to retrieve this group. This happens when the admin want to
     * add a {@link User} to a {@link UserGroup}
     */
    public void retrieveGroup()
    {
        if (UrlHelper.getParameterValue("group") != null)
        {
            UserGroupController c = new UserGroupController();
            try
            {
                setGroup(c.read(UrlHelper.getParameterValue("group"), sessionUser));
            }
            catch (Exception e)
            {
                BeanHelper.error("error loading user group " + UrlHelper.getParameterValue("group"));
                logger.error(e);
            }
        }
    }

    /**
     * Method called when a new password is sent
     * 
     * @return
     * @throws Exception
     */
    public String sendPassword()
    {
        String email = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("email");
        PasswordGenerator generator = new PasswordGenerator();
        UserBean userBean = new UserBean(email);
        SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        try
        {
            String newPassword = generator.generatePassword();
            userBean.getUser().setEncryptedPassword(StringHelper.convertToMD5(newPassword));
            userBean.updateUser();
            sendEmail(email, newPassword, userBean.getUser().getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        BeanHelper.info(session.getMessage("success_email"));
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
            BeanHelper.error("Error: Email not sent");
        }
    }

    /**
     * Delete a {@link User}
     * 
     * @return
     */
    public String deleteUser()
    {
        String email = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("email");
        UserController controller = new UserController(sessionUser);
        try
        {
            controller.delete(ObjectLoader.loadUser(email, sessionUser));
        }
        catch (Exception e)
        {
            BeanHelper.error("Error Deleting user");
            logger.error("Error Deleting user", e);
        }
        doSearch();
        return "";
    }

    /**
     * Add a {@link User} to a {@link UserGroup} and then redirect to the {@link UserGroup} page
     * 
     * @param user
     */
    public String addToGroup()
    {
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        String email = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("email");
        try
        {
            UserController uc = new UserController(sessionUser);
            User user = uc.retrieve(email);
            group.getUsers().add(user.getId());
            UserGroupController c = new UserGroupController();
            c.update(group, sessionUser);
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(nav.getApplicationUrl() + "usergroup?id=" + group.getId());
        }
        catch (Exception e)
        {
            BeanHelper.error(e.getMessage());
        }
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
     * @return the group
     */
    public UserGroup getGroup()
    {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(UserGroup group)
    {
        this.group = group;
    }

    /**
     * @return the sessionUser
     */
    public User getSessionUser()
    {
        return sessionUser;
    }

    /**
     * @param sessionUser the sessionUser to set
     */
    public void setSessionUser(User sessionUser)
    {
        this.sessionUser = sessionUser;
    }

    /**
     * @return the query
     */
    public String getQuery()
    {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query)
    {
        this.query = query;
    }
}
