/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user;

import java.util.Collection;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import de.mpg.imeji.presentation.user.util.PasswordGenerator;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.j2j.exceptions.NotFoundException;

/**
 * Java Bean for the Create new user page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UserCreationBean
{
    private User user;
    private SessionBean sb;
    private boolean sendEmail = false;
    private static Logger logger = Logger.getLogger(UserCreationBean.class);

    /**
     * Construct new bean
     */
    public UserCreationBean()
    {
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        this.setUser(new User());
    }
    
    /**
     * Method called when user create a new user
     * 
     * @return
     * @throws Exception
     */
    public String create()
    {

        if (!isValidEmail(user.getEmail()))
        {
            BeanHelper.error(sb.getMessage("error_user_email_not_valid"));
        }
        else
        {
            try
            {
                if (userAlreadyExists(user))
                {
                    BeanHelper.error(sb.getMessage("error_user_already_exists"));
                }
                else
                {
                    String password = createNewUser();
                    if (sendEmail)
                    {
                        sendNewAccountEmail(password);
                    }
                    logger.info("New user created: " + user.getEmail());
                    BeanHelper.info(sb.getMessage("success_user_create"));
                }
            }
            catch (Exception e)
            {
                BeanHelper.error(sb.getMessage("error") + ": " + e);
            }
        }
        return "pretty:";
    }

    /**
     * Create a new {@link User}
     * 
     * @throws Exception
     */
    private String createNewUser() throws Exception
    {
        UserController uc = new UserController(sb.getUser());
        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.generatePassword();
        user.setEncryptedPassword(StringHelper.convertToMD5(password));
        user.setGrants(AuthorizationPredefinedRoles.defaultUser(user.getId().toString(), user.isAllowedToCreateCollection()));
        uc.create(user);
        return password;
    }

    /**
     * Is true if the Email is valid
     * 
     * @return
     */
    public static boolean isValidEmail(String email)
    {
        String regexEmailMatch = "([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)";
        return email.matches(regexEmailMatch);
    }

    /**
     * True if the {@link User} exists
     * 
     * @return
     * @throws Exception
     */
    public static boolean userAlreadyExists(User user) throws Exception
    {
        try
        {
            SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
            UserController uc = new UserController(session.getUser());
            uc.retrieve(user.getEmail());
            return true;
        }
        catch (NotFoundException e)
        {
            logger.info("User not found: " + user.getEmail());
            return false;
        }
    }

    /**
     * Send an email to the current {@link User}
     * 
     * @param password
     */
    public void sendNewAccountEmail(String password)
    {
        EmailClient emailClient = new EmailClient();
        EmailMessages emailMessages = new EmailMessages();
        try
        {
            emailClient.sendMail(user.getEmail(), null, emailMessages.getEmailSubject(true),
                    emailMessages.getNewAccountMessage(password, user.getEmail(), user.getName()));
        }
        catch (Exception e)
        {
            logger.error("Error sending email", e);
            BeanHelper.error(sb.getMessage("error") + ": Email not sent");
        }
    }

    /**
     * setter
     * 
     * @param user
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /**
     * getter
     * 
     * @return
     */
    public User getUser()
    {
        return user;
    }

    /**
     * getter - True if the selectbox "send email to user" has been selected
     * 
     * @return
     */
    public boolean isSendEmail()
    {
        return sendEmail;
    }

    /**
     * setter
     * 
     * @param sendEmail
     */
    public void setSendEmail(boolean sendEmail)
    {
        this.sendEmail = sendEmail;
    }
}
