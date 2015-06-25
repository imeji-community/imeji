package de.mpg.imeji.presentation.user;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.ConfigurationBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import de.mpg.imeji.presentation.util.BeanHelper;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

/**
 * Bean for registration workflow
 *
 * @author makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "RegistrationBean")
@ViewScoped
public class RegistrationBean {

    private static Logger logger = Logger.getLogger(RegistrationBean.class);

    private UserController uc = new UserController(Imeji.adminUser);

    private SessionBean sb;
    private User user = new User();


    private String token = null;
    private boolean registration_submitted = false;


    private boolean registration_success = false;
    private boolean activation_submitted = false;
    private boolean activation_success = false;
    private String activation_message;


    @PostConstruct
    public void init() {
        sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        //get token etc
        this.token = UrlHelper.getParameterValue("token");
        if (!isNullOrEmptyTrim(token)) {
            activate();
        }
    }

    public void register() {
        try {
            this.activation_submitted = false;
            this.registration_submitted = true;
            user = uc.create(user, UserController.USER_TYPE.INACTIVE);
            sendRegistrationNotification();
            this.registration_success = true;
        } catch (ImejiException e) {
            //TODO: remove
            //uc.delete(user);
            BeanHelper.error(sb.getMessage(e.getLocalizedMessage()));
        }
    }

    private void activate() {
        try {
            this.activation_submitted = true;
            this.registration_submitted = false;
            user = uc.activate(this.token);
            this.activation_success = true;
            this.activation_message = sb.getMessage("activation_success");
        } catch (ImejiException e) {
            this.activation_success = false;
            this.activation_message = e.getLocalizedMessage();

        }
    }

    /**
     * Send registration email
     */
    private void sendRegistrationNotification() {
        EmailClient emailClient = new EmailClient();
        EmailMessages emailMessages = new EmailMessages();
        try {
            //send to requester
            //TODO: send plain text password
            emailClient.sendMail(
                    user.getEmail(),
                    ConfigurationBean.getEmailServerSenderStatic(),
                    emailMessages.getEmailOnRegistrationRequest_Subject(sb),
                    emailMessages.getEmailOnRegistrationRequest_Body(user, sb));
        } catch (Exception e) {
            logger.error("Error sending email", e);
            BeanHelper.error(sb.getMessage("error") + ": Email not sent");
        }
    }


    public boolean isRegistration_submitted() {
        return registration_submitted;
    }

    public void setRegistration_submitted(boolean registration_submitted) {
        this.registration_submitted = registration_submitted;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isActivation_submitted() {
        return activation_submitted;
    }

    public void setActivation_submitted(boolean activation_submitted) {
        this.activation_submitted = activation_submitted;
    }

    public boolean isActivation_success() {
        return activation_success;
    }

    public void setActivation_success(boolean activation_success) {
        this.activation_success = activation_success;
    }

    public String getActivation_message() {
        return activation_message;
    }

    public void setActivation_message(String activation_message) {
        this.activation_message = activation_message;
    }

    public boolean isRegistration_success() {
        return registration_success;
    }

    public void setRegistration_success(boolean registration_success) {
        this.registration_success = registration_success;
    }

}
