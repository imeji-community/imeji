package de.mpg.imeji.presentation.user;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.User;
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
    private User user;


    private String token = null;
    private boolean registration_submitted = false;
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

    private String register() {
        try {
            this.activation_submitted = false;
            this.registration_submitted = true;
            user = uc.create(user, UserController.USER_TYPE.INACTIVE);
            sendRegistrationEmail();
        } catch (ImejiException e) {
            BeanHelper.error(sb.getMessage(e.getLocalizedMessage()));
        }
        return "pretty:";
    }

    private String activate() {
        //retrieve
        try {
            this.activation_submitted = true;
            this.registration_submitted = false;
            user = uc.activate(this.token);
            this.activation_success = true;
            this.activation_message = sb.getMessage("activation_success");
        } catch (ImejiException e) {
            this.activation_success = false;
            this.activation_message = e.getLocalizedMessage();
            //TODO: redirect?
        }
        return "pretty:";
    }

    /**
     * Send registered email
     */
    private void sendRegistrationEmail() {
        EmailClient emailClient = new EmailClient();
        EmailMessages emailMessages = new EmailMessages();
        try {
            //send to requester
            emailClient.sendMail(
                    user.getEmail(),
                    null, //from support?
                    emailMessages.getEmailOnRegistrationRequest_Subject(sb),
                    emailMessages.getEmailOnRegistrationRequest_Body(user, user.getRegistrationToken(), sb));
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

}
